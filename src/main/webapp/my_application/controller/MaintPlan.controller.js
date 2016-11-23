sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ys/first/app/model/App",
    "ys/first/app/model/formatter"
], function (Controller, Model, formatter) {
    "use strict";

    return Controller.extend("ys.first.app.controller.MaintPlan", {
        formatter: formatter,
        onInit: function () {
            debugger;
            //this._oModel = new Model();
            this.byId("MySplitAppId").addStyleClass("wrapperShell");      
            //sap.ui.getCore().setModel(this._oModel);

        }
    });
});