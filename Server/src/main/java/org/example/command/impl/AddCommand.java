package org.example.command.impl;

import org.example.auth.AuthContext;
import org.example.command.ICommand;
import org.example.logger.AsyncLogger;
import org.example.model.Message;
import org.example.model.User;
import org.example.model.Worker;
import org.example.service.WorkerService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;

/**
 * Класс, реализующий команду добавления пользователя в коллекцию
 */
public class AddCommand implements ICommand {
    private static final AsyncLogger logger = AsyncLogger.get("server");
    private final WorkerService service = WorkerService.getInstance();
    private final AuthContext auth = AuthContext.get();

    /**
     * Method that adds the worker to the storage.
     *
     * @param payload
     * @return A Message describing the success or failure of the operation
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
            return new Message(service.add(worker,user));
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.WARNING, String.format("Произошла ошибка при десериализации: %s", e.getMessage()));
        }
        return new Message("Ошибка");
    }

    @Override
    public String toString() {
        return "add {worker} --> добавить новый элемент в коллекцию (worker add -help for more details)";
    }
}
