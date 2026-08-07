package com.enterprise.studentregistration.exception;

/** Thrown when the current password supplied on a change-password form is incorrect. */
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
