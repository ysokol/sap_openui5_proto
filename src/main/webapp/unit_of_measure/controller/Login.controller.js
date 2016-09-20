sap.ui.define([
    "sap/ui/core/mvc/Controller"
], function (Controller) {
    "use strict";
    return Controller.extend("ys.first.app.controller.Login", {
        onInit: function () {
            //this._oModel = new Model();
            //this.getView().setModel(this._oModel);
            //sap.ui.getCore().setModel(this._oModel);

        },
        onLogin: function (oEvent) {
        	sap.ui.core.UIComponent.getRouterFor(this).navTo("home");
        }
    });
});