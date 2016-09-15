/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session.service;
import database.Session;
import jpa.session.MyJpaSessionFactory;
import org.hibernate.SessionFactory;

/**
 *
 * @author Yurij
 */
public class SessionService {
    static private MyJpaSessionFactory jpaSessionFactory;
    
    public SessionService(MyJpaSessionFactory jpaSessionFactory) {
        this.jpaSessionFactory = jpaSessionFactory;
    }
/*  public Session openSession(String userId, String TenantId, String Client) {
        database.Session session = new Session();
        sessionFactory.getCurrentSession().save(session);
        return session;
    }*/
    public Session getSessionById(String sessionId) {
        return (database.Session) jpaSessionFactory.getCurrentSession().get(database.Session.class, sessionId);
    }
    public void closeSession(database.Session session) {
        
    }
}
