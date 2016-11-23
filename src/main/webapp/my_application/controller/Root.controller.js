sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ys/first/app/model/App",
    "ys/first/app/model/formatter"
], function (Controller, Model, formatter) {
    "use strict";

    return Controller.extend("ys.first.app.controller.Root", {
        formatter: formatter,
        onInit: function () {
            //this._oModel = new Model();
            //this.getView().setModel(this._oModel);
            //sap.ui.getCore().setModel(this._oModel);

        }
    });
});