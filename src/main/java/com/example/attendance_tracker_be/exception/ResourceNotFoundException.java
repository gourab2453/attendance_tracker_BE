package com.example.attendance_tracker_be.exception;

/**
 * Thrown when a requested resource (user, task, leave request, etc.) doesn't exist.
 * Mapped to 404 in GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}