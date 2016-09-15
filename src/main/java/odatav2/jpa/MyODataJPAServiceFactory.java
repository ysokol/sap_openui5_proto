/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odatav2.jpa;

import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAServiceFactory;
import org.apache.olingo.odata2.jpa.processor.ref.factory.JPAEntityManagerFactory;

/**
 *
 * @author Yurij
 */
public class MyODataJPAServiceFactory extends MyAbstractODataJPAServiceFactory {

    private static final String PUNIT_NAME = "com.mycompany_mavenproject_war_1.0-SNAPSHOTPU";

    @Override
    public ODataJPAContext initializeODataJPAContext() {
        try {
            ODataJPAContext oDataJPAContext = getODataJPAContext();
            oDataJPAContext.setEntityManagerFactory(JPAEntityManagerFactory.getEntityManagerFactory(PUNIT_NAME));
            oDataJPAContext.setPersistenceUnitName(PUNIT_NAME);
            return oDataJPAContext;
        } catch (Exception e) {

            // Deal with e as you please.
            //e may be any type of exception at all.
        }
        return null;
    }
}
