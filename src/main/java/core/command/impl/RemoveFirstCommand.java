package core.command.impl;

import core.command.ICommand;
import core.service.WorkerService;
/**
 * Класс , представляющий команду для удаления первого пользователя из коллекции
 */
public class RemoveFirstCommand implements ICommand {
    private final WorkerService service = WorkerService.getInstance();


    /**
     * Remove first string.
     * @return the string
     */
    @Override
    public String execute(String... args) {
        return service.removeFirst();
    }
}
