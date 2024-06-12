package org.example.command.impl;

import org.example.auth.AuthContext;
import org.example.command.ICommand;
import org.example.model.Message;
import org.example.model.User;
import org.example.service.WorkerService;

import java.nio.ByteBuffer;

/**
 * Класс , представляющий команду для удаления первого пользователя из коллекции
 */
public class RemoveFirstCommand implements ICommand {
    private final WorkerService service = WorkerService.getInstance();
    private final AuthContext auth = AuthContext.get();


    /**
     * Method that removes first element from collection.
     * @return A string describing the success or failure of the operation
     */
    @Override
    public Message execute(ByteBuffer payload) {
        byte[] tokenBytes = new byte[36];
        payload.get(tokenBytes);
        String token = new String(tokenBytes);
        User user = auth.getUserBySessionToken(token);
        System.out.println(user);
        payload.rewind();
        return new Message(service.removeFirst(user));
    }

    @Override
    public String toString() {
        return "remove_first --> удалить первый элемент из коллекции";
    }
}
