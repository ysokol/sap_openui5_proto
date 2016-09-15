package odatav2.jpa;

/**
 * *****************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ****************************************************************************
 */
//package org.apache.olingo.odata2.jpa.processor.core.access.data;
import database.Session;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.HttpMethod;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;

import org.apache.olingo.odata2.api.commons.InlineCount;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmMapping;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataBadRequestException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
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
import org.apache.olingo.odata2.core.uri.KeyPredicateImpl;
import org.apache.olingo.odata2.core.uri.SystemQueryOption;
import org.apache.olingo.odata2.core.uri.UriInfoImpl;
import org.apache.olingo.odata2.core.uri.UriType;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPATombstoneContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPATombstoneEntityListener;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPATransaction;
import org.apache.olingo.odata2.jpa.processor.api.access.JPAFunction;
import org.apache.olingo.odata2.jpa.processor.api.access.JPAMethodContext;
import org.apache.olingo.odata2.jpa.processor.api.access.JPAProcessor;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPAModelException;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import org.apache.olingo.odata2.jpa.processor.api.jpql.JPQLContextType;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmMapping;
import org.apache.olingo.odata2.jpa.processor.core.ODataEntityParser;
import org.apache.olingo.odata2.jpa.processor.core.access.data.JPAEntity;
import org.apache.olingo.odata2.jpa.processor.core.access.data.JPALink;
import org.apache.olingo.odata2.jpa.processor.core.access.data.JPAPage;
import org.apache.olingo.odata2.jpa.processor.core.access.data.JPAPage.JPAPageBuilder;
import org.apache.olingo.odata2.jpa.processor.core.access.data.JPAQueryBuilder;
import org.apache.olingo.odata2.jpa.processor.core.access.data.JPAQueryBuilder.JPAQueryInfo;
import session.service.SessionService;

public class MyJPAProcessorImpl {

    private static final String DELTATOKEN = "!deltatoken";
    ODataJPAContext oDataJPAContext;
    EntityManager em;
    private final SessionService sessionService;

    public MyJPAProcessorImpl(final ODataJPAContext oDataJPAContext, SessionService sessionService) {
        this.oDataJPAContext = oDataJPAContext;
        em = oDataJPAContext.getEntityManager();
        this.sessionService = sessionService;
    }

    private database.Session getClientSession() throws ODataException {
        try {
            return sessionService.getSessionById(this.oDataJPAContext.getODataContext().getRequestHeader("SessionId"));
        } catch (Throwable e) {
            throw new ODataApplicationException(String.format("Not authorized, sessionId does not exist."),
                    Locale.US, HttpStatusCodes.FORBIDDEN, "YS001", e);
        }
    }

    
    private void applyEntitySetSelectFilter(final UriInfo uriInfo) throws ODataException {
        // check request details
        UriInfoImpl uriInfoWithFilter;
        EdmEntityType entityType;
        try {
            // new UriInfo with fileter
            uriInfoWithFilter = (UriInfoImpl) uriInfo;
            // get EntityType from UriInfo
            entityType = uriInfoWithFilter.getStartEntitySet().getEntityType();

        } catch (Throwable e) {
            throw new ODataApplicationException(String.format("Unknown request format."),
                    Locale.US, HttpStatusCodes.BAD_REQUEST, "YS002", e);
        }
        // check session
        database.Session session = getClientSession();
        // build and apply filter expression addendum
        try {
            FilterExpression filter = uriInfoWithFilter.getFilter();
            String tenantClientFilter = String.format("TenantId eq %d and Client eq '%s'", session.getTenantId(), session.getClient());
            if (filter == null) {
                filter = UriParser.parseFilter(null, entityType, tenantClientFilter);
            } else {
                String oldFilter = filter.getExpressionString();
                filter = UriParser.parseFilter(null, entityType, String.format("(%s) and (%s)", oldFilter, tenantClientFilter));
            }
            uriInfoWithFilter.setFilter(filter);
        } catch (Throwable e) {
            throw new ODataApplicationException(String.format("Error during tenant and client filter addendum application."),
                    Locale.US, HttpStatusCodes.INTERNAL_SERVER_ERROR, "YS003", e);
        }
    }

    private void validateKeyAttributes(final UriInfo uriInfo) throws ODataException {
        // check session
        database.Session session = getClientSession();

        // build and apply filter expression addendum
        boolean validTenantId = false;
        boolean validClient = false;
        try {
            for (Iterator<KeyPredicate> i = uriInfo.getKeyPredicates().iterator(); i.hasNext();) {
                KeyPredicate queryKeyPredicate = (KeyPredicate) i.next();
                if (queryKeyPredicate.getProperty().getName().equals("TenantId")
                        & queryKeyPredicate.getLiteral().equals(String.format("%d", session.getTenantId()))) {
                    validTenantId = true;
                }
                if (queryKeyPredicate.getProperty().getName().equals("Client")
                        & queryKeyPredicate.getLiteral().equals(String.format("%s", session.getClient()))) {
                    validClient = true;
                }

            }
        } catch (Throwable e) {
            throw new ODataApplicationException(String.format("Error during parsing OData request."),
                    Locale.US, HttpStatusCodes.NOT_ACCEPTABLE, "YS002", e);
        }

        if (!validTenantId | !validClient) {
            throw new ODataApplicationException(String.format("Invalid or missing TenantId/Client."),
                    Locale.US, HttpStatusCodes.NOT_ACCEPTABLE, "YS001");
        }
    }

    private void validateOdataEntry(final ODataEntry oDataEntry) throws ODataException {
        // check session
        database.Session session = getClientSession();
        // check odata entry key attributes
        boolean invalidSessionParam = true;
        try {
            invalidSessionParam = ((int) oDataEntry.getProperties().get("TenantId") != session.getTenantId())
                    | !((String) oDataEntry.getProperties().get("Client")).equals(session.getClient());
        } catch (Throwable e) {
            throw new ODataApplicationException(String.format("Error during parsing OData request."),
                    Locale.US, HttpStatusCodes.NOT_ACCEPTABLE, "YS002", e);
        }
        if (invalidSessionParam) {
            throw new ODataApplicationException(String.format("Invalid or missing TenantId/Client."),
                    Locale.US, HttpStatusCodes.NOT_ACCEPTABLE, "YS001");
        }
    }

    private void preprocess(UriInfo uriInfo, ODataEntry oDataEntry) throws ODataException {
        // cast uriInfo
        UriInfoImpl uriInfoImpl;
        try {
            uriInfoImpl = (UriInfoImpl) uriInfo;
        } catch (Throwable e) {
            throw new ODataApplicationException(String.format("Error during parsing OData request."),
                    Locale.US, HttpStatusCodes.NOT_ACCEPTABLE, "YS002", e);
        }
  
        switch (oDataJPAContext.getODataContext().getHttpMethod()) {
            case HttpMethod.GET:
                switch (uriInfoImpl.getUriType()) {
                    case URI0: //Service document (SystemQueryOption.$format),
                        break;
                    case URI1: //Entity set (SystemQueryOption.$format, SystemQueryOption.$filter, SystemQueryOption.$inlinecount, SystemQueryOption.$orderby, SystemQueryOption.$skiptoken, SystemQueryOption.$skip, SystemQueryOption.$top, SystemQueryOption.$expand, SystemQueryOption.$select),
                        applyEntitySetSelectFilter(uriInfo); 
                        break;
                    case URI2: //Entity set with key predicate (SystemQueryOption.$format, SystemQueryOption.$filter, SystemQueryOption.$expand, SystemQueryOption.$select),
                        applyEntitySetSelectFilter(uriInfo); 
                        break;
                    case URI3: //Complex property of an entity (SystemQueryOption.$format),
                        validateKeyAttributes(uriInfo); 
                        break;
                    case URI4: //Simple property of a complex property of an entity (SystemQueryOption.$format)
                        validateKeyAttributes(uriInfo); 
                        break;
                    case URI5: //Simple property of an entity (SystemQueryOption.$format),
                        validateKeyAttributes(uriInfo); 
                        break;
                    case URI6A: //Navigation property of an entity with target multiplicity '1' or '0..1' (SystemQueryOption.$format, SystemQueryOption.$filter, SystemQueryOption.$expand, SystemQueryOption.$select),
                        validateKeyAttributes(uriInfo); 
                        break;
                    case URI6B: //Navigation property of an entity with target multiplicity '*' (SystemQueryOption.$format, SystemQueryOption.$filter, SystemQueryOption.$inlinecount, SystemQueryOption.$orderby, SystemQueryOption.$skiptoken, SystemQueryOption.$skip, SystemQueryOption.$top, SystemQueryOption.$expand, SystemQueryOption.$select),
                        validateKeyAttributes(uriInfo); 
                        break;
                    case URI7A: //Link to a single entity (SystemQueryOption.$format, SystemQueryOption.$filter),
                        validateKeyAttributes(uriInfo); 
                        break;
                    case URI7B: //Link to multiple entities (SystemQueryOption.$format, SystemQueryOption.$filter, SystemQueryOption.$inlinecount, SystemQueryOption.$orderby, SystemQueryOption.$skiptoken, SystemQueryOption.$skip, SystemQueryOption.$top),
                        validateKeyAttributes(uriInfo); 
                        break;
                    case URI8: //Metadata document (),
                        break;
                    //case URI9: //Batch request(),
                    //case URI10: //Function import returning a single instance of an entity type (SystemQueryOption.$format),
                    //case URI11: //Function import returning a collection of complex-type instances (SystemQueryOption.$format),
                    //case URI12: //Function import returning a single instance of a complex type (SystemQueryOption.$format),
                    //case URI13: // Function import returning a collection of primitive-type instances(SystemQueryOption.$format),
                    //case URI14: //Function import returning a single instance of a primitive type (SystemQueryOption.$format),
                    case URI15: //Count of an entity set (SystemQueryOption.$filter, SystemQueryOption.$orderby, SystemQueryOption.$skip, SystemQueryOption.$top),
                        applyEntitySetSelectFilter(uriInfo); 
                        break;
                    case URI16: //Count of a single entity (SystemQueryOption.$filter),
                        validateKeyAttributes(uriInfo); 
                        break;
                    //case URI17: //Media resource of an entity (SystemQueryOption.$format, SystemQueryOption.$filter),
                    //case URI50A: //Count of link to a single entity (SystemQueryOption.$filter),
                    //case URI50B: //Count of links to multiple entities (SystemQueryOption.$filter, SystemQueryOption.$orderby, SystemQueryOption.$skip, SystemQueryOption.$top);
                    default:
                        throw new ODataApplicationException(String.format("Odata request with UriType '%s' is not implemented for HTTP method '%s'.", uriInfoImpl.getUriType().name(), oDataJPAContext.getODataContext().getHttpMethod()),
                                Locale.US, HttpStatusCodes.NOT_IMPLEMENTED, "YS003");
                }
                break;
            case HttpMethod.POST:
                switch (uriInfoImpl.getUriType()) {
                    case URI1: //Entity set (SystemQueryOption.$format, SystemQueryOption.$filter, SystemQueryOption.$inlinecount, SystemQueryOption.$orderby, SystemQueryOption.$skiptoken, SystemQueryOption.$skip, SystemQueryOption.$top, SystemQueryOption.$expand, SystemQueryOption.$select),
                        validateOdataEntry(oDataEntry); 
                        break;
                    default:
                        throw new ODataApplicationException(String.format("Odata request with UriType '%s' is not implemented for HTTP method '%s'.", uriInfoImpl.getUriType().name(), oDataJPAContext.getODataContext().getHttpMethod()),
                                Locale.US, HttpStatusCodes.NOT_IMPLEMENTED, "YS003");
                }
                break;
            case HttpMethod.PUT:
                switch (uriInfoImpl.getUriType()) {
                    case URI2: //Entity set with key predicate (SystemQueryOption.$format, SystemQueryOption.$filter, SystemQueryOption.$expand, SystemQueryOption.$select),
                        validateKeyAttributes(uriInfo); 
                        break;
                    default:
                        throw new ODataApplicationException(String.format("Odata request with UriType '%s' is not implemented for HTTP method '%s'.", uriInfoImpl.getUriType().name(), oDataJPAContext.getODataContext().getHttpMethod()),
                                Locale.US, HttpStatusCodes.NOT_IMPLEMENTED, "YS003");
                }
                break;
            case HttpMethod.DELETE:
                switch (uriInfoImpl.getUriType()) {
                    case URI2: //Entity set with key predicate (SystemQueryOption.$format, SystemQueryOption.$filter, SystemQueryOption.$expand, SystemQueryOption.$select),
                        validateKeyAttributes(uriInfo); 
                        break;
                    default:
                        throw new ODataApplicationException(String.format("Odata request with UriType '%s' is not implemented for HTTP method '%s'.", uriInfoImpl.getUriType().name(), oDataJPAContext.getODataContext().getHttpMethod()),
                                Locale.US, HttpStatusCodes.NOT_IMPLEMENTED, "YS003");
                }
                break;
            default:
                throw new ODataApplicationException(String.format("Odata request with UriType '%s' is not implemented for HTTP method '%s'.", uriInfoImpl.getUriType().name(), oDataJPAContext.getODataContext().getHttpMethod()),
                        Locale.US, HttpStatusCodes.NOT_IMPLEMENTED, "YS003");
        }
    }

    /* Process Function Import Request */
    @SuppressWarnings("unchecked")
    public List<Object> process(final GetFunctionImportUriInfo uriParserResultView)
            throws ODataJPAModelException, ODataJPARuntimeException, ODataException {
        
        preprocess((UriInfo)uriParserResultView, null);
        
        JPAMethodContext jpaMethodContext = JPAMethodContext.createBuilder(
                JPQLContextType.FUNCTION, uriParserResultView).build();

        List<Object> resultObj = null;

        try {

            JPAFunction jpaFunction = jpaMethodContext.getJPAFunctionList()
                    .get(0);
            Method method = jpaFunction.getFunction();
            Object[] args = jpaFunction.getArguments();

            if (uriParserResultView.getFunctionImport().getReturnType()
                    .getMultiplicity().equals(EdmMultiplicity.MANY)) {

                resultObj = (List<Object>) method.invoke(
                        jpaMethodContext.getEnclosingObject(), args);
            } else {
                resultObj = new ArrayList<Object>();
                Object result = method.invoke(
                        jpaMethodContext.getEnclosingObject(), args);
                resultObj.add(result);
            }

        } catch (EdmException e) {
            throw ODataJPARuntimeException
                    .throwException(ODataJPARuntimeException.GENERAL
                            .addContent(e.getMessage()), e);
        } catch (IllegalAccessException e) {
            throw ODataJPARuntimeException
                    .throwException(ODataJPARuntimeException.GENERAL
                            .addContent(e.getMessage()), e);
        } catch (IllegalArgumentException e) {
            throw ODataJPARuntimeException
                    .throwException(ODataJPARuntimeException.GENERAL
                            .addContent(e.getMessage()), e);
        } catch (InvocationTargetException e) {
            throw ODataJPARuntimeException
                    .throwException(ODataJPARuntimeException.GENERAL
                            .addContent(e.getTargetException().getMessage()), e.getTargetException());
        }

        return resultObj;
    }

    /* Process Get Entity Set Request (Query) */
    public List<Object> process(final GetEntitySetUriInfo uriParserResultView)
            throws ODataJPAModelException, ODataJPARuntimeException, ODataException {
        
        preprocess((UriInfo)uriParserResultView, null);
        
        List<Object> result = null;
        if (uriParserResultView.getFunctionImport() != null) {
            return (List<Object>) process((GetFunctionImportUriInfo) uriParserResultView);
        }

        InlineCount inlineCount = uriParserResultView.getInlineCount();
        Integer top = uriParserResultView.getTop() == null ? 1 : uriParserResultView.getTop().intValue();
        boolean hasNoAllPages = inlineCount == null ? true : !inlineCount.equals(InlineCount.ALLPAGES);
        if (top.intValue() == 0 && hasNoAllPages) {
            return new ArrayList<Object>();
        }

        try {
            JPAEdmMapping mapping = (JPAEdmMapping) uriParserResultView.getTargetEntitySet().getEntityType().getMapping();
            JPAQueryBuilder queryBuilder = new JPAQueryBuilder(oDataJPAContext);
            JPAQueryInfo queryInfo = queryBuilder.build(uriParserResultView);
            Query query = queryInfo.getQuery();
            ODataJPATombstoneEntityListener listener
                    = queryBuilder.getODataJPATombstoneEntityListener((UriInfo) uriParserResultView);
            Map<String, String> customQueryOptions = uriParserResultView.getCustomQueryOptions();
            String deltaToken = null;
            if (customQueryOptions != null) {
                deltaToken = uriParserResultView.getCustomQueryOptions().get(DELTATOKEN);
            }
            if (deltaToken != null) {
                ODataJPATombstoneContext.setDeltaToken(deltaToken);
            }
            if (listener != null && (!queryInfo.isTombstoneQuery() && listener.isTombstoneSupported())) {
                query.getResultList();
                List<Object> deltaResult
                        = (List<Object>) ODataJPATombstoneContext.getDeltaResult(((EdmMapping) mapping).getInternalName());
                result = handlePaging(deltaResult, uriParserResultView);
            } else {
                result = handlePaging(query, uriParserResultView);
            }
            if (listener != null && listener.isTombstoneSupported()) {
                ODataJPATombstoneContext.setDeltaToken(listener.generateDeltaToken((List<Object>) result, query));
            }
            return result == null ? new ArrayList<Object>() : result;
        } catch (EdmException e) {
            throw ODataJPARuntimeException.throwException(
                    ODataJPARuntimeException.ERROR_JPQL_QUERY_CREATE, e);
        } catch (InstantiationException e) {
            throw ODataJPARuntimeException.throwException(
                    ODataJPARuntimeException.ERROR_JPQL_QUERY_CREATE, e);
        } catch (IllegalAccessException e) {
            throw ODataJPARuntimeException.throwException(
                    ODataJPARuntimeException.ERROR_JPQL_QUERY_CREATE, e);
        }
    }

    /* Process Get Entity Request (Read) */
    public <T> Object process(GetEntityUriInfo uriParserResultView)
            throws ODataJPAModelException, ODataJPARuntimeException, ODataException {
        
        preprocess((UriInfo)uriParserResultView, null);
        
        return readEntity(new JPAQueryBuilder(oDataJPAContext).build(uriParserResultView));
    }

    /* Process $count for Get Entity Set Request */
    public long process(final GetEntitySetCountUriInfo resultsView)
            throws ODataJPAModelException, ODataJPARuntimeException, ODataException {
        
        preprocess((UriInfo)resultsView, null);
        
        JPAQueryBuilder queryBuilder = new JPAQueryBuilder(oDataJPAContext);
        Query query = queryBuilder.build(resultsView);
        List<?> resultList = query.getResultList();
        if (resultList != null && resultList.size() == 1) {
            return Long.valueOf(resultList.get(0).toString());
        }

        return 0;
    }

    /* Process $count for Get Entity Request */
    public long process(final GetEntityCountUriInfo resultsView) throws ODataJPAModelException, ODataJPARuntimeException, ODataException {
        
        preprocess((UriInfo)resultsView, null);
        
        JPAQueryBuilder queryBuilder = new JPAQueryBuilder(oDataJPAContext);
        Query query = queryBuilder.build(resultsView);
        List<?> resultList = query.getResultList();
        if (resultList != null && resultList.size() == 1) {
            return Long.valueOf(resultList.get(0).toString());
        }
        return 0;
    }

    /* Process Create Entity Request */
    public Object process(final PostUriInfo createView, final InputStream content,
            final String requestedContentType) throws ODataJPAModelException,
            ODataJPARuntimeException, ODataException {
        return processCreate(createView, content, null, requestedContentType);
    }

    public Object process(final PostUriInfo createView, final Map<String, Object> content)
            throws ODataJPAModelException, ODataJPARuntimeException, ODataException {
        return processCreate(createView, null, content, null);
    }

    /* Process Update Entity Request */
    public Object process(final PutMergePatchUriInfo updateView,
            final InputStream content, final String requestContentType)
            throws ODataJPAModelException, ODataJPARuntimeException, ODataException {
        return processUpdate(updateView, content, null, requestContentType);
    }

    public Object process(final PutMergePatchUriInfo updateView, final Map<String, Object> content)
            throws ODataJPAModelException, ODataJPARuntimeException, ODataException {
        return processUpdate(updateView, null, content, null);
    }

    /* Process Delete Entity Request */
    public Object process(DeleteUriInfo uriParserResultView, final String contentType)
            throws ODataJPAModelException, ODataJPARuntimeException, ODataException {
        
        preprocess((UriInfo)uriParserResultView, null);
        
        if (uriParserResultView instanceof DeleteUriInfo) {
            if (((UriInfo) uriParserResultView).isLinks()) {
                return deleteLink(uriParserResultView);
            }
        }
        
        Object selectedObject = readEntity(new JPAQueryBuilder(oDataJPAContext).build(uriParserResultView));
        if (selectedObject != null) {

            boolean isLocalTransaction = setTransaction();
            em.remove(selectedObject);
            em.flush();
            if (isLocalTransaction) {
                oDataJPAContext.getODataJPATransaction().commit();
            }
        }
        return selectedObject;
    }

    /* Process Get Entity Link Request */
    public Object process(final GetEntityLinkUriInfo uriParserResultView)
            throws ODataJPAModelException, ODataJPARuntimeException, ODataException {

        return this.process((GetEntityUriInfo) uriParserResultView);
    }

    /* Process Get Entity Set Link Request */
    public List<Object> process(final GetEntitySetLinksUriInfo uriParserResultView)
            throws ODataJPAModelException, ODataJPARuntimeException, ODataException {
        return this.process((GetEntitySetUriInfo) uriParserResultView);
    }

    public void process(final PostUriInfo uriInfo,
            final InputStream content, final String requestContentType, final String contentType)
            throws ODataJPARuntimeException, ODataJPAModelException, ODataException {
        JPALink link = new JPALink(oDataJPAContext);
        link.create(uriInfo, content, requestContentType, contentType);
        link.save();
    }

    public void process(final PutMergePatchUriInfo putUriInfo,
            final InputStream content, final String requestContentType, final String contentType)
            throws ODataJPARuntimeException, ODataJPAModelException, ODataException {

        JPALink link = new JPALink(oDataJPAContext);
        link.update(putUriInfo, content, requestContentType, contentType);
        link.save();

    }

    /* Common method for Read and Delete */
    private Object readEntity(final Query query) throws ODataJPARuntimeException, ODataException {
        Object selectedObject = null;
        @SuppressWarnings("rawtypes")
        final List resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            selectedObject = resultList.get(0);
        }
        return selectedObject;
    }

    private Object processCreate(final PostUriInfo createView, final InputStream content,
            final Map<String, Object> properties,
            final String requestedContentType) throws ODataJPAModelException,
            ODataJPARuntimeException, ODataException {
        try {
            final EdmEntitySet oDataEntitySet = createView.getTargetEntitySet();
            final EdmEntityType oDataEntityType = oDataEntitySet.getEntityType();
            final JPAEntity virtualJPAEntity = new JPAEntity(oDataEntityType, oDataEntitySet, oDataJPAContext);
            Object jpaEntity = null;

            if (content != null) {
                final ODataEntityParser oDataEntityParser = new ODataEntityParser(oDataJPAContext);
                final ODataEntry oDataEntry
                        = oDataEntityParser.parseEntry(oDataEntitySet, content, requestedContentType, false);
                preprocess((UriInfo)createView, oDataEntry);
                virtualJPAEntity.create(oDataEntry);
            } else if (properties != null) {
                virtualJPAEntity.create(properties);
            } else {
                return null;
            }

            boolean isLocalTransaction = setTransaction();
            jpaEntity = virtualJPAEntity.getJPAEntity();

            em.persist(jpaEntity);
            if (em.contains(jpaEntity)) {
                if (isLocalTransaction) {
                    oDataJPAContext.getODataJPATransaction().commit();
                }
                return jpaEntity;
            }
        } catch (ODataBadRequestException e) {
            throw ODataJPARuntimeException.throwException(
                    ODataJPARuntimeException.ERROR_JPQL_QUERY_CREATE, e);
        } catch (EdmException e) {
            throw ODataJPARuntimeException.throwException(
                    ODataJPARuntimeException.ERROR_JPQL_QUERY_CREATE, e);
        }
        return null;
    }

    private <T> Object processUpdate(PutMergePatchUriInfo updateView,
            final InputStream content, final Map<String, Object> properties, final String requestContentType)
            throws ODataJPAModelException, ODataJPARuntimeException, ODataException {
        Object jpaEntity = null;
        try {
            boolean isLocalTransaction = setTransaction();
            jpaEntity = readEntity(new JPAQueryBuilder(oDataJPAContext).build(updateView));

            if (jpaEntity == null) {
                throw ODataJPARuntimeException
                        .throwException(ODataJPARuntimeException.RESOURCE_NOT_FOUND, null);
            }

            final EdmEntitySet oDataEntitySet = updateView.getTargetEntitySet();
            final EdmEntityType oDataEntityType = oDataEntitySet.getEntityType();
            final JPAEntity virtualJPAEntity = new JPAEntity(oDataEntityType, oDataEntitySet, oDataJPAContext);
            virtualJPAEntity.setJPAEntity(jpaEntity);
            if (content != null) {
                final ODataEntityParser oDataEntityParser = new ODataEntityParser(oDataJPAContext);
                ODataEntry oDataEntry;
                oDataEntry = oDataEntityParser.parseEntry(oDataEntitySet, content, requestContentType, false);
                preprocess((UriInfo)updateView, oDataEntry);
                virtualJPAEntity.update(oDataEntry);
            } else if (properties != null) {
                virtualJPAEntity.update(properties);
            } else {
                return null;
            }
            em.flush();
            if (isLocalTransaction) {
                oDataJPAContext.getODataJPATransaction().commit();
            }
        } catch (ODataBadRequestException e) {
            throw ODataJPARuntimeException.throwException(
                    ODataJPARuntimeException.ERROR_JPQL_QUERY_CREATE, e);
        } catch (EdmException e) {
            throw ODataJPARuntimeException.throwException(
                    ODataJPARuntimeException.ERROR_JPQL_QUERY_CREATE, e);
        }
        return jpaEntity;
    }

    private Object deleteLink(final DeleteUriInfo uriParserResultView) throws ODataJPARuntimeException, ODataException {
        JPALink link = new JPALink(oDataJPAContext);
        link.delete(uriParserResultView);
        link.save();
        return link.getTargetJPAEntity();
    }

    private List<Object> handlePaging(final List<Object> result, final GetEntitySetUriInfo uriParserResultView) {
        if (result == null) {
            return null;
        }
        JPAPageBuilder pageBuilder = new JPAPageBuilder();
        pageBuilder.pageSize(oDataJPAContext.getPageSize())
                .entities(result)
                .skipToken(uriParserResultView.getSkipToken());

        // $top/$skip with $inlinecount case handled in response builder to avoid multiple DB call
        if (uriParserResultView.getSkip() != null && uriParserResultView.getInlineCount() == null) {
            pageBuilder.skip(uriParserResultView.getSkip().intValue());
        }

        if (uriParserResultView.getTop() != null && uriParserResultView.getInlineCount() == null) {
            pageBuilder.top(uriParserResultView.getTop().intValue());
        }

        JPAPage page = pageBuilder.build();
        oDataJPAContext.setPaging(page);

        return page.getPagedEntities();
    }

    private List<Object> handlePaging(final Query query, final GetEntitySetUriInfo uriParserResultView) {

        JPAPageBuilder pageBuilder = new JPAPageBuilder();
        pageBuilder.pageSize(oDataJPAContext.getPageSize())
                .query(query)
                .skipToken(uriParserResultView.getSkipToken());

        // $top/$skip with $inlinecount case handled in response builder to avoid multiple DB call
        if (uriParserResultView.getSkip() != null && uriParserResultView.getInlineCount() == null) {
            pageBuilder.skip(uriParserResultView.getSkip().intValue());
        }

        if (uriParserResultView.getTop() != null && uriParserResultView.getInlineCount() == null) {
            pageBuilder.top(uriParserResultView.getTop().intValue());
        }

        JPAPage page = pageBuilder.build();
        oDataJPAContext.setPaging(page);

        return page.getPagedEntities();

    }

    private boolean setTransaction() {
        ODataJPATransaction transaction = oDataJPAContext.getODataJPATransaction();
        if (!transaction.isActive()) {
            transaction.begin();
            return true;
        }
        return false;
    }
}
