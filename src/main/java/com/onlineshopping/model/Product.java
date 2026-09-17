package com.onlineshopping.model;

/**
 * Entity class representing a Store Product.
 * Demonstrates Encapsulation.
 */
public class Product {
    private int id;
    private String name;
    private String category;
    private String description;
    private double price;
    private int stock;

    public Product() {}

    public Product(int id, String name, String category, String description, double price, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public Product(String name, String category, String description, double price, int stock) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return name + " (" + category + ") - $" + String.format("%.2f", price);
    }
}
