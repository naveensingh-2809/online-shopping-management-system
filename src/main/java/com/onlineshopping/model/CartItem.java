package com.onlineshopping.model;

/**
 * Model representing an item inside a customer's shopping cart.
 */
public class CartItem {
    private int id;
    private int userId;
    private Product product;
    private int quantity;

    public CartItem() {}

    public CartItem(int id, int userId, Product product, int quantity) {
        this.id = id;
        this.userId = userId;
        this.product = product;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Helper method calculating item total (quantity * unit price)
    public double getSubtotal() {
        return product != null ? product.getPrice() * quantity : 0.0;
    }
}
