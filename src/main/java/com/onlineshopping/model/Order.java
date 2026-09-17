package com.onlineshopping.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a Customer Order.
 * Demonstrates Collections Framework (List<OrderItem>).
 */
public class Order {
    private int id;
    private int userId;
    private String customerName;
    private Timestamp orderDate;
    private double totalAmount;
    private String shippingAddress;
    private String status; // PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    private List<OrderItem> items = new ArrayList<>();
    private Payment payment;

    public Order() {}

    public Order(int id, int userId, Timestamp orderDate, double totalAmount, String shippingAddress, String status) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
}
