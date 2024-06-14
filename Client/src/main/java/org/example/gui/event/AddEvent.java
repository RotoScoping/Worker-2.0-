package org.example.gui.event;

import org.example.model.Form;
import org.example.model.Worker;

public class AddEvent extends Event {

        private final Worker worker;
        public AddEvent(EventType type, Worker worker) {
            super(type);
            this.worker = worker;
        }

    public Worker getWorker() {
        return worker;
    }
}
