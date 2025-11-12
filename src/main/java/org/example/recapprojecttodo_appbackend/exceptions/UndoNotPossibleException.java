package org.example.recapprojecttodo_appbackend.exceptions;

public class UndoNotPossibleException extends RuntimeException {
    public UndoNotPossibleException(String message) {
        super(message);
    }
}
