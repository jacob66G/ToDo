package com.example.ToDo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                e.getMessage(),
                "The requested resource was not found.",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateNameException(DuplicateNameException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                409,
                e.getMessage(),
                "Set name failed for the request.",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse errorResponse = new ErrorResponse(
            400,
                "Validation failed for the request.",
                errors.toString(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HasAssociatedTasksException.class)
    public ResponseEntity<ErrorResponse> handleHasAssociatedTasksException(HasAssociatedTasksException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                409,
                e.getMessage(),
                "Delete resource failed.",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DefaultDataDeletionException.class)
    public ResponseEntity<ErrorResponse> handleDefaultDataDeletionException(DefaultDataDeletionException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                409,
                e.getMessage(),
                "Delete resource failed.",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
