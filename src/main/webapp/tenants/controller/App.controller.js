sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ys/first/app/model/App"
], function (Controller, Model) {
    "use strict";

    return Controller.extend("ys.first.app.controller.App", {
        onInit: function () {
            this._oModel = new Model();
            this._oTable = this.byId("tenantsTable");
            this.getView().setModel(this._oModel);
            this.byId("savButton").setEnabled(false);
        },
        onBtnAddRow: function (oEvent) {
            this._oModel.create("/Tenants", {
                TenantId: 0, 
                Description: "<New tenant>"
            });
            //this._oModel.refresh();
        },
        onBtnCpyRow: function (oEvent) {
            if (this._oTable.getSelectedItem() === null) {
                alert("No record selected!");
                return;
            }
            this._oModel.create("/Tenants", {
                TenantId: 0, 
                Description: this._oTable.getSelectedItem().getBindingContext().getProperty("Description")
            });
            //this._oModel.refresh();
        },
        onBtnDelRow: function (oEvent) {
            if (this._oTable.getSelectedItem() === null) {
                alert("No record selected!");
                return;
            }
            this._oModel.remove("/Tenants(" + this._oTable.getSelectedItem().getBindingContext().getProperty("TenantId") + ")");
        },
        onBtnSave: function (oEvent) {
            this._oModel.submitChanges();
            this.byId("savButton").setEnabled(false);
        },
        onDescrLiveChange: function (oEvent) {
            //var oEntry = {};
            //this._oModel._setProperty("Tenants", oEvent.getSource().getBindingContext().getProperty("TenantId"), "IsChanged", true);
            this.byId("savButton").setEnabled(true);
        }
    });
});