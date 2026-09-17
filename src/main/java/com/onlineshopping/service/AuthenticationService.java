package com.onlineshopping.service;

import com.onlineshopping.dao.UserDAO;
import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.exception.InvalidLoginException;
import com.onlineshopping.model.Customer;
import com.onlineshopping.model.User;

/**
 * Service handling User Authentication & Registration business logic.
 */
public class AuthenticationService {

    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public User login(String email, String password) throws InvalidLoginException, DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidLoginException("Please enter your email address.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidLoginException("Please enter your password.");
        }

        User user = userDAO.login(email.trim(), password);
        if (user == null) {
            throw new InvalidLoginException("Invalid email or password. Please check your credentials.");
        }
        return user;
    }

    public void registerCustomer(String name, String email, String password, String confirmPassword, String address, String phone) 
            throws InvalidLoginException, DatabaseException {

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidLoginException("Name is required.");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new InvalidLoginException("Please provide a valid email address.");
        }
        if (password == null || password.length() < 6) {
            throw new InvalidLoginException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new InvalidLoginException("Passwords do not match!");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new InvalidLoginException("Shipping address is required.");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new InvalidLoginException("Phone number is required.");
        }

        if (userDAO.isEmailRegistered(email.trim())) {
            throw new InvalidLoginException("An account with email '" + email + "' already exists.");
        }

        Customer customer = new Customer(name.trim(), email.trim(), "", address.trim(), phone.trim());
        boolean success = userDAO.registerCustomer(customer, password);

        if (!success) {
            throw new DatabaseException("Could not complete user registration.");
        }
    }
}
