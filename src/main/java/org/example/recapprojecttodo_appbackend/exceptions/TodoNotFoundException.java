package org.example.recapprojecttodo_appbackend.exceptions;

public class TodoNotFoundException extends RuntimeException {


    public TodoNotFoundException(String id) {
        super("Todo with id " + id + " not found");
    }
}
