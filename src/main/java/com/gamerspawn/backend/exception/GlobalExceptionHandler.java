package com.gamerspawn.backend.exception;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleInvalidParameter(
            MethodArgumentTypeMismatchException exception) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Bad Request",
                "message", "El parámetro '" + exception.getName() + "' tiene un formato inválido."));
    }
}