package org.example.service;


import org.example.model.Organization;
import org.example.model.User;
import org.example.model.Worker;
import org.example.storage.AuditableCrud;
import org.example.storage.iml.WorkerDao;
import org.example.storage.loader.impl.FileLoader;
import org.example.validation.annotations.basic.validator.Validator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Worker service.
 */
public class WorkerService {

    private final Validator validator;

    private final AuditableCrud<Integer, Worker> dao;

    private static final WorkerService INSTANCE = new WorkerService(WorkerDao.getInstance(), new Validator("core.model"));

    /**
     * Instantiates a new Worker service.
     *
     * @param dao       the dao
     * @param validator the validator
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
     * Add worker in collection .
     *
     * @param worker the worker
     * @return A string describing the success or failure of the operation
     */
    public String add(Worker worker, User user) {
        List<String> msg = validator.validate(worker);
        if (msg.size() == 0) {
            worker.setUser(user);
            dao.add(worker);
            System.out.println("52");
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
     * Method that update worker in collection  .
     *
     * @param worker the worker
     * @return A string describing the success or failure of the operation
     */
    public String update(Worker worker, User user) {
        worker.setUser(user);
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
    public String removeFirst(User user) {
        var storage = dao.readAll();
        boolean isDeleted = false;
        if (storage.size() == 0) {
            return "Коллекция пуста! Удаления не произошло!";
        }
        System.out.println(storage.size());
        for (Worker worker : storage) {
            System.out.println(worker.getUserId());
            if (worker.getUserId() == user.getId()) {
                System.out.println("ВЫЗОВ");
                isDeleted = dao.removeById(worker.getId());
                break;
            }
        }

        return isDeleted ? "Элемент был удален" : "Не нашлось такого элемента";
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
    public String removeById(Integer id, User user) {
        var workers = dao.readAll();
        for (Worker worker: workers) {
            if (worker.getId() == id) {
                if (worker.getUserId() == user.getId()) {
                    if (dao.removeById(id)) {
                        return "Пользователь был удален!";
                    } else {
                        return "Произошла ошибка";
                    }
                } else {
                    return "Вы не можете удалить этот объект!";
                }

            }
        }
        return String.format("Пользователя с id=%d не существует.", id);

    }

    /**
     * Add if max salary string.
     *
     * @param worker the w
     * @return A string describing the success or failure of the operation
     */
    public String addIfMaxSalary(Worker worker, User user) {
        worker.setUser(user);
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
    public String addIfMinSalary(Worker worker, User user) {
        worker.setUser(user);
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
        var workers = dao.readAll();
        System.out.println(workers);
        String s = workers.size() == 0 ? "В коллекции пусто!" :
                Arrays.stream(workers.toArray(new Worker[0]))
                        .sorted(Worker::compareTo)
                        .map(Worker::toString)
                        .collect(Collectors.joining("\n"));
        System.out.println(s);

        return s;
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

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static WorkerService getInstance() {
        return INSTANCE;
    }
}