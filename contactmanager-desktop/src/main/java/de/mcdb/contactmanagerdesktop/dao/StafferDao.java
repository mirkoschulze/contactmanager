package de.mcdb.contactmanagerdesktop.dao;

import ch.qos.logback.classic.Logger;
import com.querydsl.jpa.impl.JPAQuery;
import de.mcdb.contactmanagerapi.Dao;
import de.mcdb.contactmanagerapi.datamodel.Division;
import static de.mcdb.contactmanagerapi.datamodel.QStaffer.staffer;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import org.slf4j.LoggerFactory;

/**
 * Data access object to interact with {@link Staffer} entities.
 *
 * @author Mirko
 */
@Stateless
public class StafferDao implements Dao<Staffer> {

    private static final Logger L = (Logger) LoggerFactory.getLogger(StafferDao.class);

    private final EntityManager em = HibernateUtils.getEntityManager();

    @Override
    public List<Staffer> findAll() {
        L.info("Quering for all [{}] entities", Staffer.class.getSimpleName());
        return new JPAQuery<>(em).select(staffer).from(staffer).fetch();
    }

    @Override
    public Staffer findById(long id) {
        L.info("Quering for [{}] with ID {}", Staffer.class.getSimpleName(), id);
        return em.find(Staffer.class, id);
    }

    @Override
    public void persist(Staffer entity) {
        L.info("Persisting [{}] {}", Staffer.class.getSimpleName(), entity.toEnhancedLine());
        em.getTransaction().begin();
        if (entity.getDivision() != null) {
            Division division = em.find(Division.class, entity.getDivision().getId());
            division.addStaffer(entity);
            L.info("[{}] {} added to [{}] {}", Staffer.class.getSimpleName(), entity.toSimpleLine(), Division.class.getSimpleName(), division.toSimpleLine());
        }
        em.persist(entity);
        L.info("[{}] {} persisted", Staffer.class.getSimpleName(), entity.toSimpleLine());
        em.getTransaction().commit();
    }

    @Override
    public void update(long id, Staffer entity) {
        L.info("Updating [{}] with id {}", Staffer.class.getSimpleName(), id);
        em.getTransaction().begin();

        Staffer staffer = em.find(Staffer.class, id);

        if (entity.getDivision() != null) {
            Division division = em.find(Division.class, entity.getDivision().getId());

            staffer.setForeName(entity.getForeName());
            staffer.setSurName(entity.getSurName());
            division.addStaffer(staffer);
            L.info("[{}] {} added to [{}] {}", Staffer.class.getSimpleName(), staffer.toSimpleLine(), Division.class.getSimpleName(), division.toSimpleLine());
        }
        em.getTransaction().commit();

        L.info("[{}] {} updated", Staffer.class.getSimpleName(), staffer.toSimpleLine());
    }

    @Override
    public void remove(long id) {
        L.info("Removing [{}] with id {}", Staffer.class.getSimpleName(), id);

        em.getTransaction().begin();
        Staffer staffer = em.find(Staffer.class, id);

        if (staffer.getDivision() != null) {
            Division division = em.find(Division.class, staffer.getDivision().getId());
            division.removeStaffer(staffer);
            L.info("[{}] {} removed from [{}] {}", Staffer.class.getSimpleName(), staffer.toSimpleLine(), Division.class.getSimpleName(), staffer.getDivision().toSimpleLine());
        }

        em.remove(staffer);

        em.getTransaction().commit();
        L.info("[{}] {} removed", Staffer.class.getSimpleName(), staffer.toSimpleLine());
    }

    /**
     * Closes the {@link EntityManager} for this instance.
     */
    @Override
    public void destroy() {
        L.info("Destroying [{}]", StafferDao.class.getSimpleName());
        if (this.em != null && this.em.isOpen()) {
            if (this.em.getTransaction().isActive()) {
                L.info("Clearing [{}]", EntityManager.class.getSimpleName());
                this.em.clear();
            }
            L.info("Closing [{}]", EntityManager.class.getSimpleName());
            this.em.close();
        }
        L.info("[{}] destroyed", StafferDao.class.getSimpleName());

    }

}
