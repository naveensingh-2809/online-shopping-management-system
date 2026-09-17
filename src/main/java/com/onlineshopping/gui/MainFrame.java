package com.onlineshopping.gui;

import com.onlineshopping.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Window Frame containing top header and CardLayout panel.
 * Controls top-level navigation between Login, Register, Customer, and Admin dashboards.
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JLabel userStatusLabel;
    private JButton logoutButton;
    private User currentUser;

    // View names
    public static final String VIEW_LOGIN = "LOGIN";
    public static final String VIEW_REGISTER = "REGISTER";
    public static final String VIEW_CUSTOMER = "CUSTOMER";
    public static final String VIEW_ADMIN = "ADMIN";

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private CustomerDashboard customerDashboard;
    private AdminDashboard adminDashboard;

    public MainFrame() {
        setTitle("Online Shopping Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(950, 650));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Top Navigation Header Bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtils.BG_DARK);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("🛒 ONLINE SHOPPING MANAGEMENT SYSTEM");
        titleLabel.setFont(UIUtils.FONT_HEADER_MEDIUM);
        titleLabel.setForeground(Color.WHITE);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeaderPanel.setOpaque(false);

        userStatusLabel = new JLabel("Not Logged In");
        userStatusLabel.setFont(UIUtils.FONT_BODY);
        userStatusLabel.setForeground(new Color(220, 224, 230));

        logoutButton = UIUtils.createDangerButton("Logout");
        logoutButton.setPreferredSize(new Dimension(90, 32));
        logoutButton.setVisible(false);
        logoutButton.addActionListener(e -> logout());

        rightHeaderPanel.add(userStatusLabel);
        rightHeaderPanel.add(logoutButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Content Container with CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UIUtils.BG_LIGHT);

        // Initialize Child Panels
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        customerDashboard = new CustomerDashboard(this);
        adminDashboard = new AdminDashboard(this);

        mainContentPanel.add(loginPanel, VIEW_LOGIN);
        mainContentPanel.add(registerPanel, VIEW_REGISTER);
        mainContentPanel.add(customerDashboard, VIEW_CUSTOMER);
        mainContentPanel.add(adminDashboard, VIEW_ADMIN);

        add(mainContentPanel, BorderLayout.CENTER);

        // Default start view
        showView(VIEW_LOGIN);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            userStatusLabel.setText("👤 " + user.getName() + " (" + user.getRole() + ")");
            logoutButton.setVisible(true);

            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                adminDashboard.loadDashboardData();
                showView(VIEW_ADMIN);
            } else {
                customerDashboard.loadCustomerData(user);
                showView(VIEW_CUSTOMER);
            }
        } else {
            userStatusLabel.setText("Not Logged In");
            logoutButton.setVisible(false);
            showView(VIEW_LOGIN);
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void showView(String viewName) {
        cardLayout.show(mainContentPanel, viewName);
    }

    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to log out?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            setCurrentUser(null);
            showView(VIEW_LOGIN);
        }
    }
}
