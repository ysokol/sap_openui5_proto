sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"ys/first/app/model/App",
	"ys/first/app/model/formatter"
], function(Controller, Model, formatter) {
	"use strict";

	return Controller.extend("ys.first.app.controller.MaintItem", {
		formatter: formatter,
		onInit: function() {
			this._oModel = new Model();
			this._oTable = this.byId("myTreeTable");
			this.getView().setModel(this._oModel);
			sap.ui.getCore().setModel(this._oModel);
			this._oTable.attachToggleOpenState(this.onExpandCollapse);
			//this._oModel.attachRequestCompleted(this._onAfterRendering, this);
		},
		onExpandCollapse: function(oEvent) {
			//collapsed, expanded, leaf.
			if (oEvent.getSource().getParent().getModel().getProperty(oEvent.getParameters().rowContext.sPath + "/NodeDrillState") !== "leaf") {
				if (oEvent.getParameter("expanded")) {
					oEvent.getSource().getParent().getModel().setProperty(oEvent.getParameters().rowContext.sPath + "/NodeDrillState", "expanded");
				} else {
					oEvent.getSource().getParent().getModel().setProperty(oEvent.getParameters().rowContext.sPath + "/NodeDrillState", "collapsed");
				}
			}
		},
		_onAfterRendering: function(oEvent) {
		//	alert("_onAfterRendering");
			this._expandAccordingToModel();
		},
		_expandAccordingToModel: function() {
			for (var i = 0; i < this._oTable.getRows().length; i++) {
				switch (this._oTable.getRows()[i].getBindingContext().getProperty("NodeDrillState")) {
					case "expanded":
						this._oTable.expand(i);
						break;
					case "collapsed":
						this._oTable.collapse(i);
						break;
					case "leaf":
						break;
				}
			}
		},
		onMenuAction: function(oEvent) {
			switch (oEvent.getParameter("item").getKey()) {
				case "addSubItem":
					this.onAddSubItem(oEvent);
					break;
				case "addItem":
					this.onAddItem(oEvent);
					break;
				case "editItem":
					this.onEditItem(oEvent);
					break;
			}
		},
		onSaveAll: function(oEvent) {
			this._oModel.submitChanges();
		},
		onAddItem: function(oEvent) {
			
		},
		onEditItem: function(oEvent) {
			if (!this._oEditDialog) {
				this._oEditDialog = new sap.ui.xmlfragment("ys.first.app.view.MaintItemEdit", this);
				this.getView().addDependent(this._oEditDialog);
			}
			this._oEditDialog.bindObject(oEvent.getSource().getParent().getBindingContext().getPath()); 
			this._oEditDialog.open();
		},
		onAddSubItem: function(oEvent) {
			this._oModel.callFunction("/CreateSubItem", {
				method: "POST",
				urlParameters: {
					ParentId: oEvent.getSource().getParent().getBindingContext().getProperty("MaintenanceItemId"),
					Descr: "New SubItem"
				}
			});
		},
		onEditSubmit: function() {
			this._oEditDialog.close();
		}
		
	});
});