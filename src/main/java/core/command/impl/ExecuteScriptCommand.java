package core.command.impl;

import core.command.CommandResolver;
import core.command.ConsoleHelper;
import core.command.ICommand;
import core.logger.AsyncLogger;

import java.io.*;
import java.sql.SQLOutput;
import java.util.logging.Level;


/**
 * Класс команды, которая запускает на исполнение файл скрипта.
 */
public class ExecuteScriptCommand implements ICommand {

    private static final AsyncLogger logger = AsyncLogger.get();

    /**
     * Method that executes user's script
     * @return A string describing the success or failure of the operation
     */

    @Override
    public String execute(String... args) {
        if (args.length < 2 ) return "Не был передан путь!";
        File file = new File(args[1]);
        if (!file.canRead()) {
            logger.log(Level.WARNING, String.format("Нет прав на чтение файла %s", file.getName()));
            return "Нет прав на чтение скрипта";
        }
        try (var reader = new BufferedReader(new FileReader(file))) {
            if (!CommandResolver.setCurrentScript(file.getAbsolutePath())) {
                logger.log(Level.WARNING, String.format("Рекурсивный вызов скрипта %s", file.getName()));
                return "Рекурсивный вызов скрипта!";
            }
            while (reader.ready()) {
                String[] commandAndArgs = reader.readLine()
                        .split(" ");
                ICommand command = CommandResolver.get(commandAndArgs[0]);
                System.out.println(command.execute(commandAndArgs));
            }
        } catch (FileNotFoundException e) {
            return "Скрипт не найден ";
        } catch (IOException e) {
            return "Что то пошло не так!";
        }
        return "Скрипт исполнен!";
    }

    @Override
    public String toString() {
        return "execute_script {path} --> считать и исполнить скрипт из указанного файла.";
    }
}
