sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"ys/first/app/model/App",
	"ys/first/app/model/formatter"
], function(Controller, Model, formatter) {
	"use strict";

	return Controller.extend("ys.first.app.controller.MaintSchedule", {
		formatter: formatter,
		/**
		 * Called when a controller is instantiated and its View controls (if available) are already created.
		 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
		 * @memberOf ys.first.app.src.main..unit_of_measure.view.MaintenanceSchedule
		 */
		onInit: function() {
			this._oModel = new Model();
			this._oTable = this.byId("maintenanceScheduleTableId");
			this._oGlobalModel = this.getOwnerComponent().getModel("globalProperties");
			this._oEventBus = sap.ui.getCore().getEventBus();
			this._oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			//this._oInitialLoadFinishedDeferred = jQuery.Deferred();

			this.getView().setModel(this._oModel);
			sap.ui.getCore().setModel(this._oModel);
			//this._oTable.attachToggleOpenState(this.onExpandCollapse, this);
			//this._oTable.attachRowSelectionChange(this.onRowSelected, this);
			//this._oGlobalModel.attachPropertyChange(this.onGlobalModelChange, this);
			this._oRouter.attachRoutePatternMatched(this.onRouteMatched, this);
		},

		onRouteMatched: function(oEvent) {
			var oParameters = oEvent.getParameters();
			
			if (oParameters.name !== "maintenanceSchedule") {
				return;
			}

			this._oGlobalModel.setProperty("/maintenancePlan", oParameters.arguments.id);
			/*this._oEventBus.publish("MaintenancePlan", "onMaintenancePlanEditOpened", {
				maintenanceItemId: oParameters.arguments.id
			});

			var aFilters = [];
			var oFilter = new sap.ui.model.Filter("MaintenancePlan", "EQ", oParameters.arguments.id);
			aFilters.push(oFilter);
			this._oTable.getBinding("rows").filter(aFilters, sap.ui.model.FilterType.Application);*/
		},

		onReschedule: function(oEvent) {
			this._oModel.callFunction("/Reschedule", {
				method: "POST",
				urlParameters: {
					MaintenancePlan: this._oGlobalModel.getProperty("/maintenancePlan")
				}
			});
			this._oModel.refresh();
		}

		/**
		 * Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
		 * (NOT before the first rendering! onInit() is used for that one!).
		 * @memberOf ys.first.app.src.main..unit_of_measure.view.MaintenanceSchedule
		 */
		//	onBeforeRendering: function() {
		//
		//	},

		/**
		 * Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
		 * This hook is the same one that SAPUI5 controls get after being rendered.
		 * @memberOf ys.first.app.src.main..unit_of_measure.view.MaintenanceSchedule
		 */
		//	onAfterRendering: function() {
		//
		//	},

		/**
		 * Called when the Controller is destroyed. Use this one to free resources and finalize activities.
		 * @memberOf ys.first.app.src.main..unit_of_measure.view.MaintenanceSchedule
		 */
		//	onExit: function() {
		//
		//	}

	});

});