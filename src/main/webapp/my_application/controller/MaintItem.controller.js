sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ys/first/app/model/App",
    "ys/first/app/model/formatter"
], function (Controller, Model, formatter) {
    "use strict";

    return Controller.extend("ys.first.app.controller.MaintItem", {
        formatter: formatter,
        onInit: function () {
            this._oModel = new Model();
            this._oTable = this.byId("myTreeTable");
            this._oGlobalModel = this.getOwnerComponent().getModel("globalProperties");
            this._oEventBus = sap.ui.getCore().getEventBus();
            this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
            this._oInitialLoadFinishedDeferred = jQuery.Deferred();

            this.getView().setModel(this._oModel);
            sap.ui.getCore().setModel(this._oModel);
            this._oTable.attachToggleOpenState(this.onExpandCollapse, this);
            this._oTable.attachRowSelectionChange(this.onRowSelected, this);
            this._oGlobalModel.attachPropertyChange(this.onGlobalModelChange, this);
            this._oRouter.attachRoutePatternMatched(this.onRouteMatched, this);

            this._oTable.attachBusyStateChanged(function () {

                this._oInitialLoadFinishedDeferred.resolve();
                //this._expandAccordingToModel();
            }, this);

            this._oTable.getBinding("rows").attachDataReceived(function () {
                setTimeout(function (oTable, oController) {
                    oController._expandAccordingToModel();
                    //oTable.expandToLevel(99);
                }, 100, this._oTable, this);
            }, this);

            /*this._oModel.attachRequestCompleted(function() {
             debugger;
             }, this);*/

            /*var fOldInvalidate = this._oTable.__proto__.invalidate;
             this._oTable.__proto__.invalidate = function(oOrigin) {
             fOldInvalidate(oOrigin);
             debugger;
             };*/

            // >>>>>>>>>>>> !!!!!! Try this function for expand\collaps Wait for the list to be loaded once !!!!!!!!!! <<<<<<<<<<

            /*var oContent = this._oTable.getBinding("content");
             oContent.attachDataReceived(function(oData) {
             this._oTable.setBusy(false);
             }.ind(this));*/

            /*this.waitForInitialListLoading(function() {
             that._expandAccordingToModel();
             });*/
        },
        onRouteMatched: function (oEvent) {
            var oParameters = oEvent.getParameters();
            jQuery.when(this._oInitialLoadFinishedDeferred).then(jQuery.proxy(function () {
                // when detail navigation occurs, update the binding context
                if (oParameters.name !== "maintenancePlan") {
                    return;
                }

                this._oGlobalModel.setProperty("/maintenancePlan", oParameters.arguments.id);
                this._oEventBus.publish("MaintenancePlan", "onMaintenancePlanEditOpened", {
                    maintenanceItemId: oParameters.arguments.id
                });

                var aFilters = [];
                var oFilter = new sap.ui.model.Filter("MaintenancePlan", "EQ", oParameters.arguments.id);
                aFilters.push(oFilter);
                this._oTable.getBinding("rows").filter(aFilters, sap.ui.model.FilterType.Application);
            }, this));
        },
        onGlobalModelChange: function (oEvent) {

        },
        onExpandCollapse: function (oEvent) {
            //collapsed, expanded, leaf.
            try {
                if (oEvent.getSource().getParent().getModel().getProperty(oEvent.getParameters().rowContext.sPath + "/NodeDrillState") !== "leaf") {
                    if (oEvent.getParameter("expanded")) {
                        oEvent.getSource().getParent().getModel().setProperty(oEvent.getParameters().rowContext.sPath + "/NodeDrillState", "expanded");
                        oEvent.getSource().getParent().getModel().setProperty(oEvent.getParameters().rowContext.sPath + "/UpdateFlag", "U");
                    } else {
                        oEvent.getSource().getParent().getModel().setProperty(oEvent.getParameters().rowContext.sPath + "/NodeDrillState", "collapsed");
                        oEvent.getSource().getParent().getModel().setProperty(oEvent.getParameters().rowContext.sPath + "/UpdateFlag", "U");
                    }
                }
            } catch (err) {

            }
        },
        _expandAccordingToModel: function () {
            try {
                for (var i = 0; i < this._oTable.getRows().length; i++) {
                    switch (this._oTable.getRows()[i].getBindingContext().getProperty("NodeDrillState")) {
                        case "expanded":
                            if (!this._oTable.isExpanded(i)) {
                                this._oTable.expand(i);
                            }
                            break;
                        case "collapsed":
                            if (this._oTable.isExpanded(i)) {
                                this._oTable.collapse(i);
                            }
                            break;
                        case "leaf":
                            break;
                    }
                }
            } catch (err) {
            }
        },
        onRowSelected: function (oEvent) {
            try {
                this._oGlobalModel.setProperty("/maintenanceItemId", oEvent.getParameters().rowContext.getProperty("MaintenanceItemId"));
                this._oGlobalModel.setProperty("/maintenanceItemDescr", oEvent.getParameters().rowContext.getProperty("Descr"));
                this._oEventBus.publish("MaintenancePlan", "onMaintenanceItemSelected", {
                    maintenanceItemId: oEvent.getParameters().rowContext.getProperty("MaintenanceItemId")
                });
            } catch (err) {
                this._oGlobalModel.setProperty("/maintenanceItemId", -1);
                this._oEventBus.publish("MaintenancePlan", "onMaintenanceItemSelected", {
                    maintenanceItemId: -1
                });
            }
        },
        onMenuAction: function (oEvent) {
            switch (oEvent.getParameter("item").getKey()) {
                case "addSubItem":
                    this.onAddSubItem(oEvent);
                    break;
                case "addItem":
                    this.onAddItemAfter(oEvent);
                    break;
                case "editItem":
                    this.onEditItem(oEvent);
                    break;
                case "deleteItem":
                    this.onDeleteItem(oEvent);
                    break;
            }
        },
        onSaveAll: function (oEvent) {
            this._oModel.submitChanges();
        },
        onAddItemAfter: function (oEvent) {
            this._oModel.callFunction("/CreateItem", {
                method: "POST",
                urlParameters: {
                    MaintenanceItemId: oEvent.getSource().getParent().getBindingContext().getProperty("MaintenanceItemId"),
                    MaintenancePlan: this._oGlobalModel.getProperty("/maintenancePlan"),
                    Descr: "New Item"
                }
            });
        },
        onAddItem: function (oEvent) {
            this._oModel.callFunction("/CreateItem", {
                method: "POST",
                urlParameters: {
                    MaintenanceItemId: 0,
                    MaintenancePlan: this._oGlobalModel.getProperty("/maintenancePlan"),
                    Descr: "New Item"
                }
            });
        },
        onEditItem: function (oEvent) {
            if (!this._oEditDialog) {
                this._oEditDialog = new sap.ui.xmlfragment("ys.first.app.view.MaintItemEdit", this);
                this.getView().addDependent(this._oEditDialog);
            }
            this._oEditDialog.bindObject(oEvent.getSource().getParent().getBindingContext().getPath());
            this._oEditDialog.open();
        },
        onAddSubItem: function (oEvent) {
            this._oModel.callFunction("/CreateSubItem", {
                method: "POST",
                urlParameters: {
                    ParentId: oEvent.getSource().getParent().getBindingContext().getProperty("MaintenanceItemId"),
                    MaintenancePlan: this._oGlobalModel.getProperty("/maintenancePlan"),
                    Descr: "New SubItem"
                }
            });
        },
        onExpandAll: function (oEvent) {
            this._expandAccordingToModel();
        },
        onDeleteItem: function (oEvent) {
            this._oModel.remove(oEvent.getSource().getParent().getBindingContext().getPath());
        },
        onEditSubmit: function (oEvent) {
            this._oEditDialog.close();
        },
        onEditCancel: function (oEvent) {
            this._oModel.resetChanges([this._oEditDialog.getBindingContext().getPath()]);
            this._oEditDialog.close();
        }
    });
});