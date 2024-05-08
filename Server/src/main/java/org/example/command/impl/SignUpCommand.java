package org.example.command.impl;

import org.example.AuthContext;
import org.example.command.ICommand;
import org.example.model.Form;
import org.example.model.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

public class SignUpCommand implements ICommand {
    private final AuthContext auth = AuthContext.get();
    @Override
    public Message execute(ByteBuffer payload) {
        try (var bis = new ByteArrayInputStream(payload.array(), 37, payload.array().length - 37);
             var ois = new ObjectInputStream(bis)) {
            Form form = (Form) ois.readObject();
            System.out.println("Получен объект: " + form);
            return auth.signUp(form);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
