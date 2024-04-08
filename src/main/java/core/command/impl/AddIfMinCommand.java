package core.command.impl;

import core.command.ConsoleHelper;
import core.command.ICommand;
import core.model.Worker;
import core.service.WorkerService;

/**
 * Класс, реализующий команду добавления пользователя в коллекцию если какой нибудь парам. меньше чем у всех остальных.
 */
public class AddIfMinCommand implements ICommand {
    private final WorkerService service = WorkerService.getInstance();
    /**
     * Add if min string.
     *
     * @param args the worker
     * @return A string describing the success or failure of the operation
     */


    @Override
    public String execute(String... args) {
        Worker worker = ConsoleHelper.getWorker();
        return service.addIfMinSalary(worker);
    }

    @Override
    public String toString() {
        return "add_if_min {worker} --> добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции";
    }
}
