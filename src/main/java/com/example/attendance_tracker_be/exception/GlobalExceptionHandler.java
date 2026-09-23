package com.example.attendance_tracker_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException ex) {
        return ResponseEntity.status(ex.getStatus()).body(body(ex.getMessage(), ex.getStatus()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(InvalidCompanyException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCompany(InvalidCompanyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(InsufficientLeaveBalanceException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientLeaveBalance(InsufficientLeaveBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                err -> fieldErrors.put(err.getField(), err.getDefaultMessage())
        );

        Map<String, Object> body = body("Validation failed", HttpStatus.BAD_REQUEST);
        body.put("fields", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    private Map<String, Object> body(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}