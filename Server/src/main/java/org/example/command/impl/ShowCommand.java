package org.example.command.impl;

import org.example.command.ICommand;
import org.example.model.Message;
import org.example.service.WorkerService;

import java.nio.ByteBuffer;

/**
 * Класс , представляющий команду для вывода всех Worker в строком представлении
 */
public class ShowCommand implements ICommand {
    private final WorkerService service = WorkerService.getInstance();

    /**
     * Sends a response to the client in the form of a string containing information:
     * - The type of collection used
     * - Collection's Creation Date
     * - Collecntion's size
     * @return A string that contains information about all the workers in the service
     */
    @Override
    public Message execute(ByteBuffer payload) {
        return new Message(service.show());
    }

    @Override
    public String toString() {
        return "show --> вывести в стандартный поток вывода все элементы коллекции в строковом представлении";
    }
}
