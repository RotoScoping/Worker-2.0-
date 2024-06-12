package org.example.command.impl;

import org.example.auth.AuthContext;
import org.example.command.ICommand;
import org.example.model.Form;
import org.example.model.Message;
import org.example.util.Decrypto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

public class SignInCommand implements ICommand {

    private final AuthContext auth = AuthContext.get();
    @Override
    public Message execute(ByteBuffer payload) {
        try (var bis = new ByteArrayInputStream(payload.array(), 37, payload.array().length - 37);
             var ois = new ObjectInputStream(bis)) {
            Form form = (Form) ois.readObject();
            System.out.println("Получен объект: " + form);
            return auth.signIn(Decrypto.getDencryptedForm(form));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}
