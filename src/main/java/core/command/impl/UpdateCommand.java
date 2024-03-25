package core.command.impl;

import core.command.ConsoleHelper;
import core.command.ICommand;
import core.model.Worker;
import core.service.WorkerService;

/**
 * Класс , представляющий команду для обноваления пользователя в коллекции
 */
public class UpdateCommand implements ICommand {

    private final WorkerService service = WorkerService.getInstance();

    /**
     * Method that updates worker in service.
     *
     * @param args String...
     * @return A String value with information about the storage
     */

    @Override
    public String execute(String... args) {
        if (args.length < 1)
            return "Пожалуйста укажите id обновляемого воркера!";
        if (!ConsoleHelper.isNumeric(args[1]))
            return "Пожалуйста укажите целое число в аргументе!";
        Worker worker = ConsoleHelper.getWorker();
        worker.setId(Integer.parseInt(args[1]));
        return service.update(worker);
    }
}
