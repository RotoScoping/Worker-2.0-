package org.example.command.impl;

import org.example.command.ICommand;
import org.example.logger.AsyncLogger;
import org.example.model.Message;
import org.example.service.WorkerService;

import java.nio.ByteBuffer;
import java.util.logging.Level;

/**
 * Класс, представляющий команду info
 */
public class InfoCommand implements ICommand{
    private static final AsyncLogger logger = AsyncLogger.get("server");
    private final WorkerService service = WorkerService.getInstance();

    /**
     * Method that sends a response to the client in the form of a string containing information:
     * <br> - The type of storage used
     * <br> - Creation time
     * <br> - The number of objects in the storage
     * @return A String value with information about the storage
     */
    @Override
    public Message execute(ByteBuffer payload) {
        logger.log(Level.INFO, "Исполнение команды: info");
        return new Message(service.info());
    }

    @Override
    public String toString() {
        return "info --> вывести в стандартный поток вывода информацию о коллекции";
    }

}
