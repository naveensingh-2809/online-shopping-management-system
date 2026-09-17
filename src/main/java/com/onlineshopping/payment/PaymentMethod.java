package com.onlineshopping.payment;

/**
 * Interface representing payment strategies.
 * Demonstrates:
 * - Interface declaration
 * - Polymorphism (dynamic method dispatch at checkout runtime)
 */
public interface PaymentMethod {
    /**
     * Executes payment processing.
     * @param amount Total order amount to process
     * @return true if payment succeeds
     */
    boolean processPayment(double amount);

    /**
     * Gets summary string of payment method details.
     * @return Human-readable transaction summary
     */
    String getPaymentDetails();

    /**
     * Gets name of the payment type.
     * @return Payment method name (e.g., "CARD", "UPI", "COD")
     */
    String getMethodName();
}
