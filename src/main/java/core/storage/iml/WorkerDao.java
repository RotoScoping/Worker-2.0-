package core.storage.iml;

import core.logger.AsyncLogger;
import core.model.Worker;
import core.storage.AuditableCrud;
import core.storage.loader.ILoader;
import core.storage.loader.impl.FileLoader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.logging.Level;


/**
 * Class managing a collection of workers (dao)
 */
public class WorkerDao implements AuditableCrud<Integer, Worker> {

    private static final AsyncLogger logger = AsyncLogger.get();
    private static final WorkerDao INSTANCE = new WorkerDao(new FileLoader());
    private final Collection<Worker> storage;

    private final LocalDateTime createdAt;

    private final ILoader<? extends Collection<Worker>> loader;

    /**
     * Instantiates a new Worker repository.
     *
     * @param loader the loader
     */
    public WorkerDao(ILoader<? extends Collection<Worker>> loader) {
        this.loader = loader;
        storage = new PriorityQueue<>((w1,w2) -> w2.getId() - w1.getId());
        createdAt = LocalDateTime.now();
        Init(loader);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static WorkerDao getInstance() {
        return INSTANCE;
    }

    /**
     * A method that initializes a collection of workers using the ILoader<PriorityQueue<Worker>> interface implementation.
     * @param loader the loader
     */
    private void Init(ILoader<? extends Collection<Worker>> loader)  {
        Collection<Worker> data = loader.load();
        for (Worker worker : data) {
            add(worker);
        }
    }



    /**
     * Method that add worker to the storage
     * @param worker the worker
     */
    public void add(Worker worker) {
        int id = storage.size()+1;
        logger.log(Level.INFO, String.format("Добавление сотрудника c id = %d", id));
        worker.setId(id);
        worker.setCreationDate(LocalDate.now());
        storage.add(worker);
    }

    /**
     * Method that returns all workers
     * @return collection of workers
     */
    @Override
    public Collection<Worker> readAll() {
        return storage;
    }


    /**
     * Method that updates worker in collection.
     * <br> That
     *
     * @param worker the worker
     * @return boolean result of operation
     */
    public boolean update(Worker worker) {

        for (Worker el : storage) {
            if (el.getId() == worker.getId()) {
                removeById(worker.getId());
                storage.add(worker);
                return true;
            }
        }
        return false;
    }




    /**
     * Method that removes worker from collection by id.
     * @param id the id
     * @return boolean result of operation
     */
    public boolean removeById(Integer id) {
        return id >= 1 && id <= storage.size() && storage.removeIf(worker -> worker.getId() == id);
    }





    /**
     * Method that dumps workers to file using implementation the ILoader<PriorityQueue<Worker>> interface implementation
     */
    public boolean dumpToTheFile() {
        return loader.save(storage);
    }

    @Override
    public LocalDateTime getCreatedDate() {
        return createdAt;
    }

    @Override
    public String getCollectionType() {
        return storage.getClass()
                .getSimpleName();
    }
}
