package org.example.gui.event;

import java.nio.file.Path;

public class ScriptExecutorEvent extends Event {

    private final Path path;
    public ScriptExecutorEvent(EventType type, Path path) {
        super(type);
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
