package org.example.command.impl;

import org.example.command.ICommand;
import org.example.logger.AsyncLogger;
import org.example.model.Message;

import java.nio.ByteBuffer;
import java.util.logging.Level;

/**
 * Класс, который представляет команду help.
 */
public class HelpCommand implements ICommand {

    private static final AsyncLogger logger = AsyncLogger.get("server");

    /**
     * Method that returns help-info.
     * @return A string with help information
     */
    @Override
    public Message execute(ByteBuffer payload) {
        logger.log(Level.INFO, "Исполнение команды: help");
        return new Message(String.format("""

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
                
                """, new InfoCommand(), new ShowCommand(), new AddCommand(),new UpdateCommand(), new RemoveByIdCommand(), new ClearCommand(), new ExitCommand(), new RemoveFirstCommand(), new AddIfMaxCommand(), new AddIfMinCommand(), new AvarageOfSalaryCommand(), new PrintFieldAscendingStatus(), new PrintFieldDescendingOrganization()));
    }
}
