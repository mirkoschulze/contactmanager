package de.mcdb.contactmanagerdesktop.dao;

import ch.qos.logback.classic.Logger;
import com.querydsl.jpa.impl.JPAQuery;
import de.mcdb.contactmanagerapi.Dao;
import de.mcdb.contactmanagerapi.datamodel.Company;
import de.mcdb.contactmanagerapi.datamodel.Division;
import static de.mcdb.contactmanagerapi.datamodel.QCompany.company;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import org.slf4j.LoggerFactory;

/**
 * Data access object to interact with {@link Company} entities.
 *
 * @author Mirko
 */
@Stateless
public class CompanyDao implements Dao<Company> {

    private static final Logger L = (Logger) LoggerFactory.getLogger(CompanyDao.class);

    private final EntityManager em = HibernateUtils.getEntityManager();

    @Override
    public List<Company> findAll() {
        L.info("Quering for all [{}] entities", Company.class.getSimpleName());
        return new JPAQuery<>(em).select(company).from(company).fetch();
    }

    @Override
    public Company findById(long id) {
        L.info("Quering for [{}] with ID {}", Company.class.getSimpleName(), id);
        return em.find(Company.class, id);
    }

    @Override
    public void persist(Company entity) {
        L.info("Persisting [{}] {}", Company.class.getSimpleName(), entity.toSimpleLine());
        em.getTransaction().begin();
        em.persist(entity);
        L.info("[{}] {} persisted", Company.class.getSimpleName(), entity.toSimpleLine());
        em.getTransaction().commit();
    }

    @Override
    public void update(long id, Company entity) {
        L.info("Updating [{}] with id {}", Division.class.getSimpleName(), id);
        em.getTransaction().begin();

        Company company = em.find(Company.class, id);

        company.setName(entity.getName());

        em.getTransaction().commit();

        L.info("[{}] {} updated", Company.class.getSimpleName(), company.toSimpleLine());
    }

    @Override
    public void remove(long id) {
        L.info("Removing [{}] with id {}", Company.class.getSimpleName(), id);
        em.getTransaction().begin();

        Company company = em.find(Company.class, id);

        if (company.getDivisions() != null && !company.getDivisions().isEmpty()) {
            List<Division> divisions = new ArrayList<>();
            divisions.addAll(company.getDivisions());
            for (Division division : divisions) {
                division = em.find(Division.class, division.getId());
                company.removeDivision(division);
                L.info("[{}] {} removed from [{}] {}", Staffer.class.getSimpleName(), division.toSimpleLine(), Company.class.getSimpleName(), company.toSimpleLine());
            }
        }

        em.remove(company);
        em.getTransaction().commit();
        L.info("[{}] {} removed", Company.class.getSimpleName(), company.toSimpleLine());
    }

    /**
     * Closes the {@link EntityManager} for this instance.
     */
    @Override
    public void destroy() {
        L.info("Destroying [{}]", CompanyDao.class.getSimpleName());
        if (this.em != null && this.em.isOpen()) {
            if (this.em.getTransaction().isActive()) {
                L.info("Clearing [{}]", EntityManager.class.getSimpleName());
                this.em.clear();
            }
            L.info("Closing [{}]", EntityManager.class.getSimpleName());
            this.em.close();
        }
        L.info("[{}] destroyed", CompanyDao.class.getSimpleName());

    }

}
