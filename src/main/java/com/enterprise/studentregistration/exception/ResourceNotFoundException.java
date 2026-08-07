package com.enterprise.studentregistration.exception;

/**
 * Thrown when a requested entity (Student, User, etc.) cannot be found.
 * Mapped by GlobalExceptionHandler to a friendly 404 page.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
