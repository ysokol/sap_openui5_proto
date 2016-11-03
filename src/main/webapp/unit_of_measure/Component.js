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
					pattern: "client/{id}/uom",
					name: "uom",
					target: "uom"
				}, {
					pattern: "client",
					name: "client",
					target: "client"
				}, {
					pattern: "login",
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
						viewName: "MaintItem",
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