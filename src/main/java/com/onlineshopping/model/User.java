package com.onlineshopping.model;

/**
 * Abstract Base Class representing a System User.
 * Demonstrates:
 * - Abstract Class
 * - Encapsulation (private fields, getters/setters)
 * - Inheritance Base (Customer & Admin extend this)
 */
public abstract class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String role; // "CUSTOMER" or "ADMIN"

    // Default constructor
    public User() {}

    // Overloaded constructor demonstrating Method Overloading concepts
    public User(int id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Abstract method to be overridden by subclasses (Polymorphism)
    public abstract String getRoleDescription();

    // Getters and Setters (Encapsulation)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', role='" + role + "'}";
    }
}
