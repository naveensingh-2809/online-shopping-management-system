package com.onlineshopping.payment;

/**
 * Concrete Credit/Debit Card payment implementation.
 * Demonstrates Interface Implementation and Polymorphism.
 */
public class CardPayment implements PaymentMethod {
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;

    public CardPayment(String cardNumber, String cardHolderName, String expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    @Override
    public boolean processPayment(double amount) {
        // Validate card parameters
        if (cardNumber == null || cardNumber.replaceAll("\\s+", "").length() < 13) {
            return false;
        }
        if (cvv == null || cvv.length() < 3) {
            return false;
        }
        // Simulated authorization success
        return true;
    }

    @Override
    public String getPaymentDetails() {
        String maskedCard = cardNumber.length() > 4 ? 
            "**** **** **** " + cardNumber.substring(cardNumber.length() - 4) : "****";
        return "Credit/Debit Card (" + maskedCard + ") - Holder: " + cardHolderName;
    }

    @Override
    public String getMethodName() {
        return "CARD";
    }
}
