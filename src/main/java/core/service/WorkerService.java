package core.service;

import core.model.Organization;
import core.model.Worker;
import core.storage.AuditableCrud;
import core.storage.iml.WorkerDao;
import core.storage.loader.iml.FileLoader;
import core.validation.annotations.basic.validator.Validator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Worker service.
 */

public class WorkerService {

    private final Validator validator;

    private final AuditableCrud<Integer, Worker> dao;

    private static final WorkerService INSTANCE = new WorkerService(new WorkerDao(new FileLoader()), new Validator("core.model"));

    /**
     * Instantiates a new Worker service.
     *
     * @param dao the dao
     */
    public WorkerService(AuditableCrud<Integer, Worker> dao, Validator validator) {
        this.validator = validator;
        this.dao = dao;
    }

    /**
     * Method that get meta-information from repository.
     *
     * @return the string
     */
    public String info() {
        var storage = (PriorityQueue<Worker>) dao.readAll();
        return String.format("Используемый тип для хранения Worker - %s" +
                             "\n Была создана- %tF %tT" +
                             "\n Длина - %d", dao.getCollectionType(), dao.getCreatedDate(), dao.getCreatedDate(), storage.size());
    }

    /**
     * Save workers to file.
     *
     * @return A string describing the success or failure of the operation
     */
    public String save() {
        return dao.dumpToTheFile() ? "Файл успешно сохранен в корне проекта!" : "Произошла ошибка при сохранении";
    }

    /**
     * Add string.
     *
     * @param worker the worker
     * @return A string describing the success or failure of the operation
     */
    public String add(Worker worker) {
        List<String> msg = validator.validate(worker);
        if (msg.size() == 0) {
            dao.add(worker);
            return "Данные записаны!";
        }
        return String.join("\n", msg);
    }

    /**
     * Method that calculates avarage of salary.
     *
     * @return avarage of salary
     */
    public double averageOfSalary() {
        OptionalDouble average = dao.readAll()
                .stream()
                .map(Worker::getSalary)
                .mapToDouble(Float::doubleValue)
                .average();
        return average.isPresent() ? average.getAsDouble() : 0;
    }

    /**
     * Method thar .
     *
     * @param worker the worker
     * @return A string describing the success or failure of the operation
     */
    public String update(Worker worker) {
        List<String> messages = validator.validate(worker);
        return messages.size() == 0 ?
                dao.update(worker) ? "Данные успешно обновлены!" : "id такого worker не существует!"
                : String.join("\n", messages);

    }

    /**
     * A method that includes the logic of deleting the first worker
     *
     * @return A string describing the success or failure of the operation
     */
    public String removeFirst() {
        var storage = (PriorityQueue<Worker>) dao.readAll();
        if (storage.size() == 0) {
            return "Коллекция пуста! Удаления не произошло!";
        }
        storage.poll();
        return "Элемент был удален";
    }

    /**
     * Method that deletes workers from collection
     *
     * @return count of deleted workers
     */
    public int clear() {
        var storage = dao.readAll();
        int size = storage.size();
        storage.clear();
        return size;
    }

    /**
     * Remove by id string.
     *
     * @param id the id
     * @return A string describing the success or failure of the operation
     */
    public String removeById(Integer id) {
        return dao.removeById(id)
                ? "Пользователь был удален!"
                : String.format("Пользователя с id=%d не существует.", id);

    }

    /**
     * Add if max salary string.
     *
     * @param worker the w
     * @return A string describing the success or failure of the operation
     */
    public String addIfMaxSalary(Worker worker) {
        List<String> messages = validator.validate(worker);
        if (messages.size() != 0)
            return "Полученный объект невалидный!";
        var storage = (PriorityQueue<Worker>) dao.readAll();
        Optional<Float> maxSalary = storage.stream()
                .map(Worker::getSalary)
                .max(Float::compareTo);
        if (maxSalary.isPresent()) {
            if (maxSalary.get() < worker.getSalary()) {
                dao.add(worker);
                return "Добавлен в коллекцию как элемент с максимальной зарплатой";
            }
            return "Воркер отброшен, его зарплата не максимальна";
        }
        dao.add(worker);
        return "Первый элемент в коллекции, его зарпалата максимальна";

    }

    /**
     * Add if min salary string.
     *
     * @param worker the worker
     * @return A string describing the success or failure of the operation
     */
    public String addIfMinSalary(Worker worker) {
        List<String> messages = validator.validate(worker);
        if (messages.size() != 0)
            return "Полученный объект невалидный!";
        var storage = (PriorityQueue<Worker>) dao.readAll();
        Optional<Float> minSalary = storage.stream()
                .map(Worker::getSalary)
                .min(Comparator.naturalOrder());
        if (minSalary.isPresent()) {
            if (minSalary.get() > worker.getSalary()) {
                dao.add(worker);
                return "Добавлен в коллекцию как элемент с минимальной зарплатой";
            }
            return "Воркер отброшен, его зарплата не минимальна";
        }
        dao.add(worker);
        return "Первый элемент в коллекции, его зарпалата минимальна";

    }

    /**
     * show all workers to the client.
     *
     * @return string with information about all workers
     */
    public String show() {
        return dao.readAll()
                       .size() == 0 ? "В коллекции пусто!" :
                Arrays.stream(dao.readAll()
                                .toArray(new Worker[0]))
                        .sorted(Worker::compareTo)
                        .map(Worker::toString)
                        .collect(Collectors.joining("\n"));
    }

    /**
     * Print field descending organization string.
     *
     * @return the string
     */
    public String printFieldDescendingOrganization() {
        return dao.readAll()
                .stream()
                .map(Worker::getOrganization)
                .sorted(Organization::compareTo)
                .map(Organization::toString)
                .collect(Collectors.joining("\n"));


    }

    /**
     * Sort ascending enum status field.
     *
     * @return string with sorted statuses
     */
    public String printFieldAscendingStatus() {
        return dao.readAll()
                .stream()
                .map(worker -> worker.getStatus()
                        .toString())
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining("\n"));
    }

    public static WorkerService getInstance() {
        return INSTANCE;
    }
}