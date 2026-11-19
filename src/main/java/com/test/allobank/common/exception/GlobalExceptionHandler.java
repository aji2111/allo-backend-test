package com.test.allobank.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(
            EntityNotFoundException ex
    ) {

        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(
            IllegalArgumentException ex
    ) {

        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleInvalidRequest(
            HttpMessageNotReadableException ex
    ) {

        String message = "Invalid request payload";

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException) {

            if (
                    invalidFormatException.getTargetType()
                            .isEnum()
            ) {

                message =
                        "Invalid splitType. Allowed values: EQUAL, EXACT, PERCENTAGE";
            }
        }

        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "message", message
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(
            Exception ex
    ) {

        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 500,
                "message", "Internal server error"
        );
    }
}