package com.onlineshopping.gui;

import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.exception.InvalidLoginException;
import com.onlineshopping.model.User;
import com.onlineshopping.service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern Swing Login Panel.
 */
public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthenticationService authService;

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.authService = new AuthenticationService();

        setLayout(new GridBagLayout());
        setBackground(UIUtils.BG_LIGHT);

        initUI();
    }

    private void initUI() {
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 520));
        card.setMaximumSize(new Dimension(420, 520));

        // Title
        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(UIUtils.FONT_HEADER_LARGE);
        titleLabel.setForeground(UIUtils.TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to continue to Online Shopping");
        subtitleLabel.setFont(UIUtils.FONT_BODY);
        subtitleLabel.setForeground(UIUtils.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form Fields
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(25, 10, 15, 10));

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(UIUtils.FONT_BODY_BOLD);
        emailField = UIUtils.createTextField();
        emailField.setText("john@gmail.com"); // Pre-filled for demo ease

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UIUtils.FONT_BODY_BOLD);
        passwordField = UIUtils.createPasswordField();
        passwordField.setText("customer123");

        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);

        // Sign In Button
        JButton loginBtn = UIUtils.createPrimaryButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(380, 42));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> performLogin());

        // Register Link
        JPanel registerLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerLinkPanel.setOpaque(false);
        JLabel noAccountLabel = new JLabel("Don't have an account? ");
        noAccountLabel.setFont(UIUtils.FONT_BODY);
        JButton registerLinkBtn = new JButton("Register Now");
        registerLinkBtn.setFont(UIUtils.FONT_BODY_BOLD);
        registerLinkBtn.setForeground(UIUtils.PRIMARY_COLOR);
        registerLinkBtn.setBorderPainted(false);
        registerLinkBtn.setContentAreaFilled(false);
        registerLinkBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLinkBtn.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_REGISTER));
        registerLinkPanel.add(noAccountLabel);
        registerLinkPanel.add(registerLinkBtn);

        // Demo Quick Login Shortcuts
        JPanel demoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        demoPanel.setOpaque(false);
        demoPanel.setBorder(BorderFactory.createTitledBorder("Quick Demo Login"));

        JButton demoCustomerBtn = UIUtils.createSecondaryButton("🔑 Quick Customer (john@gmail.com / customer123)");
        demoCustomerBtn.setFont(UIUtils.FONT_SMALL);
        demoCustomerBtn.addActionListener(e -> {
            emailField.setText("john@gmail.com");
            passwordField.setText("customer123");
            performLogin();
        });

        JButton demoAdminBtn = UIUtils.createSecondaryButton("👑 Quick Admin (admin@shopping.com / admin123)");
        demoAdminBtn.setFont(UIUtils.FONT_SMALL);
        demoAdminBtn.addActionListener(e -> {
            emailField.setText("admin@shopping.com");
            passwordField.setText("admin123");
            performLogin();
        });

        demoPanel.add(demoCustomerBtn);
        demoPanel.add(demoAdminBtn);

        // Add to card
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(subtitleLabel);
        card.add(formPanel);
        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(registerLinkPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(demoPanel);

        add(card);
    }

    private void performLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            User user = authService.login(email, password);
            mainFrame.setCurrentUser(user);
        } catch (InvalidLoginException ex) {
            UIUtils.showError(this, ex.getMessage(), "Authentication Error");
        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Database Error");
        }
    }
}
