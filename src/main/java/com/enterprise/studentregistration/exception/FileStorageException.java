package com.enterprise.studentregistration.exception;

/** Thrown when a student photo upload fails (I/O error, invalid type, size limit). */
public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
