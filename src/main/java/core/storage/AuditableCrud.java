package core.storage;

/**
 * The interface AuditableCrud.
 *
 * @param <ID>     entity id type
 * @param <Entity> > entity class type
 */
public interface AuditableCrud<ID, Entity> extends Auditable, Crud<ID, Entity> {
}
