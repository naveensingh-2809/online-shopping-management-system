package com.onlineshopping.dao;

import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.model.CartItem;
import com.onlineshopping.model.Product;
import com.onlineshopping.util.FileStorageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Shopping Cart using local File I/O storage.
 */
public class CartDAO {

    public List<CartItem> getCartItemsByUser(int userId) throws DatabaseException {
        List<CartItem> allItems = FileStorageManager.loadCartItems();
        List<CartItem> userItems = new ArrayList<>();

        for (CartItem item : allItems) {
            if (item.getUserId() == userId) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    public boolean addToCart(int userId, int productId, int quantity) throws DatabaseException {
        List<CartItem> allItems = FileStorageManager.loadCartItems();

        // Check if item already exists in cart for this user
        for (CartItem item : allItems) {
            if (item.getUserId() == userId && item.getProduct().getId() == productId) {
                item.setQuantity(item.getQuantity() + quantity);
                FileStorageManager.saveCartItems(allItems);
                return true;
            }
        }

        // Otherwise insert new cart item
        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(productId);
        if (product == null) return false;

        int maxId = 0;
        for (CartItem item : allItems) {
            if (item.getId() > maxId) maxId = item.getId();
        }

        CartItem newItem = new CartItem(maxId + 1, userId, product, quantity);
        allItems.add(newItem);

        FileStorageManager.saveCartItems(allItems);
        return true;
    }

    public boolean updateQuantity(int cartItemId, int newQuantity) throws DatabaseException {
        List<CartItem> allItems = FileStorageManager.loadCartItems();

        if (newQuantity <= 0) {
            return removeFromCart(cartItemId);
        }

        boolean updated = false;
        for (CartItem item : allItems) {
            if (item.getId() == cartItemId) {
                item.setQuantity(newQuantity);
                updated = true;
                break;
            }
        }

        if (updated) {
            FileStorageManager.saveCartItems(allItems);
        }
        return updated;
    }

    public boolean removeFromCart(int cartItemId) throws DatabaseException {
        List<CartItem> allItems = FileStorageManager.loadCartItems();
        boolean removed = allItems.removeIf(item -> item.getId() == cartItemId);

        if (removed) {
            FileStorageManager.saveCartItems(allItems);
        }
        return removed;
    }

    public boolean clearCart(int userId) throws DatabaseException {
        List<CartItem> allItems = FileStorageManager.loadCartItems();
        boolean removed = allItems.removeIf(item -> item.getUserId() == userId);

        if (removed) {
            FileStorageManager.saveCartItems(allItems);
        }
        return true;
    }
}
