# ACADEMIC PROJECT REPORT

## STANDALONE ONLINE SHOPPING MANAGEMENT SYSTEM

Course: Object Oriented Programming with Java

Technology: Java 17+, Java Swing, Java File I/O, Maven

---

### 1. Title

Standalone Online Shopping Management System

---

### 2. Abstract

The Online Shopping Management System is a standalone desktop application which showcases Java Object Oriented Programming fundamentals, local File I/O storage, graphical interface development with Java Swing, multithreading, and custom exception handling. This application requires no database drivers or external services (MySQL / XAMPP), relying instead on Java character streams such as `BufferedReader`, `BufferedWriter`, `PrintWriter`, `FileReader`, `FileWriter` to store the system state in local text files (`data/users.txt`, `data/products.txt`, `data/cart.txt`, `data/orders.txt`). The system supports two modes of operation: Customer and Administrator.

---

### 3. Introduction

To develop a standalone multi-tier desktop application allowing students to understand modular system components, file persistence techniques, and standard Java GUI development.

---

### 4. Problem Statement

Many database-driven college projects often fail during viva demonstrations due to missing MySQL drivers, connection errors, port conflicts, or XAMPP service requirements. This project eliminates these external requirements by implementing a self-contained file persistence engine using standard Java File I/O.

---

### 5. Objectives

- Implement Clean Object Oriented Programming fundamentals such as Inheritance, Polymorphism, Encapsulation, Abstract Classes, Interfaces.

- Build Java Swing presentation tier with connections to local file storage (`data/` directory).

- Auto-generate default accounts and product listings if data files do not exist.

- Use Java Collections framework (`ArrayList`, Stream API) for in-memory data processing.

- Utilize multithreaded background processing (`Runnable`) for receipt generation.

- Ensure data persistence across application restarts using character streams.

---

### 6. Existing System

Traditional console or database-based systems suffer from complex setup requirements, high failure rates during college demonstrations, and lack of visual appeal.

---

### 7. Proposed System

The proposed system is a standalone application with dual-mode Swing GUI (Customer & Admin) and file persistence using `BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`, `PrintWriter` with supporting features like Polymorphic Payment Gateway (Card, UPI, COD), File Receipt Generation using Multithreading, and pre-filled Quick Demo accounts.

---

### 8. Functional Requirements

- Customer Features: User registration, login, product search/category filter, detailed view, add to cart, edit cart quantities, remove items, checkout, polymorphic payment gateway, order history/receipt inspector.

- Admin Features: Admin login, statistics dashboard (revenue, orders, products, customers), add/update/delete products, order status updates, customer account views.

---

### 9. Non-Functional Requirements

- Usability: Mouse-driven interface with dialog confirmation

- Reliability: Exception handling that prevents application crashes

- Portability: Cross-platform operation on any OS with Java installed

- Zero Configuration: Automatic directory creation and seed data initialization

---

### 10. System Architecture

The application utilizes a 4-tier Layered Architecture:

1. Presentation Layer (`com.onlineshopping.gui`): Contains Swing frames, panels, dialogs, and custom styling

2. Service Layer (`com.onlineshopping.service`): Handles business rules and orchestration

3. Data Access Layer (`com.onlineshopping.dao`): Implements in-memory filtering and File I/O

4. Persistence Tier (`com.onlineshopping.util.FileStorageManager`): Manages `data/` directory text files (`users.txt`, `products.txt`, `cart.txt`, `orders.txt`)

---

### 11. Class Design

- `User` (Abstract Class): Contains fields `id`, `name`, `email`, `password`, `role`

- `Customer` (Extends `User`): Adds `address` and `phone` fields

- `Admin` (Extends `User`): Contains `adminLevel` field

- `PaymentMethod` (Interface): Strategy interface containing `processPayment()` and `getPaymentDetails()`

- `CardPayment`, `UPIPayment`, `CashOnDelivery` (Concrete Classes): Implements Polymorphism

- `Product`, `CartItem`, `Order`, `OrderItem`, `Payment` (Domain Entities)

- `FileStorageManager` (Persistence Manager)

- `OrderProcessingThread` (Implements `Runnable`): Asynchronous order processing thread

---

### 12. Persistence Design (File I/O)

Data is stored as delimiter-separated strings (`///`) within text files:

- `data/users.txt`: User accounts and hashed credentials

- `data/products.txt`: Product catalog and inventory

- `data/cart.txt`: Customer shopping cart items

- `data/orders.txt`: Customer orders and delivery status

- `data/order_items.txt`: Order line items

- `data/payments.txt`: Payment records

---

### 13. Modules

1. Authentication Module: Customer registration and login system

2. Product Catalog Module: Searchable table with real-time stock indicator

3. Cart & Checkout Module: Cart calculations, stock validation, polymorphic payment

4. Order Processing Module: File persistence transaction and multithreaded receipt generation

5. Admin Supervision Module: Product CRUD and order status management

---

### 14. Java Concepts Used

1. OOP Fundamentals: Classes, Objects, Inheritance, Encapsulation, Polymorphism, Abstract Classes, Interfaces

2. Exceptions: Custom checked exceptions, `try-catch-finally`

3. Collections: `ArrayList`, stream mapping, lambda expressions

4. File I/O: `File`, `FileReader`, `FileWriter`, `BufferedReader`, `BufferedWriter`, `PrintWriter`

5. Multithreading: `Runnable` thread with `SwingUtilities.invokeLater`

---

### 15. Implementation

Built using Java 17 standard libraries, Swing UI toolkit, Maven project management, and local File I/O streams.

---

### 16. Screens / GUI Descriptions

- Welcome / Login Screen: Card layout form with quick demo login shortcuts

- Customer Dashboard: Tabbed interface for Product Catalog, Shopping Cart, Order History

- Checkout Dialog: Modal window with address confirmation and dynamic payment fields

- Admin Dashboard: Stat metrics cards and tabbed management tables

---

### 17. Testing

Verified through extensive positive and negative test cases including invalid login credentials, duplicate email registration, insufficient stock validation, cart empty check, payment authorization failures, and app restart data persistence.

---

### 18. Results

The system successfully compiles and runs allowing customers to seamlessly order items while updating local file storage, writing physical receipts to disk, and giving admins operational control.

---

### 19. Limitations

- Single-machine local storage appropriate for course demonstrations

---

### 20. Future Scope

- JSON / XML file format serialization

- Email notification integration via JavaMail API

---

### 21. Conclusion

The Standalone Online Shopping Management System project successfully fulfills all functional and course requirements, showcasing practical application of Java OOP, Swing, File I/O, Collections, Multithreading, and Custom Exceptions.

---

### 22. References

- Oracle Java Documentation (JDK 17)

- Java Swing Component API Guidelines

- Java I/O Streams Developer Guide (`java.io`)

- Effective Java by Joshua Bloch
