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

    private static final Map<Integer,ICommand> commandMap;

    private static final Set<String> scriptsHistory = new HashSet<>();

    static {
        commandMap = new HashMap<>();
        commandMap.put(10, new HelpCommand());
        commandMap.put(20, new InfoCommand());
        commandMap.put(30, new ShowCommand());
        commandMap.put(11, new AddCommand());
        commandMap.put(21, new UpdateCommand());
        commandMap.put(32, new RemoveByIdCommand());
        commandMap.put(40, new ClearCommand());
        commandMap.put(60, new RemoveFirstCommand());
        commandMap.put(41, new AddIfMaxCommand());
        commandMap.put(51, new AddIfMinCommand());
        commandMap.put(70, new AvarageOfSalaryCommand());
        commandMap.put(80, new PrintFieldAscendingStatus());
        commandMap.put(90, new PrintFieldDescendingOrganization());
        commandMap.put(101, new SignUpCommand());
        commandMap.put(111, new SignInCommand());
    }


    /**
     * Метод который резолвит команду
     *
     * @param operationCode the command
     * @return boolean command
     */
    public static ICommand get (int operationCode) {
        return commandMap.getOrDefault(operationCode,new UnknownCommand());
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
