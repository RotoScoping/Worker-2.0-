package core.command.impl;

import core.command.ConsoleHelper;
import core.command.ICommand;
import core.model.Worker;
import core.service.WorkerService;
/**
 * Класс, реализующий команду добавления пользователя в коллекцию
 */
public class AddCommand implements ICommand {

    private final WorkerService service = WorkerService.getInstance();

    /**
     * Method that adds the worker to the storage.
     * @param args
     * @return string
     */

    @Override
    public String execute(String... args) {
        Worker worker = ConsoleHelper.getWorker();
        return service.add(worker);
    }
}
