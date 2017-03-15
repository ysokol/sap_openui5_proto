sap.ui.define([
    "sap/ui/model/odata/v2/ODataModel"
], function (ODataModel) {
    "use strict";
    return ODataModel.extend("ys.first.app.model.App", {
        constructor: function () {
            // ODataModel.apply(this, arguments);

            // call parent constructor
            ODataModel.call(this, "http://localhost:8080/mavenproject/maintenancev2.svc/", {
                json: true,
                useBatch: false,
                defaultBindingMode: sap.ui.model.BindingMode.TwoWay,
                defaultUpdateMethod: sap.ui.model.odata.UpdateMethod.Put,
                loadMetadataAsync: false,
                tokenHandling: true //,
                        //headers: {SessionId: "123"}
            });

            this.attachRequestFailed(function (oEvent) {
                var oServerError = JSON.parse(oEvent.getParameters().response.responseText);
                var oServerDetailError = JSON.parse(oServerError.error.innererror);
                //oServerError.error.code
                //oServerError.error.innererror.detailMessage
                //oServerError.error.innererror.cause
                
                var cause = oServerDetailError
                var detailedContent = new Array(1);
                while (cause) {
                    detailedContent.push( new sap.m.Label({text: "Caused By:", design: sap.m.LabelDesign.Bold}) );
                    detailedContent.push( new sap.m.Text({text: cause.detailMessage}) );
                    cause = cause.cause;
                }
                
                var dialog = new sap.m.Dialog({
                    title: 'Error',
                    type: 'Message',
                    state: 'Error',
                    content: new sap.m.Text({
                        text: oServerError.error.message.value
                    }),
                    beginButton: new sap.m.Button({
                        text: 'Additonal Details',
                        press: function () {
                            dialog.close();
                            var innerDialog = new sap.m.Dialog({
                                title: 'Error Details',
                                type: 'Message',
                                state: 'Error',
                                content: detailedContent,
                                beginButton: new sap.m.Button({
                                    text: 'OK',
                                    press: function () {
                                        innerDialog.close();

                                    }
                                }),
                                afterClose: function () {
                                    innerDialog.destroy();
                                }
                            });
                            innerDialog.open();
                        }
                    }),
                    endButton: new sap.m.Button({
                        text: 'Close',
                        press: function () {
                            dialog.close();
                        }
                    }),
                    afterClose: function () {
                        dialog.destroy();
                    }
                });
                
                dialog.open();

            });

            this.attachPropertyChange(this.onOropertyChange, this);


            // bind events
            /*var binding = new sap.ui.model.odata.v2.ODataContextBinding(this, "/", this.getContext("/"));
             binding.attachChange(function(oEvent) {
             debugger;
             try {
             oEvent.getSource().getBindingContext().getProperty();
             }
             catch (err) {
             
             }
             }, this);*/
        },
        onOropertyChange: function (oEvent) {
            try {
                if (oEvent.getParameters().path !== "UpdateFlag") {
                    this.setProperty(oEvent.getParameters().context.getPath() + "/UpdateFlag", "");
                    for (var key in this.getProperty(oEvent.getParameters().context.getPath())) {
                        if (key !== "UpdateFlag") {
                            if (this.getOriginalProperty(oEvent.getParameters().context.getPath() + "/" + key) !==
                                    this.getProperty(oEvent.getParameters().context.getPath() + "/" + key)) {
                                this.setProperty(oEvent.getParameters().context.getPath() + "/UpdateFlag", "U");
                                break;
                            }
                        }
                    }
                }
            } catch (err) {

            }
        },
        _setProperty: function (entity, id, property, value) {
            this.setProperty("/" + entity + "(" + id.toString() + ")" + "/" + property, value);
        }
    });
});