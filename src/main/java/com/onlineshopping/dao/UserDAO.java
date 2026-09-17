package com.onlineshopping.dao;

import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.model.Customer;
import com.onlineshopping.model.User;
import com.onlineshopping.util.FileStorageManager;
import com.onlineshopping.util.PasswordUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entities using local File I/O storage.
 */
public class UserDAO {

    public User login(String email, String rawPassword) throws DatabaseException {
        if (email == null || rawPassword == null) return null;

        List<User> users = FileStorageManager.loadUsers();
        String inputHash = PasswordUtil.hashPassword(rawPassword.trim());
        String targetEmail = email.trim();

        for (User u : users) {
            if (u.getEmail() != null && u.getEmail().trim().equalsIgnoreCase(targetEmail)) {
                if (u.getPassword() != null && u.getPassword().trim().equals(inputHash)) {
                    return u;
                }
            }
        }
        return null;
    }

    public boolean registerCustomer(Customer customer, String rawPassword) throws DatabaseException {
        List<User> users = FileStorageManager.loadUsers();

        int maxId = 0;
        for (User u : users) {
            if (u.getId() > maxId) maxId = u.getId();
        }

        customer.setId(maxId + 1);
        customer.setPassword(PasswordUtil.hashPassword(rawPassword.trim()));
        users.add(customer);

        FileStorageManager.saveUsers(users);
        return true;
    }

    public boolean isEmailRegistered(String email) throws DatabaseException {
        if (email == null) return false;
        List<User> users = FileStorageManager.loadUsers();
        String targetEmail = email.trim();

        for (User u : users) {
            if (u.getEmail() != null && u.getEmail().trim().equalsIgnoreCase(targetEmail)) {
                return true;
            }
        }
        return false;
    }

    public List<Customer> getAllCustomers() throws DatabaseException {
        List<Customer> customers = new ArrayList<>();
        List<User> users = FileStorageManager.loadUsers();

        for (User u : users) {
            if (u instanceof Customer) {
                customers.add((Customer) u);
            }
        }
        return customers;
    }

    public int getCustomerCount() throws DatabaseException {
        return getAllCustomers().size();
    }
}
