package com.onlineshopping.util;

import com.onlineshopping.model.Admin;
import com.onlineshopping.model.CartItem;
import com.onlineshopping.model.Customer;
import com.onlineshopping.model.Order;
import com.onlineshopping.model.OrderItem;
import com.onlineshopping.model.Payment;
import com.onlineshopping.model.Product;
import com.onlineshopping.model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Central File Storage Persistence Manager.
 * Uses local Java File I/O Streams to persist entities in the data/ directory.
 */
public class FileStorageManager {

    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.txt";
    private static final String PRODUCTS_FILE = DATA_DIR + File.separator + "products.txt";
    private static final String CART_FILE = DATA_DIR + File.separator + "cart.txt";
    private static final String ORDERS_FILE = DATA_DIR + File.separator + "orders.txt";
    private static final String ORDER_ITEMS_FILE = DATA_DIR + File.separator + "order_items.txt";
    private static final String PAYMENTS_FILE = DATA_DIR + File.separator + "payments.txt";

    private static final String DELIMITER = "///";

    static {
        initDataDirectory();
    }

    public static synchronized void initDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File usersFile = new File(USERS_FILE);
        if (!usersFile.exists() || isDefaultUserMissing()) {
            seedDefaultUsers();
        }

        File productsFile = new File(PRODUCTS_FILE);
        if (!productsFile.exists() || loadProducts().isEmpty()) {
            seedDefaultProducts();
        }

        createFileIfNotExists(CART_FILE);
        createFileIfNotExists(ORDERS_FILE);
        createFileIfNotExists(ORDER_ITEMS_FILE);
        createFileIfNotExists(PAYMENTS_FILE);
    }

    private static void createFileIfNotExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file " + path + ": " + e.getMessage());
            }
        }
    }

    private static boolean isDefaultUserMissing() {
        List<User> users = loadUsers();
        boolean hasAdmin = false;
        boolean hasJohn = false;

        String adminHash = PasswordUtil.hashPassword("admin123");
        String johnHash = PasswordUtil.hashPassword("customer123");

        for (User u : users) {
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase("admin@shopping.com") && u.getPassword().equals(adminHash)) {
                hasAdmin = true;
            }
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase("john@gmail.com") && u.getPassword().equals(johnHash)) {
                hasJohn = true;
            }
        }
        return !hasAdmin || !hasJohn;
    }

    public static synchronized void seedDefaultUsers() {
        List<User> users = loadUsers();
        boolean hasAdmin = false;
        boolean hasJohn = false;

        String adminHash = PasswordUtil.hashPassword("admin123");
        String johnHash = PasswordUtil.hashPassword("customer123");

        for (User u : users) {
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase("admin@shopping.com")) {
                u.setPassword(adminHash);
                hasAdmin = true;
            }
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase("john@gmail.com")) {
                u.setPassword(johnHash);
                hasJohn = true;
            }
        }

        int maxId = 0;
        for (User u : users) {
            if (u.getId() > maxId) maxId = u.getId();
        }

        if (!hasAdmin) {
            maxId++;
            users.add(new Admin(maxId, "System Admin", "admin@shopping.com", adminHash, "SUPER_ADMIN"));
        }

        if (!hasJohn) {
            maxId++;
            users.add(new Customer(maxId, "John Doe", "john@gmail.com", johnHash, "123 Park Street, New York, NY", "9123456789"));
        }

        saveUsers(users);
    }

    public static synchronized void seedDefaultProducts() {
        List<Product> defaultProducts = new ArrayList<>();
        defaultProducts.add(new Product(1, "Wireless Noise-Canceling Headphones", "Electronics", "High quality over-ear bluetooth headphones with active noise cancellation.", 149.99, 25));
        defaultProducts.add(new Product(2, "Smartphone Pro 128GB", "Electronics", "Flagship smartphone featuring AMOLED display and triple camera system.", 799.00, 15));
        defaultProducts.add(new Product(3, "4K Ultra HD Smart Monitor 27\"", "Electronics", "Crisp 4K resolution monitor with USB-C connectivity for work and gaming.", 320.50, 10));
        defaultProducts.add(new Product(4, "Men Cotton Casual Shirt", "Clothing", "100% breathable cotton slim-fit casual button-down shirt.", 29.99, 50));
        defaultProducts.add(new Product(5, "Women Classic Denim Jacket", "Clothing", "Timeless denim jacket with durable stitching and modern fit.", 49.95, 30));
        defaultProducts.add(new Product(6, "Java Programming Masterclass", "Books", "Comprehensive guide covering Java OOP, Data Structures, Threads, and Swing.", 39.99, 100));
        defaultProducts.add(new Product(7, "Clean Code Architecture", "Books", "Essential handbook of agile software craftmanship and design patterns.", 44.50, 40));
        defaultProducts.add(new Product(8, "Leather Crossbody Shoulder Bag", "Accessories", "Genuine handcrafted leather bag with adjustable strap and multiple zip pockets.", 65.00, 20));
        defaultProducts.add(new Product(9, "Smart Fitness Smartwatch", "Accessories", "Waterproof fitness tracker with heart rate monitor and GPS tracking.", 89.99, 35));
        defaultProducts.add(new Product(10, "Ergonomic Memory Foam Pillow", "Home", "Orthopedic neck support pillow for comfortable sleeping posture.", 24.99, 45));
        defaultProducts.add(new Product(11, "Stainless Steel Thermal Flask 1L", "Home", "Double-wall vacuum insulated water bottle keeping drinks hot/cold for 24 hours.", 18.50, 60));

        saveProducts(defaultProducts);
    }

    // ============================================================
    // USER FILE OPERATIONS
    // ============================================================
    public static synchronized List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(DELIMITER, -1);
                if (parts.length >= 7) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String email = parts[2].trim();
                    String password = parts[3].trim();
                    String role = parts[4].trim();
                    String address = parts[5].trim();
                    String phone = parts[6].trim();

                    if ("ADMIN".equalsIgnoreCase(role)) {
                        users.add(new Admin(id, name, email, password, "SUPER_ADMIN"));
                    } else {
                        users.add(new Customer(id, name, email, password, address, phone));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users.txt: " + e.getMessage());
        }
        return users;
    }

    public static synchronized void saveUsers(List<User> users) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(USERS_FILE, StandardCharsets.UTF_8, false)))) {
            for (User u : users) {
                String address = (u instanceof Customer) ? ((Customer) u).getAddress() : "";
                String phone = (u instanceof Customer) ? ((Customer) u).getPhone() : "";
                writer.println(u.getId() + DELIMITER + 
                               clean(u.getName()) + DELIMITER + 
                               clean(u.getEmail()) + DELIMITER + 
                               clean(u.getPassword()) + DELIMITER + 
                               clean(u.getRole()) + DELIMITER + 
                               clean(address) + DELIMITER + 
                               clean(phone));
            }
        } catch (IOException e) {
            System.err.println("Error writing users.txt: " + e.getMessage());
        }
    }

    // ============================================================
    // PRODUCT FILE OPERATIONS
    // ============================================================
    public static synchronized List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return products;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(DELIMITER, -1);
                if (parts.length >= 6) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String category = parts[2].trim();
                    String desc = parts[3].trim();
                    double price = Double.parseDouble(parts[4].trim());
                    int stock = Integer.parseInt(parts[5].trim());
                    products.add(new Product(id, name, category, desc, price, stock));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading products.txt: " + e.getMessage());
        }
        return products;
    }

    public static synchronized void saveProducts(List<Product> products) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(PRODUCTS_FILE, StandardCharsets.UTF_8, false)))) {
            for (Product p : products) {
                writer.println(p.getId() + DELIMITER + 
                               clean(p.getName()) + DELIMITER + 
                               clean(p.getCategory()) + DELIMITER + 
                               clean(p.getDescription()) + DELIMITER + 
                               p.getPrice() + DELIMITER + 
                               p.getStock());
            }
        } catch (IOException e) {
            System.err.println("Error writing products.txt: " + e.getMessage());
        }
    }

    // ============================================================
    // CART FILE OPERATIONS
    // ============================================================
    public static synchronized List<CartItem> loadCartItems() {
        List<CartItem> items = new ArrayList<>();
        File file = new File(CART_FILE);
        if (!file.exists()) return items;

        List<Product> products = loadProducts();

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(DELIMITER, -1);
                if (parts.length >= 4) {
                    int id = Integer.parseInt(parts[0].trim());
                    int userId = Integer.parseInt(parts[1].trim());
                    int productId = Integer.parseInt(parts[2].trim());
                    int quantity = Integer.parseInt(parts[3].trim());

                    Product matchingProduct = products.stream()
                        .filter(p -> p.getId() == productId)
                        .findFirst()
                        .orElse(null);

                    if (matchingProduct != null) {
                        items.add(new CartItem(id, userId, matchingProduct, quantity));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading cart.txt: " + e.getMessage());
        }
        return items;
    }

    public static synchronized void saveCartItems(List<CartItem> items) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(CART_FILE, StandardCharsets.UTF_8, false)))) {
            for (CartItem item : items) {
                if (item.getProduct() != null) {
                    writer.println(item.getId() + DELIMITER + 
                                   item.getUserId() + DELIMITER + 
                                   item.getProduct().getId() + DELIMITER + 
                                   item.getQuantity());
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing cart.txt: " + e.getMessage());
        }
    }

    // ============================================================
    // ORDER & ORDER ITEMS FILE OPERATIONS
    // ============================================================
    public static synchronized List<Order> loadOrders() {
        List<Order> orders = new ArrayList<>();
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return orders;

        List<User> users = loadUsers();
        List<OrderItem> allOrderItems = loadOrderItems();
        List<Payment> allPayments = loadPayments();

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(DELIMITER, -1);
                if (parts.length >= 6) {
                    int id = Integer.parseInt(parts[0].trim());
                    int userId = Integer.parseInt(parts[1].trim());
                    Timestamp date = Timestamp.valueOf(parts[2].trim());
                    double total = Double.parseDouble(parts[3].trim());
                    String shipping = parts[4].trim();
                    String status = parts[5].trim();

                    Order o = new Order(id, userId, date, total, shipping, status);

                    // Find customer name
                    users.stream().filter(u -> u.getId() == userId).findFirst()
                         .ifPresent(u -> o.setCustomerName(u.getName()));

                    // Attach order items
                    for (OrderItem item : allOrderItems) {
                        if (item.getOrderId() == id) {
                            o.addItem(item);
                        }
                    }

                    // Attach payment
                    allPayments.stream().filter(p -> p.getOrderId() == id).findFirst()
                               .ifPresent(o::setPayment);

                    orders.add(o);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading orders.txt: " + e.getMessage());
        }
        return orders;
    }

    public static synchronized void saveOrders(List<Order> orders) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(ORDERS_FILE, StandardCharsets.UTF_8, false)))) {
            for (Order o : orders) {
                String dateStr = o.getOrderDate() != null ? o.getOrderDate().toString() : new Timestamp(System.currentTimeMillis()).toString();
                writer.println(o.getId() + DELIMITER + 
                               o.getUserId() + DELIMITER + 
                               dateStr + DELIMITER + 
                               o.getTotalAmount() + DELIMITER + 
                               clean(o.getShippingAddress()) + DELIMITER + 
                               clean(o.getStatus()));
            }
        } catch (IOException e) {
            System.err.println("Error writing orders.txt: " + e.getMessage());
        }
    }

    public static synchronized List<OrderItem> loadOrderItems() {
        List<OrderItem> items = new ArrayList<>();
        File file = new File(ORDER_ITEMS_FILE);
        if (!file.exists()) return items;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(DELIMITER, -1);
                if (parts.length >= 6) {
                    items.add(new OrderItem(
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        Integer.parseInt(parts[2].trim()),
                        parts[3].trim(),
                        Integer.parseInt(parts[4].trim()),
                        Double.parseDouble(parts[5].trim())
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading order_items.txt: " + e.getMessage());
        }
        return items;
    }

    public static synchronized void saveOrderItems(List<OrderItem> items) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(ORDER_ITEMS_FILE, StandardCharsets.UTF_8, false)))) {
            for (OrderItem i : items) {
                writer.println(i.getId() + DELIMITER + 
                               i.getOrderId() + DELIMITER + 
                               i.getProductId() + DELIMITER + 
                               clean(i.getProductName()) + DELIMITER + 
                               i.getQuantity() + DELIMITER + 
                               i.getPrice());
            }
        } catch (IOException e) {
            System.err.println("Error writing order_items.txt: " + e.getMessage());
        }
    }

    // ============================================================
    // PAYMENT FILE OPERATIONS
    // ============================================================
    public static synchronized List<Payment> loadPayments() {
        List<Payment> payments = new ArrayList<>();
        File file = new File(PAYMENTS_FILE);
        if (!file.exists()) return payments;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(DELIMITER, -1);
                if (parts.length >= 6) {
                    payments.add(new Payment(
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        parts[2].trim(),
                        parts[3].trim(),
                        parts[4].trim(),
                        Timestamp.valueOf(parts[5].trim())
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading payments.txt: " + e.getMessage());
        }
        return payments;
    }

    public static synchronized void savePayments(List<Payment> payments) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(PAYMENTS_FILE, StandardCharsets.UTF_8, false)))) {
            for (Payment p : payments) {
                String dateStr = p.getPaymentDate() != null ? p.getPaymentDate().toString() : new Timestamp(System.currentTimeMillis()).toString();
                writer.println(p.getId() + DELIMITER + 
                               p.getOrderId() + DELIMITER + 
                               clean(p.getPaymentMethod()) + DELIMITER + 
                               clean(p.getPaymentStatus()) + DELIMITER + 
                               clean(p.getTransactionDetails()) + DELIMITER + 
                               dateStr);
            }
        } catch (IOException e) {
            System.err.println("Error writing payments.txt: " + e.getMessage());
        }
    }

    private static String clean(String text) {
        if (text == null) return "";
        return text.replace("\n", " ").replace("\r", " ").replace(DELIMITER, " ");
    }
}
