sap.ui.define([
    "sap/ui/model/odata/v2/ODataModel"
], function (ODataModel) {
    "use strict";
    return ODataModel.extend("ys.first.app.model.App", {
        constructor: function () {
            // ODataModel.apply(this, arguments);

            // call parent constructor
            ODataModel.call(this, "http://localhost:8080/mavenproject/maintenancev2.svc/", {
                json: true,
                useBatch: false,
                defaultBindingMode: sap.ui.model.BindingMode.TwoWay,
                defaultUpdateMethod: sap.ui.model.odata.UpdateMethod.Put,
                loadMetadataAsync: false,
                tokenHandling: true,
                headers: {SessionId: "123"}
            });

            this.attachRequestFailed(function (oEvent) {
                debugger;
                jQuery.sap.require("sap.m.MessageBox");
                sap.m.MessageBox.error(oEvent.getParameters().response.responseText);
            });

            // bind events
            var binding = new sap.ui.model.Binding(this, "/", this.getContext("/"));
            binding.attachChange(function (oEvent) {
                //var parameters = oEvent.getParameters();
                //debugger;
                //try {
                //    oEvent.getSource().getBindingContext().setProperty("TenantId", 42);
                    //Property("TenantId");
                //}
                //catch (err) {
                //    
                //}
            });

        },
        _setProperty: function (entity, id, property, value) {
            this.setProperty("/" + entity + "(" + id.toString() + ")" + "/" + property, value);
        }
    });
});