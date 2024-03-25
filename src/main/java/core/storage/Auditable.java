package core.storage;

import java.time.LocalDateTime;

/**
 * The interface Auditable.
 */
public interface Auditable {


    /**
     * Gets created date.
     *
     * @return the created date
     */
    LocalDateTime getCreatedDate();

    /**
     * Gets collection type.
     *
     * @return the collection type
     */
    String getCollectionType();

}
