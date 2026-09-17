package com.onlineshopping.dao;

import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.exception.InsufficientStockException;
import com.onlineshopping.model.CartItem;
import com.onlineshopping.model.Order;
import com.onlineshopping.model.OrderItem;
import com.onlineshopping.model.Payment;
import com.onlineshopping.model.Product;
import com.onlineshopping.util.FileStorageManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Orders using local File I/O storage.
 * Demonstrates:
 * - Persistent transaction processing with File I/O
 * - Stock verification and exception throwing
 * - Collections (ArrayList)
 */
public class OrderDAO {

    public synchronized Order createOrder(int userId, List<CartItem> cartItems, String shippingAddress, double totalAmount, Payment paymentInfo) 
            throws DatabaseException, InsufficientStockException {

        List<Product> products = FileStorageManager.loadProducts();

        // 1. Verify stock availability
        for (CartItem item : cartItems) {
            Product freshProd = products.stream()
                .filter(p -> p.getId() == item.getProduct().getId())
                .findFirst()
                .orElse(null);

            if (freshProd == null) {
                throw new DatabaseException("Product '" + item.getProduct().getName() + "' is no longer available.");
            }
            if (freshProd.getStock() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + 
                    freshProd.getName() + " (Available: " + freshProd.getStock() + ", Requested: " + item.getQuantity() + ")");
            }
        }

        // 2. Generate Order ID
        List<Order> orders = FileStorageManager.loadOrders();
        int maxOrderId = 0;
        for (Order o : orders) {
            if (o.getId() > maxOrderId) maxOrderId = o.getId();
        }
        int newOrderId = maxOrderId + 1;

        // 3. Generate Order Items
        List<OrderItem> allOrderItems = FileStorageManager.loadOrderItems();
        int maxItemId = 0;
        for (OrderItem oi : allOrderItems) {
            if (oi.getId() > maxItemId) maxItemId = oi.getId();
        }

        List<OrderItem> createdItems = new ArrayList<>();
        for (CartItem ci : cartItems) {
            maxItemId++;
            OrderItem oi = new OrderItem(maxItemId, newOrderId, ci.getProduct().getId(), ci.getProduct().getName(), ci.getQuantity(), ci.getProduct().getPrice());
            allOrderItems.add(oi);
            createdItems.add(oi);

            // Deduct product stock
            for (Product p : products) {
                if (p.getId() == ci.getProduct().getId()) {
                    p.setStock(p.getStock() - ci.getQuantity());
                    break;
                }
            }
        }

        // 4. Generate Payment Record
        List<Payment> allPayments = FileStorageManager.loadPayments();
        int maxPayId = 0;
        for (Payment p : allPayments) {
            if (p.getId() > maxPayId) maxPayId = p.getId();
        }
        Payment payRecord = new Payment(maxPayId + 1, newOrderId, paymentInfo.getPaymentMethod(), "COMPLETED", paymentInfo.getTransactionDetails(), new Timestamp(System.currentTimeMillis()));
        allPayments.add(payRecord);

        // 5. Save Order
        Order newOrder = new Order(newOrderId, userId, new Timestamp(System.currentTimeMillis()), totalAmount, shippingAddress, "PENDING");
        newOrder.setItems(createdItems);
        newOrder.setPayment(payRecord);
        orders.add(newOrder);

        // 6. Clear Customer Cart
        List<CartItem> cartList = FileStorageManager.loadCartItems();
        cartList.removeIf(ci -> ci.getUserId() == userId);

        // Commit changes to disk files
        FileStorageManager.saveProducts(products);
        FileStorageManager.saveOrderItems(allOrderItems);
        FileStorageManager.savePayments(allPayments);
        FileStorageManager.saveOrders(orders);
        FileStorageManager.saveCartItems(cartList);

        return newOrder;
    }

    public List<Order> getOrdersByUser(int userId) throws DatabaseException {
        List<Order> allOrders = FileStorageManager.loadOrders();
        List<Order> userOrders = new ArrayList<>();

        for (Order o : allOrders) {
            if (o.getUserId() == userId) {
                userOrders.add(o);
            }
        }
        return userOrders;
    }

    public List<Order> getAllOrders() throws DatabaseException {
        return FileStorageManager.loadOrders();
    }

    public List<OrderItem> getOrderItems(int orderId) throws DatabaseException {
        List<OrderItem> allItems = FileStorageManager.loadOrderItems();
        List<OrderItem> matching = new ArrayList<>();

        for (OrderItem item : allItems) {
            if (item.getOrderId() == orderId) {
                matching.add(item);
            }
        }
        return matching;
    }

    public boolean updateOrderStatus(int orderId, String newStatus) throws DatabaseException {
        List<Order> orders = FileStorageManager.loadOrders();
        boolean updated = false;

        for (Order o : orders) {
            if (o.getId() == orderId) {
                o.setStatus(newStatus);
                updated = true;
                break;
            }
        }

        if (updated) {
            FileStorageManager.saveOrders(orders);
        }
        return updated;
    }

    public int getOrderCount() throws DatabaseException {
        return getAllOrders().size();
    }

    public double getTotalSales() throws DatabaseException {
        List<Order> orders = FileStorageManager.loadOrders();
        double sum = 0.0;

        for (Order o : orders) {
            if (!"CANCELLED".equalsIgnoreCase(o.getStatus())) {
                sum += o.getTotalAmount();
            }
        }
        return sum;
    }
}
