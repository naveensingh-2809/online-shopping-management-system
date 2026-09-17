package com.onlineshopping.exception;

/**
 * Custom Exception thrown when authentication fails.
 */
public class InvalidLoginException extends Exception {
    public InvalidLoginException(String message) {
        super(message);
    }
}
