package de.mcdb.contactmanagerapi;

import ch.qos.logback.classic.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.slf4j.LoggerFactory;

/**
 * Class with static methods to interact with the Hibernate framework.
 *
 * @author Mirko Schulze
 */
public class HibernateUtils {

    private static final String PERSISTENCE_UNIT = "ContactManagerPU";

    private static final Logger L = (Logger) LoggerFactory.getLogger(HibernateUtils.class);

    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);

    /**
     * Returns the configured {@link EntityManagerFactory}.
     *
     * @return EntityManagerFactory - the configured
     * {@link EntityManagerFactory}
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return EMF;
    }

    /**
     * Creates and returns a new {@link EntityManager}.
     *
     * @return EntityManager - a new {@link EntityManager}
     */
    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /**
     * Shuts down the {@link EntityManagerFactory} and the logging process for
     * this class.
     */
    public static void shutdown() {
        if (EMF != null && EMF.isOpen()) {
            L.info("Closing [{}]", EntityManagerFactory.class.getSimpleName());
            EMF.close();
        }
    }

}
