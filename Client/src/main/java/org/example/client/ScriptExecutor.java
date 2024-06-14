package org.example.client;

import org.example.client.Client;
import org.example.gui.event.Event;
import org.example.gui.event.EventType;
import org.example.gui.event.ScriptExecutorEvent;
import org.example.logger.AsyncLogger;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;


/**
 * Класс команды, которая запускает на исполнение файл скрипта.
 */
public class ScriptExecutor {

    private static final AsyncLogger logger = AsyncLogger.get("client");

    /**
     * Method that executes user's script
     *
     * @return A string describing the success or failure of the operation
     */

    private static final Set<String> scriptsHistory = new LinkedHashSet<>();

    private String currentScript;

    public String execute(Client client, Event event) {

        ScriptExecutorEvent scriptEvent = (ScriptExecutorEvent) event;
        File file = new File(scriptEvent.getPath().toUri());
        if (!file.canRead()) {
            return String.format("Нет прав на чтение %s", file.getName());
        }
        try (var reader = new BufferedReader(new FileReader(file))) {
            if (!setCurrentScript(file.getAbsolutePath())) {
                return String.format("Рекурсивный вызов %s!", file.getName());
            }
            while (reader.ready()) {
                String[] commandAndArg = reader.readLine()
                        .split(" ");
                client.sendPacket(new Event(EventType.EXECUTE_SCRIPT));
            }
        } catch (FileNotFoundException e) {
            return String.format("Скрипт %s не найден!" , file.getName());
        } catch (IOException e) {
            return String.format("%s: Что то пошло не так!", file.getName());
        }
        deleteCurrentScript();
        return String.format("Скрипт %s исполнен!", file.getName());
    }

    private boolean setCurrentScript(String currentScriptAbsolutePath) {
        boolean isAdded = scriptsHistory.add(currentScriptAbsolutePath);
        if (isAdded) {
            logger.log(Level.INFO, "Добавление пути " + currentScriptAbsolutePath + " скрипта в трекер");
            currentScript = currentScriptAbsolutePath;
        }
        return isAdded;
    }

    private void deleteCurrentScript() {
        scriptsHistory.remove(currentScript);
    }


}
