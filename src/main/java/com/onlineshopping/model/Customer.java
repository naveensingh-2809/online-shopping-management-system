package com.onlineshopping.model;

/**
 * Concrete Customer class extending User.
 * Demonstrates Inheritance & Method Overriding.
 */
public class Customer extends User {
    private String address;
    private String phone;

    public Customer() {
        super();
        setRole("CUSTOMER");
    }

    public Customer(int id, String name, String email, String password, String address, String phone) {
        super(id, name, email, password, "CUSTOMER");
        this.address = address;
        this.phone = phone;
    }

    public Customer(String name, String email, String password, String address, String phone) {
        super(name, email, password, "CUSTOMER");
        this.address = address;
        this.phone = phone;
    }

    @Override
    public String getRoleDescription() {
        return "Customer: Can browse products, manage cart, place orders, and view order history.";
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
