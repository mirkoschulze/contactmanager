package de.mcdb.contactmanagerapi;

import ch.qos.logback.classic.Logger;
import com.querydsl.jpa.impl.JPAQuery;
import de.mcdb.contactmanagerapi.datamodel.Company;
import de.mcdb.contactmanagerapi.datamodel.Division;
import static de.mcdb.contactmanagerapi.datamodel.QCompany.company;
import static de.mcdb.contactmanagerapi.datamodel.QDivision.division;
import static de.mcdb.contactmanagerapi.datamodel.QStaffer.staffer;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.slf4j.LoggerFactory;

/**
 * Contains methods to interact with {@link Staffer}, {@link Division} and
 * {@link Company} entities.
 * <p>
 * Contains methods to find all entries from a table, find entities by their id,
 * persist new entities and to remove existing entites.
 *
 * @author Mirko Schulze
 */
public class Dao {

    private static final Logger L = (Logger) LoggerFactory.getLogger(Dao.class);

    private final EntityManager em = HibernateUtils.getEntityManager();

    //<editor-fold defaultstate="collapsed" desc="findAllFrom">
    /**
     * Queries the database for all entries of {@link Staffer} and returns the
     * results as {@link List}.
     *
     * @return List&lt;Staffer&gt; - a {@link List}&lt;{@link Staffer}&gt; with
     * all found entities
     */
    public List<Staffer> findAllFromStaffer() {
        L.info("Quering for all [{}] entities", Staffer.class.getSimpleName());
        return new JPAQuery<>(em).select(staffer).from(staffer).fetch();
    }

    /**
     * Queries the database for all entries of {@link Division} and returns the
     * results as {@link List}.
     *
     * @return List&lt;Division&gt; - a {@link List}&lt;{@link Division}&gt;
     * with all found entities
     */
    public List<Division> findAllFromDivision() {
        L.info("Quering for all [{}] entities", Division.class.getSimpleName());
        return new JPAQuery<>(em).select(division).from(division).fetch();
    }

    /**
     * Queries the database for all entries of {@link Company} and returns the
     * results as {@link List}.
     *
     * @return List&lt;Company&gt; - a {@link List}&lt;{@link Company}&gt; with
     * all found entities
     */
    public List<Company> findAllFromCompany() {
        L.info("Quering for all [{}] entities", Company.class.getSimpleName());
        return new JPAQuery<>(em).select(company).from(company).fetch();
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="findById">
    /**
     * Queries the database for the {@link Staffer} with the submitted id and
     * returns the result.
     *
     * @param id the unique identifier of the sought-after {@link Staffer}
     * @return Staffer - the {@link Staffer} with the matching id
     */
    public Staffer findStafferById(long id) {
        L.info("Quering for [{}] with ID {}", Staffer.class.getSimpleName(), id);
        return em.find(Staffer.class, id);
    }

    /**
     * Queries the database for the {@link Division} with the submitted id and
     * returns the result.
     *
     * @param id the unique identifier of the sought-after {@link Division}
     * @return Division - the {@link Division} with the matching id
     */
    public Division findDivisionById(long id) {
        L.info("Quering for [{}] with ID {}", Division.class.getSimpleName(), id);
        return em.find(Division.class, id);
    }

    /**
     * Queries the database for the {@link Company} with the submitted id and
     * returns the result.
     *
     * @param id the unique identifier of the sought-after {@link Company}
     * @return Company - the {@link Company} with the matching id
     */
    public Company findCompanyById(long id) {
        L.info("Quering for [{}] with ID {}", Company.class.getSimpleName(), id);
        return em.find(Company.class, id);
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="persist">
    /**
     * Saves the submitted {@link Staffer} to the persistence context.
     *
     * @param staffer the {@link Staffer} to persist
     */
    public void persistStaffer(Staffer staffer) {
        L.info("Persisting [{}] {}", Staffer.class.getSimpleName(), staffer.toEnhancedLine());
        em.getTransaction().begin();
        if (staffer.getDivision() != null) {
            Division division = em.find(Division.class, staffer.getDivision().getId());
            division.addStaffer(staffer);
            L.info("[{}] {} added to [{}] {}", Staffer.class.getSimpleName(), staffer.toSimpleLine(), Division.class.getSimpleName(), division.toSimpleLine());
        }
        em.persist(staffer);
        L.info("[{}] {} persisted", Staffer.class.getSimpleName(), staffer.toSimpleLine());
        em.getTransaction().commit();
    }

    /**
     * Saves the submitted {@link Division} to the persistence context.
     *
     * @param division the {@link Division} to persist
     */
    public void persistDivision(Division division) {
        L.info("Persisting [{}] {}", Division.class.getSimpleName(), division.toSimpleLine());
        em.getTransaction().begin();
        if (division.getCompany() != null) {
            Company company = em.find(Company.class, division.getCompany().getId());
            company.addDivision(division);
            L.info("[{}] {} added to [{}] {}", Division.class.getSimpleName(), division.toSimpleLine(), Company.class.getSimpleName(), company.toSimpleLine());
        }
        em.persist(division);
        L.info("[{}] {} persisted", Division.class.getSimpleName(), division.toSimpleLine());
        em.getTransaction().commit();
    }

    /**
     * Saves the submitted {@link Company} to the persistence context.
     *
     * @param company the {@link Company} to persist
     */
    public void persistCompany(Company company) {
        L.info("Persisting [{}] {}", Company.class.getSimpleName(), company.toSimpleLine());
        em.getTransaction().begin();
        em.persist(company);
        L.info("[{}] {} persisted", Company.class.getSimpleName(), company.toSimpleLine());
        em.getTransaction().commit();
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="update">
    /**
     * Updates the {@link Staffer} with the submitted id to the values from the
     * submitted Staffer.
     *
     * @param id the id of the {@link Staffer} to update
     * @param updatedStaffer {@link Staffer} with the new values
     */
    public void updateStaffer(long id, Staffer updatedStaffer) {
        L.info("Updating [{}] with id {}", Staffer.class.getSimpleName(), id);
        em.getTransaction().begin();

        Staffer staffer = em.find(Staffer.class, id);

        if (updatedStaffer.getDivision() != null) {
            Division division = em.find(Division.class, updatedStaffer.getDivision().getId());

            staffer.setForeName(updatedStaffer.getForeName());
            staffer.setSurName(updatedStaffer.getSurName());
            division.addStaffer(staffer);
            L.info("[{}] {} added to [{}] {}", Staffer.class.getSimpleName(), staffer.toSimpleLine(), Division.class.getSimpleName(), division.toSimpleLine());
        }
        em.getTransaction().commit();

        L.info("[{}] {} updated", Staffer.class.getSimpleName(), staffer.toSimpleLine());
    }

    /**
     * Updates the {@link Division} with the submitted id to the values from the
     * submitted Division.
     *
     * @param id the id of the {@link Division} to update
     * @param updatedDivision {@link Division} with the new values
     */
    public void updateDivision(long id, Division updatedDivision) {
        L.info("Updating [{}] with id {}", Division.class.getSimpleName(), id);
        em.getTransaction().begin();

        Division division = em.find(Division.class, id);

        if (updatedDivision.getCompany() != null) {
            Company company = em.find(Company.class, updatedDivision.getCompany().getId());

            division.setName(updatedDivision.getName());
            division.setStaffers(updatedDivision.getStaffers());
            company.addDivision(division);
            L.info("[{}] {} added to [{}] {}", Division.class.getSimpleName(), division.toSimpleLine(), Company.class.getSimpleName(), company.toSimpleLine());
        }

        em.getTransaction().commit();

        L.info("[{}] {} updated", Division.class.getSimpleName(), division.toSimpleLine());
    }

    /**
     * Updates the {@link Company} with the submitted id to the values from the
     * submitted Company.
     *
     * @param id the id of the {@link Company} to update
     * @param updatedCompany {@link Company} with the new values
     */
    public void updateCompany(long id, Company updatedCompany) {
        L.info("Updating [{}] with id {}", Division.class.getSimpleName(), id);
        em.getTransaction().begin();

        Company company = em.find(Company.class, id);

        company.setName(updatedCompany.getName());

        em.getTransaction().commit();

        L.info("[{}] {} updated", Company.class.getSimpleName(), company.toSimpleLine());
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="remove">
    /**
     * Removes the submitted {@link Staffer} from the database.
     *
     * @param id identifier of the {@link Staffer} to remove
     */
    public void removeStaffer(long id) {
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
     * Removes the submitted {@link Division} from the database.
     *
     * @param id identifier of the {@link Division} to remove
     */
    public void removeDivision(long id) {
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
     * Removes the submitted {@link Company} from the database.
     *
     * @param id identifier of the {@link Company} to remove
     */
    public void removeCompany(long id) {
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
    //</editor-fold>

    /**
     * Closes the {@link EntityManager} for this instance.
     */
    public void destroy() {
        L.info("Destroying [{}]", Dao.class.getSimpleName());
        if (em != null && em.isOpen()) {
            if (em.getTransaction().isActive()) {
                L.info("Clearing [{}]", EntityManager.class.getSimpleName());
                em.clear();
            }
            L.info("Closing [{}]", EntityManager.class.getSimpleName());
            em.close();
        }
        L.info("[{}] destroyed", Dao.class.getSimpleName());
    }

}
