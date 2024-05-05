package org.example.command.impl;

import org.example.command.ICommand;
import org.example.model.Message;
import org.example.service.WorkerService;

import java.nio.ByteBuffer;

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
    public Message execute(ByteBuffer payload) {
        return new Message(service.save());
    }

    @Override
    public String toString() {
        return "save --> сохранить коллекцию в файл";
    }
}
