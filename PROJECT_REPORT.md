# ACADEMIC PROJECT REPORT

## STANDALONE ONLINE SHOPPING MANAGEMENT SYSTEM
**Course**: Object Oriented Programming with Java  
**Technology**: Java 17+, Java Swing, Java File I/O, Maven  

---

### 1. Title
**Standalone Online Shopping Management System**

---

### 2. Abstract
The Online Shopping Management System is a standalone desktop-based e-commerce application engineered to demonstrate core and advanced Java Object-Oriented Programming (OOP) principles, local File I/O persistence, graphical user interface design using Java Swing, multithreading, and custom exception handling. The application operates without any external database software (MySQL / XAMPP), relying on Java character streams (`BufferedReader`, `BufferedWriter`, `PrintWriter`, `FileReader`, `FileWriter`) to persist system state in local text files (`data/users.txt`, `data/products.txt`, `data/cart.txt`, `data/orders.txt`). The system supports dual roles: Customer and Administrator.

---

### 3. Introduction
Developing a standalone multi-tier desktop application allows students to understand modular architecture, file persistence mechanisms, and user interface design in standard Java. The project enforces separation of concerns by segregating presentation logic (Swing GUI), business services, data access abstractions (DAOs), and file I/O utilities.

---

### 4. Problem Statement
Many database-backed college projects fail during viva demonstrations due to missing MySQL drivers, server connection errors, port conflicts, or missing XAMPP services. This project eliminates external database dependencies by implementing a zero-configuration, self-contained file persistence engine using standard Java File I/O.

---

### 5. Objectives
- Implement clean Object-Oriented Programming (OOP) principles: Inheritance, Polymorphism, Encapsulation, Abstract Classes, and Interfaces.
- Connect Java Swing presentation layer to local file persistence (`data/` directory).
- Automatically seed default accounts and product catalog if data files do not exist.
- Use Java Collections Framework (`ArrayList`, Stream API) for in-memory data manipulation.
- Implement multithreaded background processing (`Runnable`) for non-blocking receipt generation.
- Ensure data persistence across application restarts using character streams.

---

### 6. Existing System
Traditional console or database-dependent systems suffer from:
- Complex environment setup requirements (MySQL server, configuration files).
- High failure rates during live college demonstrations.
- Lack of visual layout and mouse-driven interactions.

---

### 7. Proposed System
The proposed system features:
- Standalone execution with zero external software requirements.
- Dual-role responsive Swing GUI (Customer & Admin views).
- File persistence using `BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`, and `PrintWriter`.
- Dynamic Polymorphic Payment Strategy (Card, UPI, Cash On Delivery).
- Asynchronous File Receipt Generation via Multithreading.
- Pre-filled Quick Demo accounts for ease of evaluation.

---

### 8. Functional Requirements
- **Customer Modules**: User Registration, Login, Product Search & Category Filter, Detailed View, Add to Cart, Edit Cart Quantities, Remove Items, Checkout, Polymorphic Payment Gateway, Order History & Receipt Inspector.
- **Admin Modules**: Admin Login, Statistics Dashboard (Revenue, Orders, Products, Customers), Add/Update/Delete Products, Order Status Updates, Customer Account View.

---

### 9. Non-Functional Requirements
- **Usability**: Mouse-navigable interface with clear feedback dialogs.
- **Reliability**: Graceful exception handling preventing application crashes.
- **Portability**: Runs on any operating system with Java installed.
- **Zero Configuration**: Automatic directory creation and seed data initialization.

---

### 10. System Architecture
The application follows a clean 4-tier Layered Architecture:
1. **Presentation Layer (`com.onlineshopping.gui`)**: Swing frames, panels, dialogs, custom styling.
2. **Service Layer (`com.onlineshopping.service`)**: Business rules, validations, orchestration.
3. **Data Access Layer (`com.onlineshopping.dao`)**: In-memory filtering and File I/O invocation.
4. **Persistence Tier (`com.onlineshopping.util.FileStorageManager`)**: `data/` text files (`users.txt`, `products.txt`, `cart.txt`, `orders.txt`).

---

### 11. Class Design
- `User` (Abstract Class): Base user class with fields `id`, `name`, `email`, `password`, `role`.
- `Customer` (Extends `User`): Subclass adding `address` and `phone`.
- `Admin` (Extends `User`): Subclass adding `adminLevel`.
- `PaymentMethod` (Interface): Strategy interface with `processPayment()` and `getPaymentDetails()`.
- `CardPayment`, `UPIPayment`, `CashOnDelivery`: Concrete payment implementations demonstrating Polymorphism.
- `Product`, `CartItem`, `Order`, `OrderItem`, `Payment`: Domain entities.
- `FileStorageManager`: Persistence manager using Java File I/O.
- `OrderProcessingThread` (Implements `Runnable`): Asynchronous post-checkout executor.

---

### 12. Persistence Design (File I/O)
Data is saved as delimiter-separated strings (`///`) inside text files:
- `data/users.txt`: User accounts & hashed credentials.
- `data/products.txt`: Product catalog inventory & stock levels.
- `data/cart.txt`: Customer shopping cart items.
- `data/orders.txt`: Customer orders & delivery status.
- `data/order_items.txt`: Order line items.
- `data/payments.txt`: Payment records.

---

### 13. Modules
1. **Authentication Module**: Customer registration and role-based login.
2. **Product Catalog Module**: Searchable, filterable table with real-time stock indicator.
3. **Cart & Checkout Module**: Cart calculations, stock validation, polymorphic payment execution.
4. **Order Processing & Receipt Module**: File persistence transaction, multithreaded receipt generation.
5. **Admin Supervision Module**: Product CRUD management and order status workflow.

---

### 14. Java Concepts Used
1. **OOP Core**: Classes, Objects, Inheritance, Encapsulation, Polymorphism, Abstract Classes, Interfaces.
2. **Exceptions**: Custom checked exceptions, `try-catch-finally`, error propagation.
3. **Collections**: `ArrayList`, stream mapping, lambda expressions.
4. **File I/O**: `File`, `FileReader`, `FileWriter`, `BufferedReader`, `BufferedWriter`, `PrintWriter`.
5. **Multithreading**: `Runnable` thread execution with `SwingUtilities.invokeLater`.

---

### 15. Implementation
Developed using Java 17 standard libraries, Swing UI toolkit, Maven project management, and local File I/O streams.

---

### 16. Screens / GUI Descriptions
- **Welcome / Login Screen**: Card layout form with quick demo login shortcuts.
- **Customer Dashboard**: Tabbed interface containing Product Catalog, Shopping Cart, and Order History.
- **Checkout Dialog**: Modal window with address confirmation and dynamic payment gateway fields.
- **Admin Dashboard**: Stat metrics cards and tabbed management tables for products, orders, and customers.

---

### 17. Testing
Verified through extensive positive and negative test cases including invalid login credentials, duplicate email registration, insufficient stock validation, cart empty check, payment authorization failures, and app restart data persistence.

---

### 18. Results
The system successfully compiles and runs, allowing customers to seamlessly order items while updating local file storage on disk, writing physical receipts to disk, and giving admins complete operational control.

---

### 19. Limitations
- Single-machine local storage suitable for course demonstrations.

---

### 20. Future Scope
- JSON / XML file format serialization.
- Email notification integration via JavaMail API.

---

### 21. Conclusion
The Standalone Online Shopping Management System successfully fulfills all functional and course requirements, demonstrating practical application of Java OOP, Swing, File I/O, Collections, Multithreading, and Custom Exceptions.

---

### 22. References
- Oracle Java Documentation (JDK 17)
- Java Swing Component API Guidelines
- Java I/O Streams Developer Guide (`java.io`)
- Effective Java by Joshua Bloch
