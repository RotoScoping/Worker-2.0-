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
     * @return A string describing the success or failure of the operation
     */

    @Override
    public String execute(String... args) {
        Worker worker = ConsoleHelper.getWorker();
        return service.add(worker);
    }

    @Override
    public String toString() {
        return "add {worker} --> добавить новый элемент в коллекцию (worker add -help for more details)";
    }
}
