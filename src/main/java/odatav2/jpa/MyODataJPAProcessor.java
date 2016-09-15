/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odatav2.jpa;

import java.io.InputStream;
import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityLinkUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetLinksUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetFunctionImportUriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.apache.olingo.odata2.api.uri.info.PutMergePatchUriInfo;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAProcessor;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPAException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import database.Session;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import jpa.session.MyJpaSessionFactory;
import org.apache.olingo.commons.core.edm.EdmPropertyImpl;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataForbiddenException;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.core.ODataResponseImpl;
import org.apache.olingo.odata2.core.uri.KeyPredicateImpl;
import session.service.SessionService;

import org.apache.olingo.odata2.core.uri.UriInfoImpl;

public class MyODataJPAProcessor extends ODataJPAProcessor {

    private final MyJpaSessionFactory jpaSessionFactory = new MyJpaSessionFactory();
    private final SessionService sessionService;
    private final MyJPAProcessorImpl myJPAProcessor;
    
    public MyODataJPAProcessor(final ODataJPAContext oDataJPAContext) {
        super(oDataJPAContext);
        if (oDataJPAContext == null) {
            throw new IllegalArgumentException(ODataJPAException.ODATA_JPACTX_NULL);
        }
        sessionService = new SessionService(jpaSessionFactory);
        myJPAProcessor = new MyJPAProcessorImpl(oDataJPAContext, sessionService); // create custom processor
    }

    @Override
    public ODataResponse readEntitySet(final GetEntitySetUriInfo uriParserResultView,
            final String contentType)
            throws ODataException {

        List<Object> jpaEntities = myJPAProcessor.process(uriParserResultView);

        ODataResponse oDataResponse = responseBuilder.build(uriParserResultView, jpaEntities, contentType);

        return oDataResponse;

    }

    @Override
    public ODataResponse readEntity(final GetEntityUriInfo uriParserResultView,
            final String contentType)
            throws ODataException {

        Object jpaEntity = myJPAProcessor.process(uriParserResultView);

        ODataResponse oDataResponse
                = responseBuilder.build(uriParserResultView, jpaEntity, contentType);

        return oDataResponse;
    }

    @Override
    public ODataResponse countEntitySet(final GetEntitySetCountUriInfo uriParserResultView,
            final String contentType)
            throws ODataException {

        long jpaEntityCount = myJPAProcessor.process(uriParserResultView);

        ODataResponse oDataResponse = responseBuilder.build(jpaEntityCount);

        return oDataResponse;
    }

    @Override
    public ODataResponse existsEntity(final GetEntityCountUriInfo uriInfo,
            final String contentType)
            throws ODataException {

        long jpaEntityCount = myJPAProcessor.process(uriInfo);

        ODataResponse oDataResponse = responseBuilder.build(jpaEntityCount);

        return oDataResponse;
    }

    @Override
    public ODataResponse createEntity(final PostUriInfo uriParserResultView,
            final InputStream content,
            final String requestContentType,
            final String contentType) throws ODataException {

        Object createdJpaEntity = myJPAProcessor.process(uriParserResultView, content, requestContentType);

        ODataResponse oDataResponse
                = responseBuilder.build(uriParserResultView, createdJpaEntity, contentType);

        return oDataResponse;
    }

    @Override
    public ODataResponse updateEntity(final PutMergePatchUriInfo uriParserResultView,
            final InputStream content,
            final String requestContentType,
            final boolean merge,
            final String contentType) throws ODataException {

        Object jpaEntity = myJPAProcessor.process(uriParserResultView, content, requestContentType);

        ODataResponse oDataResponse = responseBuilder.build(uriParserResultView, jpaEntity);

        return oDataResponse;
    }

    @Override
    public ODataResponse deleteEntity(final DeleteUriInfo uriParserResultView,
            final String contentType)
            throws ODataException {

        Object deletedObj = myJPAProcessor.process(uriParserResultView, contentType);

        ODataResponse oDataResponse = responseBuilder.build(uriParserResultView, deletedObj);
        return oDataResponse;
    }

    @Override
    public ODataResponse executeFunctionImport(final GetFunctionImportUriInfo uriParserResultView,
            final String contentType) throws ODataException {

        List<Object> resultEntity = myJPAProcessor.process(uriParserResultView);

        ODataResponse oDataResponse
                = responseBuilder.build(uriParserResultView, resultEntity, contentType);

        return oDataResponse;
    }

    @Override
    public ODataResponse executeFunctionImportValue(final GetFunctionImportUriInfo uriParserResultView,
            final String contentType) throws ODataException {

        List<Object> result = myJPAProcessor.process(uriParserResultView);

        ODataResponse oDataResponse
                = responseBuilder.build(uriParserResultView, result, contentType);

        return oDataResponse;
    }

    @Override
    public ODataResponse readEntityLink(final GetEntityLinkUriInfo uriParserResultView,
            final String contentType)
            throws ODataException {

        Object jpaEntity = myJPAProcessor.process(uriParserResultView);

        ODataResponse oDataResponse
                = responseBuilder.build(uriParserResultView, jpaEntity, contentType);

        return oDataResponse;
    }

    @Override
    public ODataResponse readEntityLinks(final GetEntitySetLinksUriInfo uriParserResultView,
            final String contentType)
            throws ODataException {

        List<Object> jpaEntity = myJPAProcessor.process(uriParserResultView);

        ODataResponse oDataResponse
                = responseBuilder.build(uriParserResultView, jpaEntity, contentType);

        return oDataResponse;
    }

    @Override
    public ODataResponse createEntityLink(final PostUriInfo uriParserResultView,
            final InputStream content,
            final String requestContentType,
            final String contentType) throws ODataException {

        myJPAProcessor.process(uriParserResultView, content, requestContentType, contentType);

        return ODataResponse.newBuilder().build();
    }

    @Override
    public ODataResponse updateEntityLink(final PutMergePatchUriInfo uriParserResultView,
            final InputStream content,
            final String requestContentType,
            final String contentType) throws ODataException {

        myJPAProcessor.process(uriParserResultView, content, requestContentType, contentType);

        return ODataResponse.newBuilder().build();
    }

    @Override
    public ODataResponse deleteEntityLink(final DeleteUriInfo uriParserResultView,
            final String contentType)
            throws ODataException {

        myJPAProcessor.process(uriParserResultView, contentType);
        return ODataResponse.newBuilder().build();

    }
}
