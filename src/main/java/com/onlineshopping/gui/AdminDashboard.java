package com.onlineshopping.gui;

import com.onlineshopping.dao.OrderDAO;
import com.onlineshopping.dao.ProductDAO;
import com.onlineshopping.dao.UserDAO;
import com.onlineshopping.exception.DatabaseException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern Swing Admin Dashboard Container.
 */
public class AdminDashboard extends JPanel {

    private final MainFrame mainFrame;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;
    private final UserDAO userDAO;

    // Stat Cards
    private JLabel totalProductsLabel;
    private JLabel totalCustomersLabel;
    private JLabel totalOrdersLabel;
    private JLabel totalSalesLabel;

    private JTabbedPane tabbedPane;
    private ProductManagementPanel productManagementPanel;
    private OrderManagementPanel orderManagementPanel;
    private CustomerManagementPanel customerManagementPanel;

    public AdminDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
        this.userDAO = new UserDAO();

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIUtils.BG_LIGHT);

        initUI();
    }

    private void initUI() {
        // Top KPI Stats Cards Bar
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);

        statsPanel.add(createStatCard("Total Products", totalProductsLabel = new JLabel("0"), new Color(24, 119, 242)));
        statsPanel.add(createStatCard("Registered Customers", totalCustomersLabel = new JLabel("0"), new Color(102, 16, 242)));
        statsPanel.add(createStatCard("Total Orders", totalOrdersLabel = new JLabel("0"), new Color(255, 193, 7)));
        statsPanel.add(createStatCard("Total Sales Revenue", totalSalesLabel = new JLabel("$0.00"), new Color(40, 167, 69)));

        add(statsPanel, BorderLayout.NORTH);

        // Center Management Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_BODY_BOLD);

        productManagementPanel = new ProductManagementPanel(mainFrame, this);
        orderManagementPanel = new OrderManagementPanel(mainFrame, this);
        customerManagementPanel = new CustomerManagementPanel(mainFrame);

        tabbedPane.addTab("📦 Product Management", productManagementPanel);
        tabbedPane.addTab("📋 Order Management", orderManagementPanel);
        tabbedPane.addTab("👥 Customer Accounts", customerManagementPanel);

        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 0) productManagementPanel.loadProducts();
            else if (selectedIndex == 1) orderManagementPanel.loadOrders();
            else if (selectedIndex == 2) customerManagementPanel.loadCustomers();
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new BorderLayout(5, 5));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIUtils.FONT_BODY_BOLD);
        titleLabel.setForeground(UIUtils.TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accentColor);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void loadDashboardData() {
        try {
            int pCount = productDAO.getProductCount();
            int cCount = userDAO.getCustomerCount();
            int oCount = orderDAO.getOrderCount();
            double sales = orderDAO.getTotalSales();

            totalProductsLabel.setText(String.valueOf(pCount));
            totalCustomersLabel.setText(String.valueOf(cCount));
            totalOrdersLabel.setText(String.valueOf(oCount));
            totalSalesLabel.setText(String.format("$%.2f", sales));

            productManagementPanel.loadProducts();
            orderManagementPanel.loadOrders();
            customerManagementPanel.loadCustomers();

        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Error Loading Admin Dashboard Data");
        }
    }
}
