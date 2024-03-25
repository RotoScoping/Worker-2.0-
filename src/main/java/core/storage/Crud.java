package core.storage;

import java.util.Collection;

/**
 * Basic Crud interface
 *
 * @param <ID>     the type parameter
 * @param <Entity> the type parameter
 */
public interface Crud<ID, Entity> {

    /**
     * Add.
     *
     * @param e the e
     */
    void add(Entity e);

    /**
     * Remove boolean.
     *
     * @param id the id
     * @return the boolean
     */
    boolean removeById(ID id);

    /**
     * Update boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    boolean update(Entity entity);


    /**
     * Dump to the file boolean.
     *
     * @return the boolean
     */
    boolean dumpToTheFile();

    /**
     * Read all collection.
     *
     * @return the collection
     */
    Collection<Entity> readAll();




}
