package core.command.impl;

import core.command.CommandResolver;
import core.command.ConsoleHelper;
import core.command.ICommand;

import java.io.*;
/**
 * Класс команды, которая запускает на исполнение файл скрипта.
 */
public class ExecuteScriptCommand implements ICommand {


    @Override
    public String execute(String... args) {

        try (var reader = new BufferedReader(new FileReader(args[1]))) {
            if (!CommandResolver.setCurrentScript(new File(args[1]).getAbsolutePath())) {

                return "Рекурсивный вызов скрипта!";
            }
            while (reader.ready()) {
                String[] commandAndArgs = reader.readLine()
                        .split(" ");
                ICommand command = CommandResolver.get(commandAndArgs[0]);
                System.out.println(command.execute(commandAndArgs));
            }
        } catch (FileNotFoundException e) {
            return "Файл не найден ";
        } catch (IOException e) {
            return "Что то пошло не так!";
        }
        return "Скрипт исполнен!";
    }



}
