package org.example.command.impl;

import org.example.command.ICommand;
import org.example.model.Message;
import org.example.model.Worker;
import org.example.service.WorkerService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

/**
 * Класс, реализующий команду добавления пользователя в коллекцию
 */
public class AddCommand implements ICommand {

    private final WorkerService service = WorkerService.getInstance();

    /**
     * Method that adds the worker to the storage.
     *
     * @param payload
     * @return A Message describing the success or failure of the operation
     */

    @Override
    public Message execute(ByteBuffer payload) {
        try (var bis = new ByteArrayInputStream(payload.array(), 1, payload.array().length - 1);
             var ois = new ObjectInputStream(bis)) {
            Worker worker = (Worker) ois.readObject();
            System.out.println("Получен объект: " + worker);
            return new Message(service.add(worker));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "add {worker} --> добавить новый элемент в коллекцию (worker add -help for more details)";
    }
}
