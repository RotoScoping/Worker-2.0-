package org.example.command.impl;

import org.example.command.ICommand;
import org.example.model.Message;
import org.example.service.WorkerService;

import java.nio.ByteBuffer;

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
    public Message execute(ByteBuffer payload) {
        return new Message(service.removeFirst());
    }

    @Override
    public String toString() {
        return "remove_first --> удалить первый элемент из коллекции";
    }
}
