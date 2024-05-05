package org.example.command.impl;

import org.example.command.ICommand;
import org.example.model.Message;
import org.example.service.WorkerService;

import java.nio.ByteBuffer;

/**
 * Интерфейс команды, содержащий метод выполнения команды -execute()
 */
public class PrintFieldDescendingOrganization implements ICommand {
    private final WorkerService service = WorkerService.getInstance();

    /**
     * Print field descending organization string.
     * @return string with organizations DESC sort
     */

    @Override
    public Message execute(ByteBuffer payload) {
        return new Message(service.printFieldDescendingOrganization());
    }

    @Override
    public String toString() {
        return "print_field_descending_organization --> вывести значения поля organization всех элементов в порядке убывания";
    }
}
