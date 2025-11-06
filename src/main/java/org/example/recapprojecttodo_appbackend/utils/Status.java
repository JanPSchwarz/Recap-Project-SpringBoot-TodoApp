package org.example.recapprojecttodo_appbackend.utils;

public enum Status {
    OPEN("open"),
    IN_PROGRESS("inProgress"),
    DONE("done");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
