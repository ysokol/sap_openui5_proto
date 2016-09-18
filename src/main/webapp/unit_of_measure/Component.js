sap.ui.define([
    "sap/ui/core/UIComponent",
    "./controller/Application"
], function (UIComponent, Application) {
    "use strict";
    return UIComponent.extend("ys.first.app.Component", {
        metadata: {
            rootView: "ys.first.app.view.Root"
        },
        /*metadata: {
			manifest: "json"
		},*/
        init: function () {
        	var oApplication = new Application(this);
			oApplication.init();
            // call the init function of the parent
            UIComponent.prototype.init.apply(this, arguments);
        }
    });
});