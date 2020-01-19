package de.mcdb.contactmanagerweb;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

/**
 * Class with methods to interact with the Hibernate framework.
 *
 * @author Mirko Schulze
 */
public class Hibernator {

    private final EntityManagerFactory EMF;

    /**
     * Loads the persistence.xml and creates the {@link EntityManagerFactory}.
     *
     * @param persistenceUnit
     * @throws PersistenceException if the persistence unit can not be loaded
     * properly
     */
    public Hibernator() throws PersistenceException {
        EMF = Persistence.createEntityManagerFactory("ContactManagerWebPU");
    }

    /**
     * Creates and returns a new {@link EntityManager}.
     *
     * @return EntityManager - a new {@link EntityManager}
     */
    public EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /**
     * Static method to shut down the {@link EntityManagerFactory}.
     */
    public void shutdown() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }

}
