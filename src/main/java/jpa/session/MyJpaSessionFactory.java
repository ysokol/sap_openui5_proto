/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.session;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import session.service.SessionService;

/**
 *
 * @author Yurij
 */
public class MyJpaSessionFactory {
    final private SessionFactory sessionFactory;
    final private org.hibernate.Session currentSession;
    public MyJpaSessionFactory() {
        final Configuration configuration = new Configuration().configure();
        configuration.addAnnotatedClass(database.Session.class);
        final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());
        currentSession = sessionFactory.openSession();
    }
    public org.hibernate.Session getCurrentSession() {
        return currentSession;
    }
    public void destroy() {
        currentSession.flush();
        currentSession.close();
    }
    
}
