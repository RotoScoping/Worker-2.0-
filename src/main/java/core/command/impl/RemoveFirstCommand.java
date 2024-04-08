package core.command.impl;

import core.command.ICommand;
import core.service.WorkerService;

/**
 * Класс , представляющий команду для удаления первого пользователя из коллекции
 */
public class RemoveFirstCommand implements ICommand {
    private final WorkerService service = WorkerService.getInstance();


    /**
     * Method that removes first element from collection.
     * @return A string describing the success or failure of the operation
     */
    @Override
    public String execute(String... args) {
        return service.removeFirst();
    }

    @Override
    public String toString() {
        return "remove_first --> удалить первый элемент из коллекции";
    }
}
