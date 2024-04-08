package core.command.impl;

import core.command.ICommand;
import core.service.WorkerService;

/**
 * Класс, представляющий команду, которая очищает коллекцию.
 */
public class ClearCommand  implements ICommand {
    private final WorkerService service = WorkerService.getInstance();

    /**
     * Method that clears the collection of workers.
     * @return A String value with information about the number of deleted workers
     */

    @Override
    public String execute(String... args) {
        return String.format("Было удалено %d элементов", service.clear());
    }

    @Override
    public String toString() {
        return "clear --> очистить коллекцию";
    }
}
