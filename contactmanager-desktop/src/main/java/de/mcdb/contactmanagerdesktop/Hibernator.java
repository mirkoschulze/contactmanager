package de.mcdb.contactmanagerdesktop;

import ch.qos.logback.classic.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import org.slf4j.LoggerFactory;

/**
 * Class with methods to interact with the Hibernate framework.
 *
 * @author Mirko Schulze
 */
public class Hibernator {

    private static final Logger L = (Logger) LoggerFactory.getLogger(Hibernator.class);

    private final EntityManagerFactory EMF;

    /**
     * Loads the persistence.xml and creates the {@link EntityManagerFactory}.
     *
     * @param persistenceUnit
     * @throws PersistenceException if the persistence unit can not be loaded
     * properly
     */
    public Hibernator(String persistenceUnit) throws PersistenceException{
            EMF = Persistence.createEntityManagerFactory(persistenceUnit);
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
            L.info("Closing [{}]", EntityManagerFactory.class.getSimpleName());
            EMF.close();
        }
    }

}
