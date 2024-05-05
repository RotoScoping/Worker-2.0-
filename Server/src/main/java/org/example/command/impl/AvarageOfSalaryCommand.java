package org.example.command.impl;

import org.example.command.ICommand;
import org.example.model.Message;
import org.example.service.WorkerService;

import java.nio.ByteBuffer;

/**
 * Класс, который представляет команду, котораяя считает среднюю зарплату.
 */
public class AvarageOfSalaryCommand implements ICommand {

    private final WorkerService service = WorkerService.getInstance();


    /**
     * Method that gets average of salary in worker.
     *
     * @return A string describing the success or failure of the operation
     */
    @Override
    public Message execute(ByteBuffer payload) {
        return new Message(String.format("Средняя зарплата по больнице -  %.2f", service.averageOfSalary()));
    }

    @Override
    public String toString() {
        return "average_of_salary --> вывести среднее значение поля salary для всех элементов коллекции";
    }
}
