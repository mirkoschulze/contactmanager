package de.mcdb.contactmanagerdesktop;

import ch.qos.logback.classic.Logger;
import com.querydsl.jpa.impl.JPAQuery;
import de.mcdb.contactmanagerapi.datamodel.Company;
import de.mcdb.contactmanagerapi.datamodel.Division;
import static de.mcdb.contactmanagerapi.datamodel.QDivision.division;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mirko
 */
public class DivisionDao implements de.mcdb.contactmanagerapi.Dao<Division> {

    private static final Logger L = (Logger) LoggerFactory.getLogger(Dao.class);

    private final EntityManager EM = HibernateUtils.getEntityManager();

    @Override
    public List<Division> findAll() {
        L.info("Quering for all [{}] entities", Division.class.getSimpleName());
        return new JPAQuery<>(EM).select(division).from(division).fetch();
    }

    @Override
    public Division findById(long id) {
        L.info("Quering for [{}] with ID {}", Division.class.getSimpleName(), id);
        return EM.find(Division.class, id);
    }

    @Override
    public void persist(Division entity) {
        L.info("Persisting [{}] {}", Division.class.getSimpleName(), entity.toSimpleLine());
        EM.getTransaction().begin();
        if (entity.getCompany() != null) {
            Company company = EM.find(Company.class, entity.getCompany().getId());
            company.addDivision(entity);
            L.info("[{}] {} added to [{}] {}", Division.class.getSimpleName(), entity.toSimpleLine(), Company.class.getSimpleName(), company.toSimpleLine());
        }
        EM.persist(entity);
        L.info("[{}] {} persisted", Division.class.getSimpleName(), entity.toSimpleLine());
        EM.getTransaction().commit();
    }

    @Override
    public void update(long id, Division entity) {
        L.info("Updating [{}] with id {}", Division.class.getSimpleName(), id);
        EM.getTransaction().begin();

        Division division = EM.find(Division.class, id);

        if (entity.getCompany() != null) {
            Company company = EM.find(Company.class, entity.getCompany().getId());

            division.setName(entity.getName());
            division.setStaffers(entity.getStaffers());
            company.addDivision(division);
            L.info("[{}] {} added to [{}] {}", Division.class.getSimpleName(), division.toSimpleLine(), Company.class.getSimpleName(), company.toSimpleLine());
        }

        EM.getTransaction().commit();

        L.info("[{}] {} updated", Division.class.getSimpleName(), division.toSimpleLine());
    }

    @Override
    public void remove(long id, Division entity) {
        L.info("Removing [{}] with id {}", Division.class.getSimpleName(), id);

        EM.getTransaction().begin();
        Division division = EM.find(Division.class, id);

        if (division.getCompany() != null) {
            Company company = EM.find(Company.class, division.getCompany().getId());
            company.removeDivision(division);
            L.info("[{}] {} removed from [{}] {}", Staffer.class.getSimpleName(), division.toSimpleLine(), Company.class.getSimpleName(), company.toSimpleLine());
        }

        if (division.getStaffers() != null && !division.getStaffers().isEmpty()) {
            List<Staffer> staffers = new ArrayList<>();
            staffers.addAll(division.getStaffers());
            for (Staffer staffer : staffers) {
                staffer = EM.find(Staffer.class, staffer.getId());
                division.removeStaffer(staffer);
                L.info("[{}] {} removed from [{}] {}", Staffer.class.getSimpleName(), staffer.toSimpleLine(), Division.class.getSimpleName(), division.toSimpleLine());
            }
        }

        EM.remove(division);
        EM.getTransaction().commit();
        L.info("[{}] {} removed", Division.class.getSimpleName(), division.toSimpleLine());
    }

}
