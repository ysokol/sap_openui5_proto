sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "ys/first/app/model/App",
    "ys/first/app/model/formatter"
], function (Controller, Model, formatter) {
    "use strict";

    return Controller.extend("ys.first.app.controller.Client", {
        formatter: formatter,
        onInit: function () {
            this._oModel = new Model();
            this._oTable = this.byId("clientsTable");

            this.getView().setModel(this._oModel);
            sap.ui.getCore().setModel(this._oModel);

        },
        onBtnAddRow: function (oEvent) {
            if (this._newListItem)
                return;
            this._newListItem = this.byId("ColumnListItemId").clone();
            this._newListItem.getCells().forEach(function (oControl) {
                oControl.addStyleClass("newItemValue");
            });
            this._oTable.addItem(this._newListItem);

            var retContext = this._oModel.createEntry("/UnitOfMeasures", {properties: {TenantId: 42, Client: "010", Uom: "<?>"}});
            //var retContext = this._oModel.createEntry("/UnitOfMeasures");
            this._newListItem.setBindingContext(retContext);

            this._oModel.updateBindings();

            //this._oTable.addItem(this._oTable.getSelectedItem().clone());
        },
        onBtnDelRow: function (oEvent) {
            if (this._oTable.getSelectedItem() === null) {
                alert("No record selected!");
                return;
            }
            this._oModel.remove(this._oTable.getSelectedItem().getBindingContext().getPath());
        },
        onBtnSave: function (oEvent) {
            this._oModel.submitChanges();
            if (this._newListItem) {
                this._newListItem.destroy();
                delete this._newListItem;
            }
        },
        onBtnRefresh: function (oEvent) {
            this._oModel.refresh();
        }
    });
});