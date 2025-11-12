package org.example.recapprojecttodo_appbackend.exceptions;

import org.example.recapprojecttodo_appbackend.models.ErrorMessage;
import org.example.recapprojecttodo_appbackend.utils.IdService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Comparator;

@RestControllerAdvice
public class GlobalExceptionHandler {

    IdService idService = new IdService();

    @ExceptionHandler(TodoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessage> handleTodoNotFoundException(TodoNotFoundException ex) {
        ErrorMessage errorMessage = createErrorMessage(ex.getMessage(), HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(UndoNotPossibleException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<ErrorMessage> handleUndoNotPossibleException(UndoNotPossibleException ex) {
        ErrorMessage errorMessage = createErrorMessage(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE.value());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorMessage errorMessage = createErrorMessage("Request Body must not be empty", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder messageBuilder = new StringBuilder();

        ex.getBindingResult().getFieldErrors().stream()
                .sorted(Comparator.comparing(FieldError::getField)).forEach(error -> messageBuilder.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));

        ErrorMessage errorMessage = createErrorMessage(messageBuilder.toString(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    ErrorMessage createErrorMessage(String errorMessage, int httpStatus) {
        return new ErrorMessage(Instant.now().toString(), errorMessage, idService.generateErrorId(), httpStatus);
    }
}
