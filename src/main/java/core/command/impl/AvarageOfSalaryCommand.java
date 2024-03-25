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
     * @return
     */




    @Override
    public String execute(String... args) {
        return String.format("Средняя зарплата по больнице - %f", service.averageOfSalary());
    }
}
