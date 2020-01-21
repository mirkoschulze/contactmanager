package de.mcdb.contactmanagerweb.dao;

import com.querydsl.jpa.impl.JPAQuery;
import de.mcdb.contactmanagerapi.datamodel.Company;
import de.mcdb.contactmanagerapi.datamodel.Division;
import static de.mcdb.contactmanagerapi.datamodel.QDivision.division;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

/**
 *
 * @author Mirko
 */
@Stateless
public class DivisionDao implements de.mcdb.contactmanagerapi.Dao<Division> {

    private final EntityManager EM = HibernateUtils.getEntityManager();

    @Override
    public List<Division> findAll() {
        return new JPAQuery<>(EM).select(division).from(division).fetch();
    }

    @Override
    public Division findById(long id) {
        return EM.find(Division.class, id);
    }

    @Override
    public void persist(Division entity) {
        EM.getTransaction().begin();
        if (entity.getCompany() != null) {
            Company company = EM.find(Company.class, entity.getCompany().getId());
            company.addDivision(entity);
        }
        EM.persist(entity);
        EM.getTransaction().commit();
    }

    @Override
    public void update(long id, Division entity) {
        EM.getTransaction().begin();

        Division division = EM.find(Division.class, id);

        if (entity.getCompany() != null) {
            Company company = EM.find(Company.class, entity.getCompany().getId());

            division.setName(entity.getName());
            division.setStaffers(entity.getStaffers());
            company.addDivision(division);
        }

        EM.getTransaction().commit();
    }

    @Override
    public void remove(long id) {
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
