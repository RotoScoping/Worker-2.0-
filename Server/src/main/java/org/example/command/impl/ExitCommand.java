package org.example.command.impl;

import org.example.command.ICommand;
import org.example.logger.AsyncLogger;
import org.example.model.Message;

import java.nio.ByteBuffer;
import java.util.logging.Level;

/**
 * Класс, который представляет команду выхода из программы
 */
public class ExitCommand implements ICommand {
    private static final AsyncLogger logger = AsyncLogger.get("server");

    /**
     * Close ApplicationContext. Stopping service.
     * @return A string describing the success or failure of the operation
     */
    @Override
    public Message execute(ByteBuffer payload) {
        logger.log(Level.INFO, "Исполнение команды: exit");
        return new Message("Выключаемся!");
    }

    @Override
    public String toString() {
        return "exit --> завершить программу (без сохранения в файл)";
    }
}
