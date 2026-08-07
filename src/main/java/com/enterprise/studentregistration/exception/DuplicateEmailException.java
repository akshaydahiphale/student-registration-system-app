package com.enterprise.studentregistration.exception;

/**
 * Thrown by the service layer as a defense-in-depth check even though
 * the @UniqueEmail bean validation constraint normally catches this
 * earlier in the request lifecycle.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
