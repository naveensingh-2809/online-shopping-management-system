package com.onlineshopping.service;

import com.onlineshopping.dao.OrderDAO;
import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.exception.EmptyCartException;
import com.onlineshopping.exception.InsufficientStockException;
import com.onlineshopping.model.CartItem;
import com.onlineshopping.model.Order;
import com.onlineshopping.model.Payment;
import com.onlineshopping.payment.PaymentMethod;
import com.onlineshopping.thread.OrderProcessingThread;

import java.util.List;

/**
 * Service coordinating Order Checkout, Payment Processing Polymorphism,
 * Database Transactions, and Multithreaded Receipt Generation.
 */
public class OrderService {

    private final OrderDAO orderDAO;
    private final CartService cartService;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.cartService = new CartService();
    }

    /**
     * Executes order checkout flow:
     * 1. Validates non-empty cart
     * 2. Executes Payment Strategy processPayment() (Polymorphism)
     * 3. Creates DB Transaction (OrderDAO)
     * 4. Triggers background Multithreaded receipt generator
     */
    public void checkout(int userId, String customerName, String shippingAddress, PaymentMethod paymentMethod, 
                         OrderProcessingThread.OrderProcessingCallback callback) 
            throws EmptyCartException, InsufficientStockException, DatabaseException, IllegalArgumentException {

        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping address cannot be empty.");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method must be selected.");
        }

        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new EmptyCartException("Your shopping cart is empty! Add items before checkout.");
        }

        double totalAmount = cartService.calculateCartTotal(cartItems);

        // Execute Polymorphic Payment Processing Strategy
        boolean paymentSuccess = paymentMethod.processPayment(totalAmount);
        if (!paymentSuccess) {
            throw new IllegalArgumentException("Payment authorization failed. Please check payment details.");
        }

        Payment paymentRecord = new Payment(0, paymentMethod.getMethodName(), "COMPLETED", paymentMethod.getPaymentDetails());

        // Perform atomic Database Transaction
        Order order = orderDAO.createOrder(userId, cartItems, shippingAddress.trim(), totalAmount, paymentRecord);

        // Execute multithreaded post-processing (Background Runnable)
        OrderProcessingThread processingThread = new OrderProcessingThread(order, customerName, paymentMethod.getPaymentDetails(), callback);
        Thread thread = new Thread(processingThread, "OrderProcessingThread-Order#" + order.getId());
        thread.start();
    }

    public List<Order> getCustomerOrders(int userId) throws DatabaseException {
        return orderDAO.getOrdersByUser(userId);
    }

    public List<Order> getAllOrders() throws DatabaseException {
        return orderDAO.getAllOrders();
    }

    public void updateOrderStatus(int orderId, String newStatus) throws DatabaseException {
        orderDAO.updateOrderStatus(orderId, newStatus);
    }
}
