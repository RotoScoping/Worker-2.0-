package org.example.gui.event;

import org.example.model.Form;

public class AuthEvent extends Event{

    private final Form form;
    public AuthEvent(EventType type, Form form) {
        super(type);
        this.form = form;
    }

    public String getLogin() {
        return form.login();
    }

    public String getPassword() {
        return form.password();
    }


    public Form getForm() {
        return form;
    }
}
