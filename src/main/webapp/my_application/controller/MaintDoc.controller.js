sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ys/first/app/model/App",
    "ys/first/app/model/formatter"
], function (Controller, Model, formatter) {
    "use strict";

    return Controller.extend("ys.first.app.controller.MaintDoc", {
        formatter: formatter,
        onInit: function () {
            this._oModel = new Model();
            this._oObjectPage = this.byId("objectPageLayoutId");
            this._oGlobalModel = this.getOwnerComponent().getModel("globalProperties");
            this._oEventBus = sap.ui.getCore().getEventBus();
            this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
            this._oInitialLoadFinishedDeferred = jQuery.Deferred();

            this.getView().setModel(this._oModel);
            sap.ui.getCore().setModel(this._oModel);
            this._oRouter.attachRoutePatternMatched(this.onRouteMatched, this);
            this._oModel.attachMetadataLoaded(function () {
                this._oInitialLoadFinishedDeferred.resolve();
            }, this)

        },
        onRouteMatched: function (oEvent) {
            var oParameters = oEvent.getParameters();

            jQuery.when(this._oInitialLoadFinishedDeferred).then(jQuery.proxy(function () {
                if (oParameters.name !== "maintenanceDoc") {
                    return;
                }

                this._oGlobalModel.setProperty("/maintenanceDoc", oParameters.arguments.id);
                this._oEventBus.publish("MaintenanceDoc", "onMaintenanceDocOpened", {
                    maintenanceDoc: oParameters.arguments.id
                });

                var maintenanceDoc = {
                    MaintenanceDoc: oParameters.arguments.id
                }
                this._oObjectPage.bindObject(this._oModel.createKey("/MaintenanceDocs", maintenanceDoc));
            }, this));

        },
        onAddLine: function (oEvent) {
            debugger;
            var collectionPath = oEvent.getSource().getParent().getParent().getBindingContext().getPath() + 
                   "/" + oEvent.getSource().getParent().getParent().getBindingPath("items");
            var that = this;
            this._oModel.callFunction("/MaintenanceDocAddLine", {
                method: "POST",
                urlParameters: {
                    MaintenanceDoc: this._oGlobalModel.getProperty("/maintenanceDoc")
                },
                success: function () {
                    that._oModel.refresh();
                }
            });
        },
        onDeleteLine: function (oEvent) {
            this._oModel.remove(oEvent.getSource().getParent().getBindingContext().getPath());
        },
    });

});