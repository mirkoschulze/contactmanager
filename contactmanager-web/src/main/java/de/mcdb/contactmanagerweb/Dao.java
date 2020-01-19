package de.mcdb.contactmanagerweb;

import com.querydsl.jpa.impl.JPAQuery;
import de.mcdb.contactmanagerapi.datamodel.Company;
import de.mcdb.contactmanagerapi.datamodel.Division;
import static de.mcdb.contactmanagerapi.datamodel.QCompany.company;
import static de.mcdb.contactmanagerapi.datamodel.QDivision.division;
import static de.mcdb.contactmanagerapi.datamodel.QStaffer.staffer;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

/**
 * Contains methods to interact with {@link Staffer}, {@link Division} and
 * {@link Company} entities.
 * <p>
 * Contains methods to find all entries from a table, find entities by their id,
 * persist new entities and to remove existing entites.
 *
 * @author Mirko Schulze
 */
@Stateless
public class Dao {

    private Hibernator hibernator;
    private final EntityManager EM;

    /**
     *
     * @param persistenceUnit name of the used persistence unit
     */
    public Dao() {
        this.hibernator = new Hibernator();
        this.EM = this.hibernator.getEntityManager();
    }

    //<editor-fold defaultstate="collapsed" desc="findAllFrom">
    /**
     * Queries the database for all entries of {@link Staffer} and returns the
     * results as {@link List}.
     *
     * @return List&lt;Staffer&gt; - a {@link List}&lt;{@link Staffer}&gt; with
     * all found entities
     */
    public List<Staffer> findAllFromStaffer() {
        return new JPAQuery<>(EM).select(staffer).from(staffer).fetch();
    }

    /**
     * Queries the database for all entries of {@link Division} and returns the
     * results as {@link List}.
     *
     * @return List&lt;Division&gt; - a {@link List}&lt;{@link Division}&gt;
     * with all found entities
     */
    public List<Division> findAllFromDivision() {
        return new JPAQuery<>(EM).select(division).from(division).fetch();
    }

    /**
     * Queries the database for all entries of {@link Company} and returns the
     * results as {@link List}.
     *
     * @return List&lt;Company&gt; - a {@link List}&lt;{@link Company}&gt; with
     * all found entities
     */
    public List<Company> findAllFromCompany() {
        return new JPAQuery<>(EM).select(company).from(company).fetch();
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
        return EM.find(Staffer.class, id);
    }

    /**
     * Queries the database for the {@link Division} with the submitted id and
     * returns the result.
     *
     * @param id the unique identifier of the sought-after {@link Division}
     * @return Division - the {@link Division} with the matching id
     */
    public Division findDivisionById(long id) {
        return EM.find(Division.class, id);
    }

    /**
     * Queries the database for the {@link Company} with the submitted id and
     * returns the result.
     *
     * @param id the unique identifier of the sought-after {@link Company}
     * @return Company - the {@link Company} with the matching id
     */
    public Company findCompanyById(long id) {
        return EM.find(Company.class, id);
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="persist">
    /**
     * Saves the submitted {@link Staffer} to the persistence context.
     *
     * @param staffer the {@link Staffer} to persist
     */
    public void persistStaffer(Staffer staffer) {
        EM.getTransaction().begin();
        if (staffer.getDivision() != null) {
            Division division = EM.find(Division.class, staffer.getDivision().getId());
            division.addStaffer(staffer);
        }
        EM.persist(staffer);
        EM.getTransaction().commit();
    }

    /**
     * Saves the submitted {@link Division} to the persistence context.
     *
     * @param division the {@link Division} to persist
     */
    public void persistDivision(Division division) {
        EM.getTransaction().begin();
        if (division.getCompany() != null) {
            Company company = EM.find(Company.class, division.getCompany().getId());
            company.addDivision(division);
        }
        EM.persist(division);
        EM.getTransaction().commit();
    }

    /**
     * Saves the submitted {@link Company} to the persistence context.
     *
     * @param company the {@link Company} to persist
     */
    public void persistCompany(Company company) {
        EM.getTransaction().begin();
        EM.persist(company);
        EM.getTransaction().commit();
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
        EM.getTransaction().begin();

        Staffer staffer = EM.find(Staffer.class, id);

        if (updatedStaffer.getDivision() != null) {
            Division division = EM.find(Division.class, updatedStaffer.getDivision().getId());

            staffer.setForeName(updatedStaffer.getForeName());
            staffer.setSurName(updatedStaffer.getSurName());
            division.addStaffer(staffer);
        }
        EM.getTransaction().commit();

    }

    /**
     * Updates the {@link Division} with the submitted id to the values from the
     * submitted Division.
     *
     * @param id the id of the {@link Division} to update
     * @param updatedDivision {@link Division} with the new values
     */
    public void updateDivision(long id, Division updatedDivision) {
        EM.getTransaction().begin();

        Division division = EM.find(Division.class, id);

        if (updatedDivision.getCompany() != null) {
            Company company = EM.find(Company.class, updatedDivision.getCompany().getId());

            division.setName(updatedDivision.getName());
            division.setStaffers(updatedDivision.getStaffers());
            company.addDivision(division);
        }

        EM.getTransaction().commit();

    }

    /**
     * Updates the {@link Company} with the submitted id to the values from the
     * submitted Company.
     *
     * @param id the id of the {@link Company} to update
     * @param updatedCompany {@link Company} with the new values
     */
    public void updateCompany(long id, Company updatedCompany) {
        EM.getTransaction().begin();

        Company company = EM.find(Company.class, id);

        company.setName(updatedCompany.getName());

        EM.getTransaction().commit();

    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="remove">
    /**
     * Removes the submitted {@link Staffer} from the database.
     *
     * @param id identifier of the {@link Staffer} to remove
     */
    public void removeStaffer(long id) {

        EM.getTransaction().begin();
        Staffer staffer = EM.find(Staffer.class, id);

        if (staffer.getDivision() != null) {
            Division division = EM.find(Division.class, staffer.getDivision().getId());
            division.removeStaffer(staffer);
        }

        EM.remove(staffer);

        EM.getTransaction().commit();
    }

    /**
     * Removes the submitted {@link Division} from the database.
     *
     * @param id identifier of the {@link Division} to remove
     */
    public void removeDivision(long id) {

        EM.getTransaction().begin();
        Division division = EM.find(Division.class, id);

        if (division.getCompany() != null) {
            Company company = EM.find(Company.class, division.getCompany().getId());
            company.removeDivision(division);
        }

        if (division.getStaffers() != null && !division.getStaffers().isEmpty()) {
            List<Staffer> staffers = new ArrayList<>();
            staffers.addAll(division.getStaffers());
            for (Staffer staffer : staffers) {
                staffer = EM.find(Staffer.class, staffer.getId());
                division.removeStaffer(staffer);
            }
        }

        EM.remove(division);
        EM.getTransaction().commit();
    }

    /**
     * Removes the submitted {@link Company} from the database.
     *
     * @param id identifier of the {@link Company} to remove
     */
    public void removeCompany(long id) {
        EM.getTransaction().begin();

        Company company = EM.find(Company.class, id);

        if (company.getDivisions() != null && !company.getDivisions().isEmpty()) {
            List<Division> divisions = new ArrayList<>();
            divisions.addAll(company.getDivisions());
            for (Division division : divisions) {
                division = EM.find(Division.class, division.getId());
                company.removeDivision(division);
            }
        }

        EM.remove(company);
        EM.getTransaction().commit();
    }
    //</editor-fold>

    /**
     * Closes the {@link EntityManager} for this instance.
     */
    public void destroy() {
        if (this.EM != null && this.EM.isOpen()) {
            if (this.EM.getTransaction().isActive()) {
                this.EM.clear();
            }
            this.EM.close();
        }
        if (this.hibernator != null) {
            this.hibernator.shutdown();
        }

    }

}
