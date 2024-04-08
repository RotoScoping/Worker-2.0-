package core.command.impl;

import core.command.ICommand;
import core.logger.AsyncLogger;

import java.util.logging.Level;

/**
 * Класс, который представляет команду help.
 */
public class HelpCommand implements ICommand {

    private static final AsyncLogger logger = AsyncLogger.get();

    /**
     * Method that returns help-info.
     * @return A string with help information
     */
    @Override
    public String execute(String... args) {
        logger.log(Level.INFO, "Исполнение команды: help");
        return String.format("""

                CLI помогает вам взаимодействовать с сервисом Worker

                Использование:

                \tworker <command>

                Commands:

                \t%s

                \t%s

                \t%s

                \t%s

                \t%s

                \t%s

                \t%s

                \t%s

                \t%s

                \t%s

                \t%s

                \t%s

                \t%s

                \t%s

                \t%s
                
                """, new InfoCommand(), new ShowCommand(), new AddCommand(),new UpdateCommand(), new RemoveByIdCommand(), new ClearCommand(), new SaveCommand(), new ExecuteScriptCommand(), new ExitCommand(), new RemoveFirstCommand(), new AddIfMaxCommand(), new AddIfMinCommand(), new AvarageOfSalaryCommand(), new PrintFieldAscendingStatus(), new PrintFieldDescendingOrganization());
    }
}
