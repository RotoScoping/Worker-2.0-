package org.example.command;


import org.example.command.impl.*;
import org.example.logger.AsyncLogger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Класс, который просматривает пользовательский ввод и возвращает команды
 */
public class CommandResolver {

    private static final AsyncLogger logger = AsyncLogger.get("server");

    private static final Map<String,ICommand> commandMap;

    private static final Set<String> scriptsHistory = new HashSet<>();

    static {
        commandMap = new HashMap<>();
        commandMap.put("help", new HelpCommand());
        commandMap.put("info", new InfoCommand());
        commandMap.put("show", new ShowCommand());
        commandMap.put("add", new AddCommand());
        commandMap.put("update_id", new UpdateCommand());
        commandMap.put("remove_by_id", new RemoveByIdCommand());
        commandMap.put("clear", new ClearCommand());
        commandMap.put("save", new SaveCommand());
        commandMap.put("exit", new ExitCommand());
        commandMap.put("remove_first", new RemoveFirstCommand());
        commandMap.put("add_if_max", new AddIfMaxCommand());
        commandMap.put("add_if_min", new AddIfMinCommand());
        commandMap.put("average_of_salary", new AvarageOfSalaryCommand());
        commandMap.put("print_field_ascending_status", new PrintFieldAscendingStatus());
        commandMap.put("print_field_descending_organization", new PrintFieldDescendingOrganization());
    }


    /**
     * Метод который резолвит команду
     *
     * @param command the command
     * @return boolean command
     */
    public static ICommand get (String command) {
        logger.log(Level.INFO, "Разрешение " + command + "команды в списке доступных команд");
        return commandMap.getOrDefault(command.trim(),new UnknownCommand());


    }

    /**
     * Метод который добавляет абсолютный путь запущенного скрипта во множество, которое не допускает дублей
     * Это позволяет затрекать циклические вызовы
     *
     * @param currentScriptAbsolutePath the current script absolute path
     * @return boolean flag that indicates recursive invoke
     */
    public static boolean setCurrentScript(String currentScriptAbsolutePath) {
        logger.log(Level.INFO, "Добавление пути " + currentScriptAbsolutePath + " скрипта в трекер");
        return scriptsHistory.add(currentScriptAbsolutePath);
    }


}
