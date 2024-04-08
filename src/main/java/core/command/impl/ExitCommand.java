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
     * @return A string describing the success or failure of the operation
     */
    @Override
    public String execute(String... args) {
        logger.log(Level.INFO, "Исполнение команды: exit");
        return "Выключаемся!";
    }

    @Override
    public String toString() {
        return "exit --> завершить программу (без сохранения в файл)";
    }
}
