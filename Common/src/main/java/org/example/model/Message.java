package org.example.model;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {


    private String message;

    private List<Worker> workers;


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
}
