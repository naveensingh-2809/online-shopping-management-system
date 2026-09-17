package com.onlineshopping.exception;

/**
 * Custom Exception thrown when requested product quantity exceeds available stock.
 */
public class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) {
        super(message);
    }
}
