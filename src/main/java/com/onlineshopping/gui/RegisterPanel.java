package com.onlineshopping.gui;

import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.exception.InvalidLoginException;
import com.onlineshopping.service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Swing Customer Registration Panel.
 */
public class RegisterPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthenticationService authService;

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField addressField;
    private JTextField phoneField;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.authService = new AuthenticationService();

        setLayout(new GridBagLayout());
        setBackground(UIUtils.BG_LIGHT);

        initUI();
    }

    private void initUI() {
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(480, 580));
        card.setMaximumSize(new Dimension(480, 580));

        JLabel titleLabel = new JLabel("Create Customer Account");
        titleLabel.setFont(UIUtils.FONT_HEADER_LARGE);
        titleLabel.setForeground(UIUtils.TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Register to start shopping online today");
        subtitleLabel.setFont(UIUtils.FONT_BODY);
        subtitleLabel.setForeground(UIUtils.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        nameField = UIUtils.createTextField();
        emailField = UIUtils.createTextField();
        passwordField = UIUtils.createPasswordField();
        confirmPasswordField = UIUtils.createPasswordField();
        addressField = UIUtils.createTextField();
        phoneField = UIUtils.createTextField();

        formPanel.add(createLabel("Full Name *"));
        formPanel.add(nameField);
        formPanel.add(createLabel("Email Address *"));
        formPanel.add(emailField);
        formPanel.add(createLabel("Password *"));
        formPanel.add(passwordField);
        formPanel.add(createLabel("Confirm Password *"));
        formPanel.add(confirmPasswordField);
        formPanel.add(createLabel("Shipping Address *"));
        formPanel.add(addressField);
        formPanel.add(createLabel("Phone Number *"));
        formPanel.add(phoneField);

        JButton registerBtn = UIUtils.createSuccessButton("Register Account");
        registerBtn.setMaximumSize(new Dimension(400, 42));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> performRegister());

        JButton cancelBtn = UIUtils.createSecondaryButton("Back to Login");
        cancelBtn.setMaximumSize(new Dimension(400, 36));
        cancelBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelBtn.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_LOGIN));

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(subtitleLabel);
        card.add(formPanel);
        card.add(registerBtn);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(cancelBtn);

        add(card);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIUtils.FONT_BODY_BOLD);
        return label;
    }

    private void performRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String address = addressField.getText();
        String phone = phoneField.getText();

        try {
            authService.registerCustomer(name, email, password, confirmPassword, address, phone);
            UIUtils.showInfo(this, "Registration successful! You can now log in.", "Success");
            clearForm();
            mainFrame.showView(MainFrame.VIEW_LOGIN);
        } catch (InvalidLoginException ex) {
            UIUtils.showError(this, ex.getMessage(), "Registration Error");
        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Database Error");
        }
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        addressField.setText("");
        phoneField.setText("");
    }
}
