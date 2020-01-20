package de.mcdb.contactmanagerdesktop;

import ch.qos.logback.classic.Logger;
import com.querydsl.jpa.impl.JPAQuery;
import de.mcdb.contactmanagerapi.datamodel.Company;
import de.mcdb.contactmanagerapi.datamodel.Division;
import static de.mcdb.contactmanagerapi.datamodel.QCompany.company;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mirko
 */
public class CompanyDao implements de.mcdb.contactmanagerapi.Dao<Company>{
    
    private static final Logger L = (Logger) LoggerFactory.getLogger(Dao.class);

    private final EntityManager EM = HibernateUtils.getEntityManager();

    @Override
    public List<Company> findAll() {
        L.info("Quering for all [{}] entities", Company.class.getSimpleName());
        return new JPAQuery<>(EM).select(company).from(company).fetch();
    }

    @Override
    public Company findById(long id) {
         L.info("Quering for [{}] with ID {}", Company.class.getSimpleName(), id);
        return EM.find(Company.class, id);
    }

    @Override
    public void persist(Company entity) {
        L.info("Persisting [{}] {}", Company.class.getSimpleName(), entity.toSimpleLine());
        EM.getTransaction().begin();
        EM.persist(entity);
        L.info("[{}] {} persisted", Company.class.getSimpleName(), entity.toSimpleLine());
        EM.getTransaction().commit();
    }

    @Override
    public void update(long id, Company entity) {
       L.info("Updating [{}] with id {}", Division.class.getSimpleName(), id);
        EM.getTransaction().begin();

        Company company = EM.find(Company.class, id);

        company.setName(entity.getName());

        EM.getTransaction().commit();

        L.info("[{}] {} updated", Company.class.getSimpleName(), company.toSimpleLine());
    }

    @Override
    public void remove(long id, Company entity) {
        L.info("Removing [{}] with id {}", Company.class.getSimpleName(), id);
        EM.getTransaction().begin();

        Company company = EM.find(Company.class, id);

        if (company.getDivisions() != null && !company.getDivisions().isEmpty()) {
            List<Division> divisions = new ArrayList<>();
            divisions.addAll(company.getDivisions());
            for (Division division : divisions) {
                division = EM.find(Division.class, division.getId());
                company.removeDivision(division);
                L.info("[{}] {} removed from [{}] {}", Staffer.class.getSimpleName(), division.toSimpleLine(), Company.class.getSimpleName(), company.toSimpleLine());
            }
        }

        EM.remove(company);
        EM.getTransaction().commit();
        L.info("[{}] {} removed", Company.class.getSimpleName(), company.toSimpleLine());
    }
    
}
