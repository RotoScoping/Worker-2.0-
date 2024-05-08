package org.example.command.impl;

import org.example.command.ICommand;
import org.example.model.Message;

import java.nio.ByteBuffer;

/**
 * Класс , представляющий команду для обработки несущессвующих команд, введенных пользователей
 */
public class UnknownCommand implements ICommand {
    @Override
    public Message execute(ByteBuffer payload) {
        payload.rewind();
        return new Message(String.format("""
                
                ОШИБКА: Неизвестная команда "%d"\s

                Использование:

                   worker<command> [optional args]

                Чтобы ознакомиться с полным списком команд, выполните:

                   worker help

                """, payload.get()));

    }
}
