package com.onlineshopping.model;

import java.sql.Timestamp;

/**
 * Model representing a Payment record in the database.
 */
public class Payment {
    private int id;
    private int orderId;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionDetails;
    private Timestamp paymentDate;

    public Payment() {}

    public Payment(int id, int orderId, String paymentMethod, String paymentStatus, String transactionDetails, Timestamp paymentDate) {
        this.id = id;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionDetails = transactionDetails;
        this.paymentDate = paymentDate;
    }

    public Payment(int orderId, String paymentMethod, String paymentStatus, String transactionDetails) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionDetails = transactionDetails;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getTransactionDetails() { return transactionDetails; }
    public void setTransactionDetails(String transactionDetails) { this.transactionDetails = transactionDetails; }

    public Timestamp getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }
}
