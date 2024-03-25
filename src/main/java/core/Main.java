package core;

import core.command.CommandResolver;
import core.command.ConsoleHelper;
import core.command.ICommand;
import core.logger.AsyncLogger;
import core.model.Worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

/**
 * The entry point of lab application
 */

public class Main {

    private static final AsyncLogger logger = AsyncLogger.get();

    /**
     * The Main method, that reads the user's input
     * @param args the input arguments
     */
    public static void main(String[] args)
    {
        logger.log(Level.INFO, "Начало работы приложения");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String[] commandAndArgs;
            do {
                System.out.print("Введите команду: ");
                commandAndArgs = reader.readLine().split(" ");
                ICommand command = CommandResolver.get(commandAndArgs[0]);
                System.out.println(command.execute(commandAndArgs));
            } while (!commandAndArgs[0].equals("exit"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при работе с вводом команд");
        }

        logger.log(Level.INFO, "Остановка приложения");
        logger.shutdown();
    }
}