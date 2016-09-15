package odatav4;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import database.Tenant;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

/*import maintenance.Tenant;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;*/
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
//import com.microsoft.sqlserver.jdbc.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is invoked by the Olingo framework when the the OData service is
 * invoked order to display a list/collection of data (entities). This is the
 * case if an EntitySet is requested by the user. Such an example URL would be:
 * http://localhost:8080/ExampleService1/ExampleService1.svc/Products
 */
public class MaintEntityCollectionProcessor implements EntityCollectionProcessor {

    private OData odata;
    private ServiceMetadata serviceMetadata;
    private final EntityCollection tenantsCollection = new EntityCollection();

    // our processor is initialized with the OData context object
    public void init(OData odata, ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
        loadData();
    }

    // the only method that is declared in the EntityCollectionProcessor interface
    // this method is called, when the user fires a request to an EntitySet
    // in our example, the URL would be:
    // http://localhost:8080/ExampleService1/ExampleServlet1.svc/Products
    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException, SerializerException {

        // 1st we have retrieve the requested EntitySet from the uriInfo object (representation of the parsed service URI)
        List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); // in our example, the first segment is the EntitySet
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

        // 2nd: fetch the data from backend for this requested EntitySetName // it has to be delivered as EntitySet object
        EntityCollection entitySet = getData(edmEntitySet);

        // 3rd: create a serializer based on the requested format (json)
        ODataSerializer serializer = odata.createSerializer(responseFormat);

        // 4th: Now serialize the content: transform from the EntitySet object to InputStream
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        //ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
        /*URI serviceRoot;
        try {
            serviceRoot = new URI("http://localhost/mavenproject/maintenance");
        } catch (URISyntaxException ex) {
        }*/

        //ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).navOrPropertyPath("http://localhost/mavenproject/maintenance/").build();
        //ContextURL contextUrl = ContextURL.with().serviceRoot(createURI("http://localhost/mavenproject/maintenance/")).entitySet(edmEntitySet).build();
        ContextURL contextUrl = ContextURL.with().serviceRoot(createURI(request.getRawBaseUri() + "/" )).entitySet(edmEntitySet).build();
        final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
        EntityCollectionSerializerOptions opts
                = EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl).build();
        SerializerResult serializedContent = serializer.entityCollection(serviceMetadata, edmEntityType, entitySet, opts);

        // Finally: configure the response object: set the body, headers and status code
        //response.setContent( serializer.serviceDocument(serviceMetadata, request.getRawRequestUri()).getContent() );
        response.setContent(serializedContent.getContent());

        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }

    /**
     * Helper method for providing some sample data
     *
     * @param edmEntitySet for which the data is requested
     * @return data of requested entity set
     */
    private EntityCollection getData(EdmEntitySet edmEntitySet) {

        EntityCollection entityCollection = new EntityCollection();
        //EntityCollection tenantsCollection = new EntityCollection();
        // check for which EdmEntitySet the data is requested
        if (MaintEdmProvider.ES_TENANTS_NAME.equals(edmEntitySet.getName())) {
            entityCollection = tenantsCollection;
        }
        return entityCollection;
    }

    private void loadData() {
        List<Entity> tenantList = tenantsCollection.getEntities();
        Entity e1;
        try {
            final Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(Tenant.class);
            final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            final SessionFactory factory = configuration.buildSessionFactory(builder.build());
            final Session session = factory.openSession();
            session.beginTransaction();
            /*Tenant tenant = new Tenant();
                tenant.setDescription("metherfucker");
                session.save(tenant);
                session.getTransaction().commit();*/
            List result = session.createQuery("from Tenant").list();
            for (Tenant tenant : (List<Tenant>) result) {
                e1 = new Entity();
                e1.addProperty(new Property(null, "tenant_id", ValueType.PRIMITIVE, tenant.getTenantId()));
                e1.addProperty(new Property(null, "description", ValueType.PRIMITIVE, tenant.getDescription()));
                e1.setId(createId("tenant", tenant.getTenantId()));
                tenantList.add(e1);
            }
            session.close();
        } catch (Throwable ex) {

        }
    }

    private URI createURI(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException("Unable to create id for entity: " + uri, e);
        }
    }

    private URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
        }
    }
}
