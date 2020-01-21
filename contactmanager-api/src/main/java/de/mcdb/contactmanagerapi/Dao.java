package de.mcdb.contactmanagerapi;

import java.util.List;
import javax.persistence.EntityManager;

/**
 * Data access interface to enable accessing the underlying database.
 * <p>
 * Contains method declarations to find all entities, find an entity by id,
 * persist a new entity, update an entity selected by id and to remove an entity
 * selected by id.
 *
 * @param <T> generic placeolder for the concrete entity class
 * @author Mirko
 */
public interface Dao<T> {

    /**
     * Looks for all entites of a class and collects them to a List which is
     * then returned.
     *
     * @return List&lt;T&gt; - List with the found entities
     */
    public List<T> findAll();

    /**
     * Looks for the entity with the submitted id and returns it.
     *
     * @param id id of the wanted entity
     * @return T - the found entity
     */
    public T findById(long id);

    /**
     * Persists the submitted entity to the database.
     *
     * @param entity the entity to persist
     */
    public void persist(T entity);

    /**
     * Updates the entity with the submitted id with the values from the
     * submitted entity.
     *
     * @param id id of the wanted entity
     * @param entity object with new values for the entity
     */
    public void update(long id, T entity);

    /**
     * Removes the entity with the submitted id from the database.
     *
     * @param id id of the wanted entity
     */
    public void remove(long id);

    /**
     * Called to safely shut down the Dao by closing its {@link EntityManager}.
     */
    public void destroy();

}
