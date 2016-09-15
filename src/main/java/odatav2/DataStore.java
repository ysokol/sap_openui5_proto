package odatav2;

import database.Tenant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class DataStore {

    //Data accessors
    public Map<String, Object> getTenant(int id) {
        Map<String, Object> data = null;

        Calendar updated = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        switch (id) {
            case 1:
                updated.set(2012, 11, 11, 11, 11, 11);
                data = createCar(1, "F1 W03", 1, 189189.43, "EUR", "2012", updated, "file://imagePath/w03");
                break;

            case 2:
                updated.set(2013, 11, 11, 11, 11, 11);
                data = createCar(2, "F1 W04", 1, 199999.99, "EUR", "2013", updated, "file://imagePath/w04");
                break;

            case 3:
                updated.set(2012, 12, 12, 12, 12, 12);
                data = createCar(3, "F2012", 2, 137285.33, "EUR", "2012", updated, "http://pathToImage/f2012");
                break;

            case 4:
                updated.set(2013, 12, 12, 12, 12, 12);
                data = createCar(4, "F2013", 2, 145285.00, "EUR", "2013", updated, "http://pathToImage/f2013");
                break;

            case 5:
                updated.set(2011, 11, 11, 11, 11, 11);
                data = createCar(5, "F1 W02", 1, 167189.00, "EUR", "2011", updated, "file://imagePath/wXX");
                break;

            default:
                break;
        }

        return data;
    }

    private Map<String, Object> createCar(int carId, String model, int manufacturerId, double price, String currency, String modelYear, Calendar updated, String imagePath) {
        Map<String, Object> data = new HashMap<String, Object>();

        data.put("Id", carId);
        data.put("Model", model);
        data.put("ManufacturerId", manufacturerId);
        data.put("Price", price);
        data.put("Currency", currency);
        data.put("ModelYear", modelYear);
        data.put("Updated", updated);
        data.put("ImagePath", imagePath);

        return data;
    }

    public Map<String, Object> getManufacturer(int id) {
        Map<String, Object> data = null;
        Calendar date = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        switch (id) {
            case 1:
                Map<String, Object> addressStar = createAddress("Star Street 137", "Stuttgart", "70173", "Germany");
                date.set(1954, 7, 4);
                data = createManufacturer(1, "Star Powered Racing", addressStar, date);
                break;

            case 2:
                Map<String, Object> addressHorse = createAddress("Horse Street 1", "Maranello", "41053", "Italy");
                date.set(1929, 11, 16);
                data = createManufacturer(2, "Horse Powered Racing", addressHorse, date);
                break;

            default:
                break;
        }

        return data;
    }

    private Map<String, Object> createManufacturer(int id, String name, Map<String, Object> address, Calendar updated) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("Id", id);
        data.put("Name", name);
        data.put("Address", address);
        data.put("Updated", updated);
        return data;
    }

    private Map<String, Object> createAddress(String street, String city, String zipCode, String country) {
        Map<String, Object> address = new HashMap<String, Object>();
        address.put("Street", street);
        address.put("City", city);
        address.put("ZipCode", zipCode);
        address.put("Country", country);
        return address;
    }

    public List<Map<String, Object>> getTenants() {
        List<Map<String, Object>> tenants = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = null;
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
                row = new HashMap<String, Object>();
                row.put("TenantId", tenant.getTenantId());
                row.put("Description", tenant.getDescription());
                row.put("IsChanged", false);
                tenants.add(row);
            }
            session.close();
        } catch (Throwable ex) {

        }
        return tenants;
    }

    public int getTenantsCount() {
        List<Map<String, Object>> tenants = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = null;
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
                row = new HashMap<String, Object>();
                row.put("TenantId", tenant.getTenantId());
                row.put("Description", tenant.getDescription());
                tenants.add(row);
            }
            session.close();
        } catch (Throwable ex) {

        }
        return tenants.size();
    }

    public int createTenant(Tenant tenant) {
        try {
            final Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(Tenant.class);
            final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            final SessionFactory factory = configuration.buildSessionFactory(builder.build());
            final Session session = factory.openSession();
            session.beginTransaction();
            session.save(tenant);
            session.getTransaction().commit();
            session.close();
        } catch (Throwable ex) {
            String error = ex.toString();
        }
        return tenant.getTenantId();
    }

    public void updateTenant(Tenant tenant) {
        try {
            final Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(Tenant.class);
            final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            final SessionFactory factory = configuration.buildSessionFactory(builder.build());
            final Session session = factory.openSession();
            session.beginTransaction();
            session.update(tenant);
            session.getTransaction().commit();
            session.close();
        } catch (Throwable ex) {
            String error = ex.toString();
        }
    }

    public void deleteTenant(Tenant tenant) {
        try {
            final Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(Tenant.class);
            final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
            final SessionFactory factory = configuration.buildSessionFactory(builder.build());
            final Session session = factory.openSession();
            session.beginTransaction();
            session.delete(tenant);
            session.getTransaction().commit();
            session.close();
        } catch (Throwable ex) {
            String error = ex.toString();
        }
    }

    public List<Map<String, Object>> getManufacturers() {
        List<Map<String, Object>> manufacturers = new ArrayList<Map<String, Object>>();
        manufacturers.add(getManufacturer(1));
        manufacturers.add(getManufacturer(2));
        return manufacturers;
    }

    public List<Map<String, Object>> getCarsFor(int manufacturerId) {
        //List<Map<String, Object>> cars = getCars();
        List<Map<String, Object>> carsForManufacturer = new ArrayList<Map<String, Object>>();

        /*for (Map<String, Object> car : cars) {
            if (Integer.valueOf(manufacturerId).equals(car.get("ManufacturerId"))) {
                carsForManufacturer.add(car);
            }
        }*/
        return carsForManufacturer;
    }

    public Map<String, Object> getManufacturerFor(int carId) {
        /*Map<String, Object> car = getCar(carId);
        if (car != null) {
            Object manufacturerId = car.get("ManufacturerId");
            if (manufacturerId != null) {
                return getManufacturer((Integer) manufacturerId);
            }
        }*/
        return null;
    }
}
