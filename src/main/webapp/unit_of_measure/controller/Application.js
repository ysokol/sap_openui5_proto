sap.ui.define([
	"sap/ui/base/Object",
	"sap/ui/Device",
	"sap/ui/model/resource/ResourceModel",
	"sap/ui/model/odata/v2/ODataModel",
	"sap/ui/model/json/JSONModel",
	"sap/ui/model/BindingMode",
	"sap/ui/core/routing/History"
], function(Object, Device, ResourceModel, ODataModel, JSONModel, BindingMode, History) {
	"use strict";

	return Object.extend("ys.first.app.controller.Application", {
		constructor: function(oComponent) {
			this._oComponent = oComponent;
		},
		
		init: function() {
			// set the globalProperties model
			this._oGlobalModel = new JSONModel({
				application: this,
				tenantId: 42,
				client: "010"
			});
			this._oGlobalModel.setDefaultBindingMode(BindingMode.TwoWay);
			this._oComponent.setModel(this._oGlobalModel, "globalProperties");
		}
	});
});