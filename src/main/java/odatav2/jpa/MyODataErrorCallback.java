/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odatav2.jpa;

import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPAException;

/**
 *
 * @author Yurij
 */
public class MyODataErrorCallback implements ODataErrorCallback {

    @Override
    public ODataResponse handleError(final ODataErrorContext context) throws ODataApplicationException {

        final String SEPARATOR = " : ";

        Throwable t = context.getException();
        if (t instanceof ODataJPAException && t.getCause() != null) {
            StringBuilder errorBuilder = new StringBuilder();
            errorBuilder.append(t.getCause().getClass().toString());
            errorBuilder.append(SEPARATOR);
            errorBuilder.append(t.getCause().getMessage());
            context.setInnerError(errorBuilder.toString());
        }
        return EntityProvider.writeErrorDocument(context);

    }
}
