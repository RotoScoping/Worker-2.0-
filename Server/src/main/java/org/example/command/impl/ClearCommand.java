package org.example.command.impl;

import org.example.command.ICommand;
import org.example.model.Message;
import org.example.service.WorkerService;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

/**
 * Класс, представляющий команду, которая очищает коллекцию.
 */
public class ClearCommand  implements ICommand {
    private final WorkerService service = WorkerService.getInstance();

    /**
     * Method that clears the collection of workers.
     * @return A String value with information about the number of deleted workers
     */

    @Override
    public Message execute(ByteBuffer payload) {
        return new Message(String.format("Было удалено %d элементов", service.clear()));
    }

    @Override
    public String toString() {
        return "clear --> очистить коллекцию";
    }
}
