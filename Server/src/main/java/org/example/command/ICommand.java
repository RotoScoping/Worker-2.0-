package org.example.command;

import org.example.model.Message;

import java.nio.ByteBuffer;

/**
 * Интерфейс команды, содержащий метод выполнения команды -execute()
 */
public interface ICommand {
    /**
     * Execute string.
     *
     * @param payload the UDP payload
     * @return the Message
     */
    Message execute(ByteBuffer payload);
}
