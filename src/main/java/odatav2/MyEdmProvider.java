package odatav2;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmConcurrencyMode;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.EdmTargetPath;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationEnd;
import org.apache.olingo.odata2.api.edm.provider.AssociationSet;
import org.apache.olingo.odata2.api.edm.provider.AssociationSetEnd;
import org.apache.olingo.odata2.api.edm.provider.ComplexProperty;
import org.apache.olingo.odata2.api.edm.provider.ComplexType;
import org.apache.olingo.odata2.api.edm.provider.CustomizableFeedMappings;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.edm.provider.EntityContainer;
import org.apache.olingo.odata2.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Facets;
import org.apache.olingo.odata2.api.edm.provider.FunctionImport;
import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.NavigationProperty;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.PropertyRef;
import org.apache.olingo.odata2.api.edm.provider.ReturnType;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.apache.olingo.odata2.api.exception.ODataException;

public class MyEdmProvider extends EdmProvider {

    static final String ENTITY_SET_NAME_TENANTS = "Tenants";
    static final String ENTITY_NAME_TENANT = "Tenant";
    private static final String NAMESPACE = "Maintenance";

    private static final FullQualifiedName ENTITY_TYPE_TENANT = new FullQualifiedName(NAMESPACE, ENTITY_NAME_TENANT);

    private static final String ENTITY_CONTAINER = "MaintenanceContainer";

    @Override
    public List<Schema> getSchemas() throws ODataException {
        List<Schema> schemas = new ArrayList<Schema>();

        Schema schema = new Schema();
        schema.setNamespace(NAMESPACE);

        List<EntityType> entityTypes = new ArrayList<EntityType>();
        entityTypes.add(getEntityType(ENTITY_TYPE_TENANT));
        schema.setEntityTypes(entityTypes);

        List<EntityContainer> entityContainers = new ArrayList<EntityContainer>();
        EntityContainer entityContainer = new EntityContainer();
        entityContainer.setName(ENTITY_CONTAINER).setDefaultEntityContainer(true);

        List<EntitySet> entitySets = new ArrayList<EntitySet>();
        entitySets.add(getEntitySet(ENTITY_CONTAINER, ENTITY_SET_NAME_TENANTS));
        entityContainer.setEntitySets(entitySets);

        entityContainers.add(entityContainer);
        schema.setEntityContainers(entityContainers);

        schemas.add(schema);

        return schemas;
    }

    @Override
    public EntityType getEntityType(FullQualifiedName edmFQName) throws ODataException {
        if (NAMESPACE.equals(edmFQName.getNamespace())) {
            if (ENTITY_TYPE_TENANT.getName().equals(edmFQName.getName())) {

                //Properties
                List<Property> properties = new ArrayList<Property>();
                properties.add(new SimpleProperty().setName("TenantId").setType(EdmSimpleTypeKind.Int32).setFacets(new Facets().setNullable(false)));
                properties.add(new SimpleProperty().setName("Description").setType(EdmSimpleTypeKind.String).setFacets(new Facets().setNullable(false).setMaxLength(20)));
                properties.add(new SimpleProperty().setName("IsChanged").setType(EdmSimpleTypeKind.Boolean).setFacets(new Facets().setNullable(false)));
                //Key
                List<PropertyRef> keyProperties = new ArrayList<PropertyRef>();
                keyProperties.add(new PropertyRef().setName("TenantId"));
                Key key = new Key().setKeys(keyProperties);

                return new EntityType().setName(ENTITY_TYPE_TENANT.getName())
                        .setProperties(properties)
                        .setKey(key);

            }
        }

        return null;
    }

    @Override
    public EntityContainerInfo getEntityContainerInfo(String name) throws ODataException {
        if (name == null || ENTITY_CONTAINER.equals(name)) {
            return new EntityContainerInfo().setName(ENTITY_CONTAINER).setDefaultEntityContainer(true);
        }

        return null;
    }

    @Override
    public EntitySet getEntitySet(String entityContainer, String name) throws ODataException {
        if (ENTITY_CONTAINER.equals(entityContainer)) {
            if (ENTITY_SET_NAME_TENANTS.equals(name)) {
                return new EntitySet().setName(name).setEntityType(ENTITY_TYPE_TENANT);
            }
        }
        return null;
    }
}
