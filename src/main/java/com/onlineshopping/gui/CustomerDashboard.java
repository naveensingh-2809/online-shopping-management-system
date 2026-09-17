package com.onlineshopping.gui;

import com.onlineshopping.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Customer Dashboard Container Panel hosting Tabbed Pane views.
 */
public class CustomerDashboard extends JPanel {

    private final MainFrame mainFrame;
    private JTabbedPane tabbedPane;

    private ProductPanel productPanel;
    private CartPanel cartPanel;
    private OrderHistoryPanel orderHistoryPanel;

    public CustomerDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_LIGHT);

        initUI();
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_BODY_BOLD);

        productPanel = new ProductPanel(mainFrame, this);
        cartPanel = new CartPanel(mainFrame, this);
        orderHistoryPanel = new OrderHistoryPanel(mainFrame);

        tabbedPane.addTab("🛍️ Browse Products", productPanel);
        tabbedPane.addTab("🛒 Shopping Cart", cartPanel);
        tabbedPane.addTab("📦 Order History", orderHistoryPanel);

        // Add listener to refresh panel data on tab switch
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 0) {
                productPanel.loadProducts();
            } else if (selectedIndex == 1) {
                cartPanel.loadCart();
            } else if (selectedIndex == 2) {
                orderHistoryPanel.loadOrders();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void loadCustomerData(User user) {
        productPanel.loadProducts();
        cartPanel.loadCart();
        orderHistoryPanel.loadOrders();
    }

    public void switchToCartTab() {
        tabbedPane.setSelectedIndex(1);
    }

    public void switchToOrdersTab() {
        tabbedPane.setSelectedIndex(2);
    }
}
