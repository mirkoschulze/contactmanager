package de.mcdb.contactmanagerdesktop;

import ch.qos.logback.classic.Logger;
import com.querydsl.jpa.impl.JPAQuery;
import de.mcdb.contactmanagerapi.datamodel.Division;
import static de.mcdb.contactmanagerapi.datamodel.QStaffer.staffer;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
import java.util.List;
import javax.persistence.EntityManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mirko
 */
public class StafferDao implements de.mcdb.contactmanagerapi.Dao<Staffer> {

    private static final Logger L = (Logger) LoggerFactory.getLogger(Dao.class);

    private final EntityManager EM = HibernateUtils.getEntityManager();

    @Override
    public List<Staffer> findAll() {
        L.info("Quering for all [{}] entities", Staffer.class.getSimpleName());
        return new JPAQuery<>(EM).select(staffer).from(staffer).fetch();
    }

    @Override
    public Staffer findById(long id) {
        L.info("Quering for [{}] with ID {}", Staffer.class.getSimpleName(), id);
        return EM.find(Staffer.class, id);
    }

    @Override
    public void persist(Staffer entity) {
        L.info("Persisting [{}] {}", Staffer.class.getSimpleName(), entity.toEnhancedLine());
        EM.getTransaction().begin();
        if (entity.getDivision() != null) {
            Division division = EM.find(Division.class, entity.getDivision().getId());
            division.addStaffer(entity);
            L.info("[{}] {} added to [{}] {}", Staffer.class.getSimpleName(), entity.toSimpleLine(), Division.class.getSimpleName(), division.toSimpleLine());
        }
        EM.persist(entity);
        L.info("[{}] {} persisted", Staffer.class.getSimpleName(), entity.toSimpleLine());
        EM.getTransaction().commit();
    }

    @Override
    public void update(long id, Staffer entity) {
        L.info("Updating [{}] with id {}", Staffer.class.getSimpleName(), id);
        EM.getTransaction().begin();

        Staffer staffer = EM.find(Staffer.class, id);

        if (entity.getDivision() != null) {
            Division division = EM.find(Division.class, entity.getDivision().getId());

            staffer.setForeName(entity.getForeName());
            staffer.setSurName(entity.getSurName());
            division.addStaffer(staffer);
            L.info("[{}] {} added to [{}] {}", Staffer.class.getSimpleName(), staffer.toSimpleLine(), Division.class.getSimpleName(), division.toSimpleLine());
        }
        EM.getTransaction().commit();

        L.info("[{}] {} updated", Staffer.class.getSimpleName(), staffer.toSimpleLine());
    }

    @Override
    public void remove(long id, Staffer entity) {
        L.info("Removing [{}] with id {}", Staffer.class.getSimpleName(), id);

        EM.getTransaction().begin();
        Staffer staffer = EM.find(Staffer.class, id);

        if (staffer.getDivision() != null) {
            Division division = EM.find(Division.class, staffer.getDivision().getId());
            division.removeStaffer(staffer);
            L.info("[{}] {} removed from [{}] {}", Staffer.class.getSimpleName(), staffer.toSimpleLine(), Division.class.getSimpleName(), staffer.getDivision().toSimpleLine());
        }

        EM.remove(staffer);

        EM.getTransaction().commit();
        L.info("[{}] {} removed", Staffer.class.getSimpleName(), staffer.toSimpleLine());
    }

}
