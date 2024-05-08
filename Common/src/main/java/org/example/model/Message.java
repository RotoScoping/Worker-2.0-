package org.example.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Message implements Serializable {


    private String message;

    private List<Worker> workers;

    private UUID token;


    public Message() {};
    public Message(String message) {
        this(message, null);
    }

    public Message(String message, List<Worker> workers) {
        this.message = message;
        this.workers = workers;

    }

    public String getMessage() {
        return message;
    }

    public List<Worker> getWorkers() {
        return workers;
    }


    public void setToken(UUID token) {
        this.token = token;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    public UUID getToken() {
        return token;
    }
}

