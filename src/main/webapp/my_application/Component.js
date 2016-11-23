sap.ui.define([
	"sap/ui/core/UIComponent",
	"./controller/Application"
], function(UIComponent, Application) {
	"use strict";
	return UIComponent.extend("ys.first.app.Component", {
		metadata: {
			rootView: "ys.first.app.view.RoutingRoot",
			routing: {
				config: {
					routerClass: "sap.m.routing.Router",
					viewType: "XML",
					viewPath: "ys.first.app.view",
					controlId: "routingRoot",
					controlAggregation: "pages",
					clearTarget: false,
					transition: "slide",
					bypassed: {
						target: "notFound"
					}
				},
				routes: [{
					pattern: "MaintenancePlan/{id}",
					name: "maintenancePlan",
					target: "maintenancePlan"
				}, {
					pattern: "MaintenanceSchedule/{id}",
					name: "maintenanceSchedule",
					target: "maintenanceSchedule"
				}, {
					pattern: "MaintenanceDoc/{id}",
					name: "maintenanceDoc",
					target: "maintenanceDoc"
				}, {
					pattern: "Client",
					name: "client",
					target: "client"
				}, {
					pattern: "Login",
					name: "login",
					target: "login"
				}, {
					pattern: "",
					name: "home",
					target: "home"
				} ],
				targets: {
					uom: {
						viewName: "App",
						//controlAggregation: "detailPages",
						parent: "client"
					},
					client: {
						viewName: "Client"//,
						//controlAggregation: "masterPages"
					},
					home: {
						viewName: "MaintPlanList",
						viewLevel: 1
					},
					maintenancePlan: {
						viewName: "MaintPlan",
						viewLevel: 1
					},
					maintenanceSchedule: {
						viewName: "MaintSchedule",
						viewLevel: 1
					},
					maintenanceDoc: {
						viewName: "MaintDoc",
						viewLevel: 1
					},
				    login: {
						viewName: "Login",
						viewLevel: 1
					},
					notFound: {
						viewName: "NotFound",
						viewLevel: 1
						//parent: "client"
					}
				}
			}
		},
		/*metadata: {
			manifest: "json"
		},*/
		init: function() {
			var oApplication = new Application(this);
			oApplication.init();
			// call the init function of the parent
			UIComponent.prototype.init.apply(this, arguments);
			
			// create the views based on the url/hash
            this.getRouter().initialize();
			
			this.getRouter().navTo("login");
		}
	});
});