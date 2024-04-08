package core.command.impl;

import core.command.ConsoleHelper;
import core.command.ICommand;
import core.model.Worker;
import core.service.WorkerService;

/**
 * Класс, реализующий команду добавления пользователя в коллекцию если какой нибудь парам. больше чем у всех остальных.
 */
public class AddIfMaxCommand implements ICommand {
    private final WorkerService service = WorkerService.getInstance();


    /**
     * Add if max string.
     *
     * @param args the args
     * @return A string describing the success or failure of the operation
     */

    @Override
    public String execute(String... args) {
        Worker worker = ConsoleHelper.getWorker();
        return service.addIfMaxSalary(worker);
    }

    @Override
    public String toString() {
        return "add_if_max {worker} --> добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции";
    }
}
