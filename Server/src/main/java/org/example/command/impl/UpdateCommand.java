package org.example.command.impl;

import org.example.auth.AuthContext;
import org.example.command.ICommand;
import org.example.model.Message;
import org.example.model.User;
import org.example.model.Worker;
import org.example.service.WorkerService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

/**
 * Класс , представляющий команду для обноваления пользователя в коллекции
 */
public class UpdateCommand implements ICommand {

    private final WorkerService service = WorkerService.getInstance();
    private final AuthContext auth = AuthContext.get();

    /**
     * Method that updates worker in service.
     *
     * @param args String...
     * @return A String value with information about the storage
     */

    @Override
    public Message execute(ByteBuffer payload) {

        byte[] tokenBytes = new byte[36];
        payload.get(tokenBytes);
        String token = new String(tokenBytes);
        User user = auth.getUserBySessionToken(token);
        payload.rewind();
        try (var bis = new ByteArrayInputStream(payload.array(), 37, payload.array().length - 37);
             var ois = new ObjectInputStream(bis)) {
            Worker worker = (Worker) ois.readObject();
            worker.setUser(user);
            System.out.println("Получен объект: " + worker);
            return new Message(service.update(worker, user));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "update_id {worker} -->  обновить значение элемента коллекции, id которого равен заданному";
    }
}
