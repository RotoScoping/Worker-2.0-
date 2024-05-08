package org.example.model;

import java.io.Serializable;

public record Form (String login, String password) implements Serializable {
    @Override
    public String login() {
        return login;
    }

    @Override
    public String password() {
        return password;
    }
}
