package de.mcdb.contactmanagerweb.dao;

import com.querydsl.jpa.impl.JPAQuery;
import de.mcdb.contactmanagerapi.datamodel.Division;
import static de.mcdb.contactmanagerapi.datamodel.QStaffer.staffer;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

/**
 *
 * @author Mirko
 */
@Stateless
public class StafferDao implements de.mcdb.contactmanagerapi.Dao<Staffer> {

    private final EntityManager EM = HibernateUtils.getEntityManager();

    @Override
    public List<Staffer> findAll() {
        return new JPAQuery<>(EM).select(staffer).from(staffer).fetch();
    }

    @Override
    public Staffer findById(long id) {
        return EM.find(Staffer.class, id);
    }

    @Override
    public void persist(Staffer entity) {
        EM.getTransaction().begin();
        if (entity.getDivision() != null) {
            Division division = EM.find(Division.class, entity.getDivision().getId());
            division.addStaffer(entity);
        }
        EM.persist(entity);
        EM.getTransaction().commit();
    }

    @Override
    public void update(long id, Staffer entity) {
        EM.getTransaction().begin();

        Staffer staffer = EM.find(Staffer.class, id);

        if (entity.getDivision() != null) {
            Division division = EM.find(Division.class, entity.getDivision().getId());

            staffer.setForeName(entity.getForeName());
            staffer.setSurName(entity.getSurName());
            division.addStaffer(staffer);
        }
        EM.getTransaction().commit();
    }

    @Override
    public void remove(long id) {
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
