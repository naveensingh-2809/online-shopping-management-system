package com.onlineshopping.exception;

/**
 * Custom Exception thrown when attempting to checkout with an empty cart.
 */
public class EmptyCartException extends Exception {
    public EmptyCartException(String message) {
        super(message);
    }
}
