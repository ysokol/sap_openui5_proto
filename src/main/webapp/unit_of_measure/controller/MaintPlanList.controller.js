sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"ys/first/app/model/App",
	"ys/first/app/model/formatter"
], function(Controller, Model, formatter) {
	"use strict";
	return Controller.extend("ys.first.app.controller.MaintPlanList", {
		formatter: formatter,
		onInit: function() {
			this._oModel = new Model();
			this._oGlobalModel = this.getOwnerComponent().getModel("globalProperties");
			this._oTable = this.byId("maintenancePlanTableId");
			this._oEventBus = sap.ui.getCore().getEventBus();
			this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);

			this.getView().setModel(this._oModel);
			sap.ui.getCore().setModel(this._oModel);

		},

		onExit: function() {

		},

		onNavigation: function(oEvent) {
			if (oEvent.getSource().getBindingContext().getProperty("MaintenancePlan")) {
				this._oRouter.navTo("maintenancePlan", {
					id: oEvent.getSource().getBindingContext().getProperty("MaintenancePlan")
				});
			}
		},
		
		onViewSchedule: function(oEvent) {
			if (oEvent.getSource().getBindingContext().getProperty("MaintenancePlan")) {
				this._oRouter.navTo("maintenanceSchedule", {
					id: oEvent.getSource().getBindingContext().getProperty("MaintenancePlan")
				});
			}
		},

		onAddRecord: function(oEvent) {
			this._oModel.create("/MaintenancePlans", {
				MaintenancePlan: "NEW",
				Descr: "Maintenance Plan Description"
			});
		},

		onSaveAll: function(oEvent) {
			this._oModel.submitChanges();
		}

	});
});