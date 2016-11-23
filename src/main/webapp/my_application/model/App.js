sap.ui.define([
	"sap/ui/model/odata/v2/ODataModel"
], function(ODataModel) {
	"use strict";
	return ODataModel.extend("ys.first.app.model.App", {
		constructor: function() {
			// ODataModel.apply(this, arguments);

			// call parent constructor
			ODataModel.call(this, "http://localhost:8080/mavenproject/maintenancev2.svc/", {
				json: true,
				useBatch: false,
				defaultBindingMode: sap.ui.model.BindingMode.TwoWay,
				defaultUpdateMethod: sap.ui.model.odata.UpdateMethod.Put,
				loadMetadataAsync: false,
				tokenHandling: true //,
					//headers: {SessionId: "123"}
			});

			this.attachRequestFailed(function(oEvent) {
				jQuery.sap.require("sap.m.MessageBox");
				sap.m.MessageBox.error(oEvent.getParameters().response.responseText);
			});

			this.attachPropertyChange(this.onOropertyChange, this);
			
			
			// bind events
			/*var binding = new sap.ui.model.odata.v2.ODataContextBinding(this, "/", this.getContext("/"));
			binding.attachChange(function(oEvent) {
				debugger;
				try {
				    oEvent.getSource().getBindingContext().getProperty();
				}
				catch (err) {
				    
				}
			}, this);*/
		},
		onOropertyChange: function(oEvent) {
			try {
				if (oEvent.getParameters().path !== "UpdateFlag") {
					this.setProperty(oEvent.getParameters().context.getPath() + "/UpdateFlag", "");
					for (var key in this.getProperty(oEvent.getParameters().context.getPath())) {
						if (key !== "UpdateFlag") {
							if (this.getOriginalProperty(oEvent.getParameters().context.getPath() + "/" + key) !==
								this.getProperty(oEvent.getParameters().context.getPath() + "/" + key)) {
								this.setProperty(oEvent.getParameters().context.getPath() + "/UpdateFlag", "U");
								break;
							}
						}
					}
				}
			} catch (err) {

			}
		},
		_setProperty: function(entity, id, property, value) {
			this.setProperty("/" + entity + "(" + id.toString() + ")" + "/" + property, value);
		}
	});
});