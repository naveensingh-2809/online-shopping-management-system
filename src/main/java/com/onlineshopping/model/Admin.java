package com.onlineshopping.model;

/**
 * Concrete Admin class extending User.
 * Demonstrates Inheritance & Method Overriding.
 */
public class Admin extends User {
    private String adminLevel; // e.g., "SUPER_ADMIN", "MANAGER"

    public Admin() {
        super();
        setRole("ADMIN");
        this.adminLevel = "SYSTEM_ADMIN";
    }

    public Admin(int id, String name, String email, String password, String adminLevel) {
        super(id, name, email, password, "ADMIN");
        this.adminLevel = adminLevel;
    }

    @Override
    public String getRoleDescription() {
        return "Admin: Full management permissions for products, orders, and customer accounts.";
    }

    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
}
