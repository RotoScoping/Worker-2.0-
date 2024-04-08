package core.command.impl;

import core.command.ICommand;
import core.logger.AsyncLogger;
import core.service.WorkerService;

import java.util.logging.Level;

/**
 * Класс, представляющий команду info
 */
public class InfoCommand implements ICommand{
    private static final AsyncLogger logger = AsyncLogger.get();
    private final WorkerService service = WorkerService.getInstance();

    /**
     * Method that sends a response to the client in the form of a string containing information:
     * <br> - The type of storage used
     * <br> - Creation time
     * <br> - The number of objects in the storage
     * @return A String value with information about the storage
     */
    @Override
    public String execute(String... args) {
        logger.log(Level.INFO, "Исполнение команды: info");
        return service.info();
    }

    @Override
    public String toString() {
        return "info --> вывести в стандартный поток вывода информацию о коллекции";
    }

}
