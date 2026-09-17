package com.onlineshopping.dao;

import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.model.Product;
import com.onlineshopping.util.FileStorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for Products using local File I/O storage.
 * Demonstrates:
 * - Method Overloading (searchProducts overloaded methods)
 * - Collections Framework (ArrayList, Stream API)
 * - File I/O persistence
 */
public class ProductDAO {

    public List<Product> getAllProducts() throws DatabaseException {
        return FileStorageManager.loadProducts();
    }

    public Product getProductById(int productId) throws DatabaseException {
        List<Product> products = FileStorageManager.loadProducts();
        for (Product p : products) {
            if (p.getId() == productId) {
                return p;
            }
        }
        return null;
    }

    // Demonstrates Method Overloading (Search by Keyword)
    public List<Product> searchProducts(String keyword) throws DatabaseException {
        return searchProducts(keyword, "All");
    }

    // Demonstrates Method Overloading (Search by Keyword and Category)
    public List<Product> searchProducts(String keyword, String category) throws DatabaseException {
        List<Product> allProducts = FileStorageManager.loadProducts();
        List<Product> filtered = new ArrayList<>();

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty() && !"All".equalsIgnoreCase(category);

        String kwLower = hasKeyword ? keyword.trim().toLowerCase() : "";
        String catLower = hasCategory ? category.trim().toLowerCase() : "";

        for (Product p : allProducts) {
            boolean matchesKeyword = !hasKeyword || 
                (p.getName() != null && p.getName().toLowerCase().contains(kwLower)) ||
                (p.getDescription() != null && p.getDescription().toLowerCase().contains(kwLower));

            boolean matchesCategory = !hasCategory || 
                (p.getCategory() != null && p.getCategory().equalsIgnoreCase(catLower));

            if (matchesKeyword && matchesCategory) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    public List<String> getAllCategories() throws DatabaseException {
        List<Product> products = FileStorageManager.loadProducts();
        List<String> categories = new ArrayList<>();
        categories.add("All");

        for (Product p : products) {
            if (p.getCategory() != null && !categories.contains(p.getCategory())) {
                categories.add(p.getCategory());
            }
        }
        return categories;
    }

    public boolean addProduct(Product product) throws DatabaseException {
        List<Product> products = FileStorageManager.loadProducts();
        int maxId = 0;
        for (Product p : products) {
            if (p.getId() > maxId) maxId = p.getId();
        }
        product.setId(maxId + 1);
        products.add(product);
        FileStorageManager.saveProducts(products);
        return true;
    }

    public boolean updateProduct(Product product) throws DatabaseException {
        List<Product> products = FileStorageManager.loadProducts();
        boolean updated = false;

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                updated = true;
                break;
            }
        }

        if (updated) {
            FileStorageManager.saveProducts(products);
        }
        return updated;
    }

    public boolean deleteProduct(int productId) throws DatabaseException {
        List<Product> products = FileStorageManager.loadProducts();
        boolean removed = products.removeIf(p -> p.getId() == productId);

        if (removed) {
            FileStorageManager.saveProducts(products);
        }
        return removed;
    }

    public int getProductCount() throws DatabaseException {
        return getAllProducts().size();
    }
}
