package org.example.command.impl;

import org.example.command.ICommand;
import org.example.model.Message;
import org.example.service.WorkerService;

import java.nio.ByteBuffer;

/**
 * Класс, представляющий команду PrintFieldAscendingStatus, которая выводит статусы воркеров в порядке возрастания
 */
public class PrintFieldAscendingStatus implements ICommand {
    private final WorkerService service = WorkerService.getInstance();

    /**
     * Print field ascending status string.
     * @return string with status ASC sort
     */


    @Override

    public Message execute(ByteBuffer payload) {
        return new Message(service.printFieldAscendingStatus());
    }

    @Override
    public String toString() {
        return "print_field_ascending_status --> вывести значения поля status всех элементов в порядке возрастания";
    }
}
