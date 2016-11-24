sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ys/first/app/model/App",
    "ys/first/app/model/formatter"
], function (Controller, Model, formatter) {
    "use strict";
    return Controller.extend("ys.first.app.controller.MaintOperation", {
        formatter: formatter,
        onInit: function () {
            this._oModel = new Model();
            this._oGlobalModel = this.getOwnerComponent().getModel("globalProperties");
            this._oTable = this.byId("maintOperationTableId");
            this._oEventBus = sap.ui.getCore().getEventBus();

            this.getView().setModel(this._oModel);
            sap.ui.getCore().setModel(this._oModel);

            this._oEventBus.subscribe("MaintenancePlan", "onMaintenanceItemSelected", this.onMaintenanceItemSelected, this);
        },
        onExit: function () {
            if (this._oDialog) {
                this._oDialog.destroy();
            }
        },
        onMaintenanceItemSelected: function (channel, oEvent, data) {
            var aFilters = [];
            var oFilter = new sap.ui.model.Filter("MaintenanceItemId", "EQ", data.maintenanceItemId);
            aFilters.push(oFilter);
            this._oTable.getBinding("items").filter(aFilters);
        },
        onTokenChange: function (oEvent) {
            /*if (oEvent.getParameters().token && oEvent.getParameters().token.getKey() !== "") {
             debugger;
             var newStrategyPackage = {
             MaintenanceOperationId: oEvent.getSource().getParent().getBindingContext().getProperty("MaintenanceOperationId"),
             Strategy: "TIME",
             StrategyPackage: oEvent.getParameters().token.getKey()
             };
             var propertyLinksPath = oEvent.getSource().getParent().getBindingContextPath() + "/$links/MaintenanceOperationPackageDetails";
             var collection = "/MaintenanceOperationPackages";
             var key = this._oModel.createKey(collection, newStrategyPackage);
             var linkKey = this._oModel.createKey(propertyLinksPath, newStrategyPackage);
             switch (oEvent.getParameters().type) {
             case "added":
             if (!this._oModel.getProperty(key)) {
             this._oModel.create(propertyLinksPath, { uri: key } );
             }
             break;
             case "removed":
             if (this._oModel.getProperty(key)) {
             this._oModel.remove(linkKey);
             }
             break;
             }
             }*/

            try {
                if (oEvent.getParameters().token &&
                        oEvent.getParameters().token.getKey() !== "" &&
                        oEvent.getSource().getParent().getBindingContext().getProperty("MaintenanceOperationId")) {
                    debugger;
                    var newStrategyPackage = {
                        MaintenanceOperationId: oEvent.getSource().getParent().getBindingContext().getProperty("MaintenanceOperationId"),
                        Strategy: "",
                        StrategyPackage: oEvent.getParameters().token.getKey()
                    };
                    var propertyCollection = "";
                    var collection = "";
                    if (oEvent.getSource().getId().includes("timeMultiInputId")) {
                        newStrategyPackage.Strategy = "TIME";
                        propertyCollection = "/MaintenanceOperationTimePackageDetails";
                        collection = "/MaintenanceOperationTimePackages";
                    } else if (oEvent.getSource().getId().includes("distMultiInputId")) {
                        newStrategyPackage.Strategy = "DIST";
                        propertyCollection = "/MaintenanceOperationDistPackageDetails";
                        collection = "/MaintenanceOperationDistPackages";
                    }
                    var key = this._oModel.createKey(collection, newStrategyPackage);
                    var propertyLinksPath = oEvent.getSource().getParent().getBindingContextPath() + "/$links" + propertyCollection;
                    var linkKey = this._oModel.createKey(propertyLinksPath, newStrategyPackage);
                    switch (oEvent.getParameters().type) {
                        case "added":
                            if (!this._oModel.getProperty(key)) {
                                //this._oModel.createEntry(collection, { properties: newStrategyPackage });
                                this._oModel.create(collection, newStrategyPackage);

                            }
                            break;
                        case "removed":
                            if (this._oModel.getProperty(key)) {
                                this._oModel.remove(linkKey);
                            }
                            break;
                    }
                }
            } catch (err) {

            }
        },
        onAddOperation: function (oEvent) {
            this._oModel.create("/MaintenanceOperations", {
                MaintenanceItemId: this._oGlobalModel.getProperty("/maintenanceItemId"),
                Descr: "New Operation"
            });
        },
        onSaveAll: function (oEvent) {
            this._oModel.submitChanges();
        }

    });
});