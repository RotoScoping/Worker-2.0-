package core.command.impl;

import core.command.ICommand;
import core.service.WorkerService;

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

    public String execute(String... args) {
        return service.printFieldAscendingStatus();
    }

    @Override
    public String toString() {
        return "print_field_ascending_status --> вывести значения поля status всех элементов в порядке возрастания";
    }
}
