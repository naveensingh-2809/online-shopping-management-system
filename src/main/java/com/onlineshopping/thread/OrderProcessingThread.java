package com.onlineshopping.thread;

import com.onlineshopping.model.Order;
import com.onlineshopping.util.FileManager;

import javax.swing.SwingUtilities;

/**
 * Background Multithreading class.
 * Demonstrates:
 * - Multithreading via Runnable interface
 * - Asynchronous background processing without blocking GUI thread
 * - SwingUtilities.invokeLater for UI safety
 */
public class OrderProcessingThread implements Runnable {

    private final Order order;
    private final String customerName;
    private final String paymentMethod;
    private final OrderProcessingCallback callback;

    public interface OrderProcessingCallback {
        void onSuccess(String receiptPath);
        void onError(String errorMessage);
    }

    public OrderProcessingThread(Order order, String customerName, String paymentMethod, OrderProcessingCallback callback) {
        this.order = order;
        this.customerName = customerName;
        this.paymentMethod = paymentMethod;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            // Simulate realistic background order processing delay (e.g. 1.5 seconds)
            Thread.sleep(1500);

            // Generate physical receipt file on background thread (File I/O)
            String receiptPath = FileManager.generateReceipt(order, customerName, paymentMethod);

            // Safely notify Swing GUI on Event Dispatch Thread
            if (callback != null) {
                SwingUtilities.invokeLater(() -> callback.onSuccess(receiptPath));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (callback != null) {
                SwingUtilities.invokeLater(() -> callback.onError("Order processing was interrupted."));
            }
        } catch (Exception e) {
            if (callback != null) {
                SwingUtilities.invokeLater(() -> callback.onError("Error generating receipt: " + e.getMessage()));
            }
        }
    }
}
