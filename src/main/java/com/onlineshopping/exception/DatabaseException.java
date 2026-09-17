package com.onlineshopping.exception;

/**
 * Custom Exception thrown when database operations encounter errors.
 * Demonstrates Java Exception Handling (Custom Exception subclassing Exception).
 */
public class DatabaseException extends Exception {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
