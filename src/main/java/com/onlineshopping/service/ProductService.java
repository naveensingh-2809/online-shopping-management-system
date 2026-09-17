package com.onlineshopping.service;

import com.onlineshopping.dao.ProductDAO;
import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.exception.InvalidProductException;
import com.onlineshopping.model.Product;

import java.util.List;

/**
 * Business Service layer for Product catalog management.
 */
public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public List<Product> getAllProducts() throws DatabaseException {
        return productDAO.getAllProducts();
    }

    public List<Product> searchProducts(String keyword, String category) throws DatabaseException {
        return productDAO.searchProducts(keyword, category);
    }

    public List<String> getAllCategories() throws DatabaseException {
        return productDAO.getAllCategories();
    }

    public void addProduct(String name, String category, String description, double price, int stock) 
            throws InvalidProductException, DatabaseException {

        validateProductData(name, category, price, stock);
        Product product = new Product(name.trim(), category.trim(), description.trim(), price, stock);
        productDAO.addProduct(product);
    }

    public void updateProduct(int id, String name, String category, String description, double price, int stock) 
            throws InvalidProductException, DatabaseException {

        if (id <= 0) {
            throw new InvalidProductException("Invalid Product ID.");
        }
        validateProductData(name, category, price, stock);
        Product product = new Product(id, name.trim(), category.trim(), description.trim(), price, stock);
        productDAO.updateProduct(product);
    }

    public void deleteProduct(int productId) throws InvalidProductException, DatabaseException {
        if (productId <= 0) {
            throw new InvalidProductException("Invalid Product ID selected.");
        }
        productDAO.deleteProduct(productId);
    }

    private void validateProductData(String name, String category, double price, int stock) throws InvalidProductException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductException("Product name cannot be empty.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new InvalidProductException("Category cannot be empty.");
        }
        if (price < 0.0) {
            throw new InvalidProductException("Price cannot be negative.");
        }
        if (stock < 0) {
            throw new InvalidProductException("Stock quantity cannot be negative.");
        }
    }
}
