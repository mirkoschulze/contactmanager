package de.mcdb.contactmanagerapi;

import java.util.List;

/**
 *
 * @author Mirko
 * @param <T>
 */
public interface Dao<T> {
    
    public List<T> findAll();

    public T findById(long id);

    public void persist(T entity);

    public void update(long id, T entity);

    public void remove(long id, T entity);

}
