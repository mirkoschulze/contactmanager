package de.mcdb.contactmanagerapi;

import java.util.List;

/**
 * Data access interface to enable accessing the underlying database.
 *
 * @param <T> generic placeolder for the concrete entity class
 * @author Mirko
 */
public interface Dao<T> {

    public List<T> findAll();

    public T findById(long id);

    public void persist(T entity);

    public void update(long id, T entity);

    public void remove(long id);

    public void destroy();

}
