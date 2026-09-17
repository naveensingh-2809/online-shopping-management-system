package com.onlineshopping.service;

import com.onlineshopping.dao.CartDAO;
import com.onlineshopping.dao.ProductDAO;
import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.exception.InsufficientStockException;
import com.onlineshopping.model.CartItem;
import com.onlineshopping.model.Product;

import java.util.List;

/**
 * Business Service for Customer Cart operations.
 * Demonstrates Collections Framework operations (List processing, streams, total calculations).
 */
public class CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService() {
        this.cartDAO = new CartDAO();
        this.productDAO = new ProductDAO();
    }

    public List<CartItem> getCartItems(int userId) throws DatabaseException {
        return cartDAO.getCartItemsByUser(userId);
    }

    public void addToCart(int userId, Product product, int quantity) throws DatabaseException, InsufficientStockException {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        // Fetch fresh stock status from DB
        Product dbProduct = productDAO.getProductById(product.getId());
        if (dbProduct == null) {
            throw new DatabaseException("Selected product is no longer available.");
        }

        // Calculate existing quantity in cart
        List<CartItem> currentCart = cartDAO.getCartItemsByUser(userId);
        int existingQty = 0;
        for (CartItem item : currentCart) {
            if (item.getProduct().getId() == product.getId()) {
                existingQty = item.getQuantity();
                break;
            }
        }

        int totalRequested = existingQty + quantity;
        if (totalRequested > dbProduct.getStock()) {
            throw new InsufficientStockException("Cannot add " + quantity + " items. Stock available: " + 
                dbProduct.getStock() + " (Already in cart: " + existingQty + ")");
        }

        cartDAO.addToCart(userId, product.getId(), quantity);
    }

    public void updateCartQuantity(int userId, int cartItemId, int newQuantity) throws DatabaseException, InsufficientStockException {
        if (newQuantity <= 0) {
            cartDAO.removeFromCart(cartItemId);
            return;
        }

        // Validate stock
        List<CartItem> currentCart = cartDAO.getCartItemsByUser(userId);
        for (CartItem item : currentCart) {
            if (item.getId() == cartItemId) {
                Product freshProduct = productDAO.getProductById(item.getProduct().getId());
                if (newQuantity > freshProduct.getStock()) {
                    throw new InsufficientStockException("Requested quantity (" + newQuantity + ") exceeds available stock (" + freshProduct.getStock() + ").");
                }
                break;
            }
        }

        cartDAO.updateQuantity(cartItemId, newQuantity);
    }

    public void removeFromCart(int cartItemId) throws DatabaseException {
        cartDAO.removeFromCart(cartItemId);
    }

    public void clearCart(int userId) throws DatabaseException {
        cartDAO.clearCart(userId);
    }

    public double calculateCartTotal(List<CartItem> items) {
        if (items == null || items.isEmpty()) return 0.0;
        return items.stream()
                    .mapToDouble(CartItem::getSubtotal)
                    .sum();
    }
}
