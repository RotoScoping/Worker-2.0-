package core.command.impl;

import core.command.ICommand;
import core.logger.AsyncLogger;

import java.util.logging.Level;
/**
 * Класс, который представляет команду выхода из программы
 */
public class ExitCommand implements ICommand {
    private static final AsyncLogger logger = AsyncLogger.get();

    /**
     * Close ApplicationContext. Stopping service.
     */


    @Override
    public String execute(String... args) {
        logger.log(Level.INFO, "Исполнение команды: exit");
        return "Выключаемся!";
    }
}
