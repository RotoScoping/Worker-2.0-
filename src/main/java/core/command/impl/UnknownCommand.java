package core.command.impl;

import core.command.ICommand;

/**
 * Класс , представляющий команду для обработки несущессвующих команд, введенных пользователей
 */
public class UnknownCommand implements ICommand {
    @Override
    public String execute(String... args) {

        return String.format("""
                
                ОШИБКА: Неизвестная команда "%s"\s

                Использование:

                   worker<command> [optional args]

                Чтобы ознакомиться с полным списком команд, выполните:

                   worker help

                """, args[0]);

    }
}
