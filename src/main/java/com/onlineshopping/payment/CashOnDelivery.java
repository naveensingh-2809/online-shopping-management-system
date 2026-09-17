package com.onlineshopping.payment;

/**
 * Concrete Cash On Delivery payment implementation.
 * Demonstrates Interface Implementation and Polymorphism.
 */
public class CashOnDelivery implements PaymentMethod {
    private String deliveryAddress;

    public CashOnDelivery(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    public boolean processPayment(double amount) {
        // COD requires valid shipping address; payment collected upon delivery
        return deliveryAddress != null && !deliveryAddress.trim().isEmpty();
    }

    @Override
    public String getPaymentDetails() {
        return "Cash On Delivery (Pay cash upon shipment receipt at: " + deliveryAddress + ")";
    }

    @Override
    public String getMethodName() {
        return "CASH_ON_DELIVERY";
    }
}
