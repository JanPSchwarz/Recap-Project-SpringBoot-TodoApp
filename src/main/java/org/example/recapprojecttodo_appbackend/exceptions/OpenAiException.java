package org.example.recapprojecttodo_appbackend.exceptions;

public class OpenAiException extends RuntimeException {
    public OpenAiException(String message) {
        super(message);
    }
}
