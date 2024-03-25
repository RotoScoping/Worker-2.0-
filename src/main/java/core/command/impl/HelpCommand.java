package core.command.impl;

import core.command.ICommand;
import core.logger.AsyncLogger;

import java.util.logging.Level;
/**
 * Класс, который представляет команду help.
 */
public class HelpCommand implements ICommand {

    private static final AsyncLogger logger = AsyncLogger.get();
    @Override
    public String execute(String... args) {
        logger.log(Level.INFO, "Исполнение команды: help");
        return """

                CLI помогает вам взаимодействовать с сервисом Worker

                Использование:

                \tworker <command>

                Commands:

                \thelp --> вывести справку по доступным командам

                \tinfo --> вывести в стандартный поток вывода информацию о коллекции

                \tshow --> вывести в стандартный поток вывода все элементы коллекции в строковом представлении

                \tadd {worker} --> добавить новый элемент в коллекцию (worker add -help for more details)

                \tupdate_id {worker} -->  обновить значение элемента коллекции, id которого равен заданному

                \tremove_by_id {id} --> удалить элемент из коллекции по его id

                \tclear --> очистить коллекцию

                \tsave --> сохранить коллекцию в файл

                \texecute_script {path} --> считать и исполнить скрипт из указанного файла.

                \texit --> завершить программу (без сохранения в файл)

                \tremove_first --> удалить первый элемент из коллекции

                \tadd_if_max {worker} --> добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции

                \tadd_if_min {worker} --> добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции

                \taverage_of_salary --> вывести среднее значение поля salary для всех элементов коллекции

                \tprint_field_ascending_status --> вывести значения поля status всех элементов в порядке возрастания

                \tprint_field_descending_organization --> вывести значения поля organization всех элементов в порядке убывания""";
    }
}
