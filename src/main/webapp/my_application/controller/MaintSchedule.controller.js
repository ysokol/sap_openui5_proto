sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ys/first/app/model/App",
    "ys/first/app/model/formatter"
], function (Controller, Model, formatter) {
    "use strict";
    return Controller.extend("ys.first.app.controller.MaintSchedule", {
        formatter: formatter,
        /**
         * Called when a controller is instantiated and its View controls (if available) are already created.
         * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
         * @memberOf ys.first.app.src.main..unit_of_measure.view.MaintenanceSchedule
         */
        onInit: function () {
            this._oModel = new Model();
            this._oTable = this.byId("maintenanceScheduleTableId");
            this._oGlobalModel = this.getOwnerComponent().getModel("globalProperties");
            this._oEventBus = sap.ui.getCore().getEventBus();
            this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
            this._oInitialLoadFinishedDeferred = jQuery.Deferred();
            this.getView().setModel(this._oModel);
            sap.ui.getCore().setModel(this._oModel);
            //this._oTable.attachToggleOpenState(this.onExpandCollapse, this);
            //this._oTable.attachRowSelectionChange(this.onRowSelected, this);
            //this._oGlobalModel.attachPropertyChange(this.onGlobalModelChange, this);            
            this._oRouter.attachRoutePatternMatched(this.onRouteMatched, this);
            this._oTable.attachUpdateFinished(function () {
                this._oInitialLoadFinishedDeferred.resolve();
            }, this);
        },
        onRouteMatched: function (oEvent) {
            var oParameters = oEvent.getParameters();
            if (oParameters.name !== "maintenanceSchedule") {
                return;
            }

            this._oGlobalModel.setProperty("/maintenancePlan", oParameters.arguments.id);
            this._oEventBus.publish("MaintenanceSchedule", "onMaintenanceScheduleOpened", {
                maintenanceItemId: oParameters.arguments.id
            });
            jQuery.when(this._oInitialLoadFinishedDeferred).then(jQuery.proxy(function () {
                var aFilters = [];
                var oFilter = new sap.ui.model.Filter("MaintenancePlan", "EQ", oParameters.arguments.id);
                aFilters.push(oFilter);
                this._oTable.getBinding("items").filter(aFilters, sap.ui.model.FilterType.Application);
            }, this));
        },
        onMenuAction: function (oEvent) {
            switch (oEvent.getParameter("item").getKey()) {
                case "keyConfirm":
                    this.onConfirmSingle(oEvent);
                    break;
                case "keyCancel":
                    this.onCancelSingle(oEvent);
                    break;
            }
        },
        onReschedule: function (oEvent) {
            var that = this;
            this._oModel.callFunction("/Reschedule", {
                method: "POST",
                urlParameters: {
                    MaintenancePlan: this._oGlobalModel.getProperty("/maintenancePlan")
                },
                success: function () {
                    that._oModel.refresh(true, true);
                }
            });
        },
        onConfirmOpearations: function (oEvent) {
            var that = this;
            this._oModel.callFunction("/ConfirmScheduledOperations", {
                method: "POST",
                urlParameters: {
                    MaintenancePlan: this._oGlobalModel.getProperty("/maintenancePlan"),
                    MaintenanceDoc: 1,
                    MaintenanceDocLine: 1
                },
                success: function () {
                    that.onRefresh();
                }
            });
        },
        onConfirmSingle: function (oEvent) {
            var that = this;
            this._oModel.callFunction("/ConfirmScheduledOperations", {
                method: "POST",
                urlParameters: {
                    MaintenanceDate: oEvent.getSource().getBindingContext().getProperty("MaintenanceDate"),
                    MaintenanceMileage: oEvent.getSource().getBindingContext().getProperty("MaintenanceMileage"),
                    MaintenancePlan: that._oGlobalModel.getProperty("/maintenancePlan"),
                    MaintenanceDoc: oEvent.getSource().getBindingContext().getProperty("MaintenanceDoc"),
                    MaintenanceDocLine: 1
                },
                success: function (oData, oResponse) {
                    that._oModel.resetChanges();
                    that.onRefresh();
                    jQuery.sap.require("sap.m.MessageToast");
                    sap.m.MessageToast.show("Maintenance Document Posted #" + oResponse.data.ConfirmScheduledOperations);
                }
            });
        },
        onSaveAll: function (oEvent) {
            this._oModel.submitChanges();
        },
        onMaintenanceDocPress: function (oEvent) {
            this._oRouter.navTo("maintenanceDoc", {
                id: oEvent.getSource().getParent().getBindingContext().getProperty("MaintenanceDoc")
            });
        },
        onRefresh: function (oEvent) {
            this._oModel.refresh(true, true);
        },
        onResetAllChanges: function (oEvent) {
            this._oModel.resetChanges();
            this._oModel.refresh(true, true);
        }

        /**
         * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
         * (NOT before the first rendering! onInit() is used for that one!).
         * @memberOf ys.first.app.src.main..unit_of_measure.view.MaintenanceSchedule
         */
        //	onBeforeRendering: function() {
        //
        //	},

        /**
         * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
         * This hook is the same one that SAPUI5 controls get after being rendered.
         * @memberOf ys.first.app.src.main..unit_of_measure.view.MaintenanceSchedule
         */
        //	onAfterRendering: function() {
        //
        //	},

        /**
         * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
         * @memberOf ys.first.app.src.main..unit_of_measure.view.MaintenanceSchedule
         */
        //	onExit: function() {
        //
        //	}

    });
});