package de.mcdb.contactmanagerweb.dataaccess;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Class with static methods to interact with the Hibernate framework.
 *
 * @author Mirko Schulze
 */
public class HibernateUtils {

    private static final String PERSISTENCE_UNIT = "ContactManagerWebPU";

    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);

    /**
     * Creates and returns a new {@link EntityManager}.
     *
     * @return EntityManager - a new {@link EntityManager}
     */
    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /**
     * Static method to shut down the {@link EntityManagerFactory}.
     */
    public static void shutdown() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }

}
