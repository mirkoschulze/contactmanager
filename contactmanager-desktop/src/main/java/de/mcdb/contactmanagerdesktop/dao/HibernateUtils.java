package de.mcdb.contactmanagerdesktop.dao;

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

    private static final Logger L = (Logger) LoggerFactory.getLogger(HibernateUtils.class);

    private static final String PERSISTENCE_UNIT = "ContactManagerDesktopPU";

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
     * Shuts down the {@link EntityManagerFactory}.
     */
    public static void shutdown() {
        if (EMF != null && EMF.isOpen()) {
            L.info("Closing [{}]", EntityManagerFactory.class.getSimpleName());
            EMF.close();
        }
    }

}
