package com.onlineshopping.payment;

/**
 * Concrete UPI payment implementation (e.g. Google Pay / PhonePe / Paytm).
 * Demonstrates Interface Implementation and Polymorphism.
 */
public class UPIPayment implements PaymentMethod {
    private String upiId;

    public UPIPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public boolean processPayment(double amount) {
        if (upiId == null || !upiId.contains("@")) {
            return false;
        }
        // Simulated instant payment gateway confirmation
        return true;
    }

    @Override
    public String getPaymentDetails() {
        return "UPI Transfer (VPA: " + upiId + ")";
    }

    @Override
    public String getMethodName() {
        return "UPI";
    }
}
