package com.onlineshopping.exception;

/**
 * Custom Exception thrown when product data validation fails.
 */
public class InvalidProductException extends Exception {
    public InvalidProductException(String message) {
        super(message);
    }
}
