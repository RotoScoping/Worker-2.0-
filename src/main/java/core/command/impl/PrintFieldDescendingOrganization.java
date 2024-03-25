package core.command.impl;

import core.command.ICommand;
import core.service.WorkerService;
/**
 * Интерфейс команды, содержащий метод выполнения команды -execute()
 */
public class PrintFieldDescendingOrganization implements ICommand {
    private final WorkerService service = WorkerService.getInstance();

    /**
     * Print field descending organization string.
     *
     * @return string with organizations DESC sort
     */


    @Override
    public String execute(String... args) {
        return service.printFieldDescendingOrganization();
    }
}
