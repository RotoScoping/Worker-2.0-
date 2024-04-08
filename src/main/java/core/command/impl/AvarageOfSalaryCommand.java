package core.command.impl;

import core.command.ICommand;
import core.service.WorkerService;

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
    public String execute(String... args) {
        return String.format("Средняя зарплата по больнице -  %.2f", service.averageOfSalary());
    }

    @Override
    public String toString() {
        return "average_of_salary --> вывести среднее значение поля salary для всех элементов коллекции";
    }
}
