sap.ui.define([], function () {
    "use strict";

    return {
        /**
         * Rounds the number unit value to 2 digits
         * @public
         * @param {string} sValue the number string to be rounded
         * @returns {string} sValue with 2 digits rounded
         */
        edmTypeToSapUiType: function (sValue) {
            switch (sValue) {
                case "Edm.Boolean":
                    return sap.ui.model.odata.type.Boolean;
                case "Edm.Byte":
                    return sap.ui.model.odata.type.Byte;
                case "Edm.Date":
                    return sap.ui.model.odata.type.Date;
                case "Edm.DateTime":
                    return sap.ui.model.odata.type.DateTime;
                case "Edm.DateTimeOffset":
                    return sap.ui.model.odata.type.DateTimeOffset;
                case "Edm.Decimal":
                    return sap.ui.model.odata.type.Decimal;
                case "Edm.Double":
                    return sap.ui.model.odata.type.Double;
                case "Edm.Guid":
                    return sap.ui.model.odata.type.Guid;
                case "Edm.Int16":
                    return sap.ui.model.odata.type.Int16;
                case "Edm.Int32":
                    return sap.ui.model.odata.type.Int32;
                case "Edm.Int64":
                    return sap.ui.model.odata.type.Int64;
                case "Edm.SByte":
                    return sap.ui.model.odata.type.SByte;
                case "Edm.Single":
                    return sap.ui.model.odata.type.Single;
                case "Edm.String":
                    return sap.m.InputType.Text;
                case "Edm.Time":
                    return sap.ui.model.odata.type.Time;
                case "Edm.TimeOfDay":
                    return sap.ui.model.odata.type.TimeOfDay;
                default:
                    return "";
            }
        },
        updateFlagToString: function (sValue) {
        	switch (sValue) {
        		case "N":
        			return "New";
        		case "U":
        			return "Updated";
        		case "D":
        			return "Deleted";
        		case "":
        			return "";
        	}
        },
        strategyPackageDetailsKeys: function (sValue) {
        	debugger;
        	var keys = ["1Y", "2Y", "3Y"];
        	return keys;
        }
    };

});