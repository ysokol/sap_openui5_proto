sap.ui.define([
    "sap/ui/core/UIComponent",
    "sap/ui/model/resource/ResourceModel"
], function (UIComponent, ResourceModel) {
    "use strict";
    return UIComponent.extend("ys.first.app.Component", {
        metadata: {
            rootView: "ys.first.app.view.App"
        },
        init: function () {
            // call the init function of the parent
            UIComponent.prototype.init.apply(this, arguments);
            // set i18n model
            var i18nModel = new ResourceModel({
                bundleName: "ys.first.app.i18n.i18n"
            });
            this.setModel(i18nModel, "i18n");
        }
    });
});