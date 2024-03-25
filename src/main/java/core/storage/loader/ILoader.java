package core.storage.loader;

import core.model.Worker;

import java.util.Collection;

/**
 * The interface Loader.
 *
 * @param <T> type of the save/load object
 */
public interface ILoader<T> {

    /**
     * Save boolean.
     *
     * @param col the col
     * @return the boolean
     */
    boolean save(Collection<Worker> col);

    /**
     * Load t.
     *
     * @return the t
     */
    T load() ;
}
