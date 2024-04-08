package core.command.impl;

import core.command.ICommand;
import core.service.WorkerService;

/**
 * Класс , представляющий команду для сохранения пользователя в файл
 */
public class SaveCommand implements ICommand {
    private final WorkerService service = WorkerService.getInstance();

    /**
     * Method that save the workers to the file.
     * @return string information about saving
     */
    @Override
    public String execute(String... args) {
        return service.save();
    }

    @Override
    public String toString() {
        return "save --> сохранить коллекцию в файл";
    }
}
