# Online Shopping Management System

A standalone **Java Swing + Local File I/O** desktop application built for the **Java Programming** course domain.

---

## 📌 Project Overview
The **Online Shopping Management System** is a standalone, multi-role desktop application designed to run on any machine without requiring MySQL, XAMPP, or external database software. Data persistence is handled directly through local Java File I/O text files inside the `data/` directory (`users.txt`, `products.txt`, `cart.txt`, `orders.txt`, `order_items.txt`, `payments.txt`).

The system supports two distinct user roles:
1. **Customer**: Account registration, login, product browsing, keyword search, category filtering, persistent cart management (add, update quantity, remove), checkout with polymorphic payment gateways (Credit/Debit Card, UPI, Cash on Delivery), order history inspection, and background text receipt generation.
2. **Admin**: Platform metrics dashboard (Total Sales Revenue, Orders, Products, Customers), complete product catalog management (Add, Update, Delete), customer order status management (Pending, Processing, Shipped, Delivered, Cancelled), and registered customer account inspection.

---

## 🛠️ Technology Stack & Architecture
- **Language**: Java 17+
- **GUI Toolkit**: Java Swing (`JFrame`, `CardLayout`, `JTable`, `JSplitPane`, `JTabbedPane`, `JOptionPane`)
- **Build Tool**: Apache Maven
- **Persistence Layer**: Local Java File I/O (`File`, `FileReader`, `FileWriter`, `BufferedReader`, `BufferedWriter`, `PrintWriter`)
- **External Database Required**: **NONE** (100% Standalone)
- **Architecture**: Layered Architecture (`Model`, `DAO`, `Service`, `GUI`, `Payment`, `Thread`, `Util`, `Exception`)

---

## 🎓 Java Course Concepts Demonstrated

| Concept | Implementation in Code |
| :--- | :--- |
| **Java Classes & Objects** | Domain models (`Product`, `Order`, `CartItem`, `Payment`, `User`) encapsulating application state. |
| **Constructors** | Default and overloaded constructors across model and UI classes for flexible instantiation. |
| **Encapsulation** | `private` instance fields with public getter/setter methods and field validation checks. |
| **Inheritance** | Abstract base class `User` extended by concrete subclasses `Customer` and `Admin`. |
| **Method Overloading** | Overloaded search methods in `ProductDAO.searchProducts(keyword)` vs `ProductDAO.searchProducts(keyword, category)`. |
| **Method Overriding** | Subclasses overriding `getRoleDescription()` in `Customer` and `Admin`, `toString()` implementations. |
| **Polymorphism** | Dynamic Payment Strategy via `PaymentMethod` interface implemented by `CardPayment`, `UPIPayment`, and `CashOnDelivery`. |
| **Abstract Classes** | `abstract class User` declaring abstract method `getRoleDescription()`. |
| **Interfaces** | `PaymentMethod` interface defining `processPayment(double amount)`, `getPaymentDetails()`, and `getMethodName()`. |
| **Exception Handling** | Custom checked exceptions (`InvalidLoginException`, `InsufficientStockException`, `EmptyCartException`, `InvalidProductException`, `DatabaseException`) handled via `try-catch-finally`. |
| **Multithreading** | `OrderProcessingThread` implementing `Runnable` to generate text receipt files asynchronously without freezing the Swing Event Dispatch Thread (EDT). |
| **Collections Framework** | Extensive use of `ArrayList<Product>`, `ArrayList<CartItem>`, `ArrayList<Order>`, and Stream API for calculations. |
| **File I/O Streams** | `FileStorageManager` and `FileManager` using `BufferedWriter`, `PrintWriter`, `FileReader`, and `BufferedReader` for file storage & receipt generation in `data/` and `receipts/`. |

---

## 🔑 Default Evaluation Credentials

On first run, the system automatically creates the `data/` folder and populates default accounts and 11 sample products across 5 categories:

### Admin Credentials:
- **Email**: `admin@shopping.com`
- **Password**: `admin123`

### Customer Credentials:
- **Email**: `john@gmail.com`
- **Password**: `customer123`

*(The login screen also features quick-login demo buttons for instant one-click authentication).*

---

## 🚀 How to Build and Run

### Option 1: Run from VS Code / IDE
1. Open the project directory in VS Code, Eclipse, or IntelliJ IDEA.
2. Run `src/main/java/com/onlineshopping/Main.java`.

### Option 2: Run via Command Line
```cmd
cd online-shopping-system
javac -d target/classes (Get-ChildItem -Recurse -Filter "*.java" src\main\java).FullName
java -cp target/classes com.onlineshopping.Main
```

### Option 3: Run via Maven
```cmd
mvn clean package
java -jar target/online-shopping-system-1.0-SNAPSHOT.jar
```

---

## 📂 Local Data Persistence (`data/` & `receipts/`)

All changes persist automatically across application restarts in local text files:
- `data/users.txt` — User accounts & SHA-256 hashed credentials
- `data/products.txt` — Product inventory catalog & stock levels
- `data/cart.txt` — Persistent customer shopping cart items
- `data/orders.txt` — Customer orders & delivery status
- `data/order_items.txt` — Line items for placed orders
- `data/payments.txt` — Order payment transactions
- `receipts/order_<id>.txt` — Formatted order text receipts generated by background threads

---

## 📁 Project Directory Structure
```
online-shopping-system/
├── pom.xml
├── README.md
├── PROJECT_REPORT.md
├── VIVA_QUESTIONS.md
├── TEST_CASES.md
├── data/
│   ├── users.txt
│   ├── products.txt
│   ├── cart.txt
│   ├── orders.txt
│   ├── order_items.txt
│   └── payments.txt
├── receipts/
└── src/
    └── main/
        └── java/
            └── com/
                └── onlineshopping/
                    ├── Main.java
                    ├── model/
                    │   ├── User.java (Abstract)
                    │   ├── Customer.java
                    │   ├── Admin.java
                    │   ├── Product.java
                    │   ├── CartItem.java
                    │   ├── Order.java
                    │   ├── OrderItem.java
                    │   └── Payment.java
                    ├── payment/
                    │   ├── PaymentMethod.java (Interface)
                    │   ├── CardPayment.java
                    │   ├── UPIPayment.java
                    │   └── CashOnDelivery.java
                    ├── dao/
                    │   ├── UserDAO.java
                    │   ├── ProductDAO.java
                    │   ├── CartDAO.java
                    │   ├── OrderDAO.java
                    │   └── PaymentDAO.java
                    ├── service/
                    │   ├── AuthenticationService.java
                    │   ├── ProductService.java
                    │   ├── CartService.java
                    │   └── OrderService.java
                    ├── exception/
                    │   ├── InvalidLoginException.java
                    │   ├── InsufficientStockException.java
                    │   ├── EmptyCartException.java
                    │   ├── InvalidProductException.java
                    │   └── DatabaseException.java
                    ├── util/
                    │   ├── FileStorageManager.java
                    │   ├── PasswordUtil.java
                    │   └── FileManager.java
                    ├── thread/
                    │   └── OrderProcessingThread.java
                    └── gui/
                        ├── UIUtils.java
                        ├── MainFrame.java
                        ├── LoginPanel.java
                        ├── RegisterPanel.java
                        ├── CustomerDashboard.java
                        ├── ProductPanel.java
                        ├── CartPanel.java
                        ├── CheckoutDialog.java
                        ├── OrderHistoryPanel.java
                        ├── AdminDashboard.java
                        ├── ProductManagementPanel.java
                        ├── OrderManagementPanel.java
                        └── CustomerManagementPanel.java
```
