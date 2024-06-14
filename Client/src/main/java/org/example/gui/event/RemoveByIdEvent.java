package org.example.gui.event;

import org.example.model.Worker;

public class RemoveByIdEvent extends Event{
    private final int id;
    public RemoveByIdEvent(EventType type, int id) {
        super(type);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
