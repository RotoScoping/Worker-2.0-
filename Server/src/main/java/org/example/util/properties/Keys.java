package org.example.util.properties;

public enum Keys {

    PASSWORD_KEY("db.password"),
    USERNAME_KEY("db.username"),
    URL_KEY("db.url");

    private final String key;

    Keys(String key) {
        this.key = key;
    }

    public String get() {
        return key;
    }

}
