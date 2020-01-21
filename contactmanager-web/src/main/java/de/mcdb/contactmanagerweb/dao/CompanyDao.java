package de.mcdb.contactmanagerweb.dao;

import com.querydsl.jpa.impl.JPAQuery;
import de.mcdb.contactmanagerapi.datamodel.Company;
import de.mcdb.contactmanagerapi.datamodel.Division;
import static de.mcdb.contactmanagerapi.datamodel.QCompany.company;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

/**
 *
 * @author Mirko
 */
@Stateless
public class CompanyDao implements de.mcdb.contactmanagerapi.Dao<Company> {

    private final EntityManager EM = HibernateUtils.getEntityManager();

    @Override
    public List<Company> findAll() {
        return new JPAQuery<>(EM).select(company).from(company).fetch();
    }

    @Override
    public Company findById(long id) {
        return EM.find(Company.class, id);
    }

    @Override
    public void persist(Company entity) {
        EM.getTransaction().begin();
        EM.persist(entity);
        EM.getTransaction().commit();
    }

    @Override
    public void update(long id, Company entity) {
        EM.getTransaction().begin();

        Company company = EM.find(Company.class, id);

        company.setName(entity.getName());

        EM.getTransaction().commit();
    }

    @Override
    public void remove(long id) {
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

    /**
     * Closes the {@link EntityManager} for this instance.
     */
    @Override
    public void destroy() {
        if (this.EM != null && this.EM.isOpen()) {
            if (this.EM.getTransaction().isActive()) {
                this.EM.clear();
            }
            this.EM.close();
        }
    }

}
