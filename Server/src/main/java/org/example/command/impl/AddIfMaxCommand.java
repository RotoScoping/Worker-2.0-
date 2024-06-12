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
 * Класс, реализующий команду добавления пользователя в коллекцию если какой нибудь парам. больше чем у всех остальных.
 */
public class AddIfMaxCommand implements ICommand {
    private final WorkerService service = WorkerService.getInstance();

    private final AuthContext auth = AuthContext.get();
    /**
     * Add if max string.
     *
     * @param args the args
     * @return A string describing the success or failure of the operation
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
            System.out.println("Получен объект: " + worker);
            return new Message(service.addIfMaxSalary(worker,user));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "add_if_max {worker} --> добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции";
    }
}
