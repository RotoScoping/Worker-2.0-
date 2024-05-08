package org.example.command.impl;

import org.example.command.ICommand;
import org.example.model.Message;
import org.example.service.WorkerService;

import java.nio.ByteBuffer;

/**
 * Класс , представляющий команду для удаления пользователя по id
 */
public class RemoveByIdCommand implements ICommand {
    private final WorkerService service = WorkerService.getInstance();

    /**
     * Method thar removes worker by id in service
     *
     * @param args the id
     * @return the string
     */

    @Override
    public Message execute(ByteBuffer payload) {

        payload.position(37);
        byte b1 = payload.get();
        byte b2 = payload.get();
        byte b3 = payload.get();
        byte b4 = payload.get();
        int index = ((b1 & 0xFF) << 24) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 8) | (b4 & 0xFF);
        return new Message(service.removeById(index));

    }

    @Override
    public String toString() {
        return "remove_by_id {id} --> удалить элемент из коллекции по его id" ;
    }
}
