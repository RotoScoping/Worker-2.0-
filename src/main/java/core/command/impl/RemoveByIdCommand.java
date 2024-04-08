package core.command.impl;

import core.command.ConsoleHelper;
import core.command.ICommand;
import core.service.WorkerService;

/**
 * Класс , представляющий команду для удаления пользователя по id
 */
public class RemoveByIdCommand implements ICommand {
    private final WorkerService service = WorkerService.getInstance();

    /**
     * Method thar removes worker by id in service
     *
     * @param args the id
     * @return the string
     */

    @Override
    public String execute(String... args) {
        if (args.length < 2)
            return "Пожалуйста укажите id удаляемого воркера!";
        if (!ConsoleHelper.isNumeric(args[1]))
            return "Пожалуйста укажите целое число в аргументе!";
        return service.removeById(Integer.parseInt(args[1]));

    }

    @Override
    public String toString() {
        return "remove_by_id {id} --> удалить элемент из коллекции по его id" ;
    }
}
