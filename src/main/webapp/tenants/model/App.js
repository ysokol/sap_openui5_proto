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
                defaultUpdateMethod: sap.ui.model.odata.UpdateMethod.Put
            });
            // bind events
            var binding = new sap.ui.model.Binding(this, "/", this.getContext("/"));
            binding.attachChange(function (oEvent) {
                //var parameters = oEvent.getParameters();
                //oEvent.getSource().getBindingContext().getProperty("TenantId");
            });
        },
        _setProperty: function (entity, id, property, value) {
            this.setProperty("/" + entity + "(" + id.toString() + ")" + "/" + property, value);
        } 
    });
});