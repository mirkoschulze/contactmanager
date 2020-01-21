package de.mcdb.contactmanagerdesktop.dao;

import ch.qos.logback.classic.Logger;
import com.querydsl.jpa.impl.JPAQuery;
import de.mcdb.contactmanagerapi.Dao;
import de.mcdb.contactmanagerapi.datamodel.Company;
import de.mcdb.contactmanagerapi.datamodel.Division;
import static de.mcdb.contactmanagerapi.datamodel.QDivision.division;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import org.slf4j.LoggerFactory;

/**
 * Data access object to interact with {@link Division} entities.
 *
 * @author Mirko
 */
@Stateless
public class DivisionDao implements Dao<Division> {

    private static final Logger L = (Logger) LoggerFactory.getLogger(DivisionDao.class);

    private final EntityManager em = HibernateUtils.getEntityManager();

    @Override
    public List<Division> findAll() {
        L.info("Quering for all [{}] entities", Division.class.getSimpleName());
        return new JPAQuery<>(em).select(division).from(division).fetch();
    }

    @Override
    public Division findById(long id) {
        L.info("Quering for [{}] with ID {}", Division.class.getSimpleName(), id);
        return em.find(Division.class, id);
    }

    @Override
    public void persist(Division entity) {
        L.info("Persisting [{}] {}", Division.class.getSimpleName(), entity.toSimpleLine());
        em.getTransaction().begin();
        if (entity.getCompany() != null) {
            Company company = em.find(Company.class, entity.getCompany().getId());
            company.addDivision(entity);
            L.info("[{}] {} added to [{}] {}", Division.class.getSimpleName(), entity.toSimpleLine(), Company.class.getSimpleName(), company.toSimpleLine());
        }
        em.persist(entity);
        L.info("[{}] {} persisted", Division.class.getSimpleName(), entity.toSimpleLine());
        em.getTransaction().commit();
    }

    @Override
    public void update(long id, Division entity) {
        L.info("Updating [{}] with id {}", Division.class.getSimpleName(), id);
        em.getTransaction().begin();

        Division division = em.find(Division.class, id);

        if (entity.getCompany() != null) {
            Company company = em.find(Company.class, entity.getCompany().getId());

            division.setName(entity.getName());
            division.setStaffers(entity.getStaffers());
            company.addDivision(division);
            L.info("[{}] {} added to [{}] {}", Division.class.getSimpleName(), division.toSimpleLine(), Company.class.getSimpleName(), company.toSimpleLine());
        }

        em.getTransaction().commit();

        L.info("[{}] {} updated", Division.class.getSimpleName(), division.toSimpleLine());
    }

    @Override
    public void remove(long id) {
        L.info("Removing [{}] with id {}", Division.class.getSimpleName(), id);

        em.getTransaction().begin();
        Division division = em.find(Division.class, id);

        if (division.getCompany() != null) {
            Company company = em.find(Company.class, division.getCompany().getId());
            company.removeDivision(division);
            L.info("[{}] {} removed from [{}] {}", Staffer.class.getSimpleName(), division.toSimpleLine(), Company.class.getSimpleName(), company.toSimpleLine());
        }

        if (division.getStaffers() != null && !division.getStaffers().isEmpty()) {
            List<Staffer> staffers = new ArrayList<>();
            staffers.addAll(division.getStaffers());
            for (Staffer staffer : staffers) {
                staffer = em.find(Staffer.class, staffer.getId());
                division.removeStaffer(staffer);
                L.info("[{}] {} removed from [{}] {}", Staffer.class.getSimpleName(), staffer.toSimpleLine(), Division.class.getSimpleName(), division.toSimpleLine());
            }
        }

        em.remove(division);
        em.getTransaction().commit();
        L.info("[{}] {} removed", Division.class.getSimpleName(), division.toSimpleLine());
    }

    /**
     * Closes the {@link EntityManager} for this instance.
     */
    @Override
    public void destroy() {
        L.info("Destroying [{}]", DivisionDao.class.getSimpleName());
        if (this.em != null && this.em.isOpen()) {
            if (this.em.getTransaction().isActive()) {
                L.info("Clearing [{}]", EntityManager.class.getSimpleName());
                this.em.clear();
            }
            L.info("Closing [{}]", EntityManager.class.getSimpleName());
            this.em.close();
        }
        L.info("[{}] destroyed", DivisionDao.class.getSimpleName());

    }

}
