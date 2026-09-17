package com.onlineshopping.gui;

import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.exception.InsufficientStockException;
import com.onlineshopping.model.CartItem;
import com.onlineshopping.service.CartService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing Customer Shopping Cart Panel.
 */
public class CartPanel extends JPanel {

    private final MainFrame mainFrame;
    private final CustomerDashboard customerDashboard;
    private final CartService cartService;

    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalAmountLabel;
    private JButton checkoutBtn;
    private JButton incBtn;
    private JButton decBtn;
    private JButton removeBtn;
    private JButton clearBtn;

    private List<CartItem> currentCartItems = new ArrayList<>();
    private CartItem selectedItem = null;

    public CartPanel(MainFrame mainFrame, CustomerDashboard customerDashboard) {
        this.mainFrame = mainFrame;
        this.customerDashboard = customerDashboard;
        this.cartService = new CartService();

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIUtils.BG_LIGHT);

        initUI();
    }

    private void initUI() {
        // Table Setup
        String[] columnNames = {"Cart ID", "Product Name", "Category", "Unit Price", "Quantity", "Subtotal"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(tableModel);
        UIUtils.styleTable(cartTable);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        cartTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = cartTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < currentCartItems.size()) {
                    selectedItem = currentCartItems.get(selectedRow);
                    incBtn.setEnabled(true);
                    decBtn.setEnabled(true);
                    removeBtn.setEnabled(true);
                } else {
                    selectedItem = null;
                    incBtn.setEnabled(false);
                    decBtn.setEnabled(false);
                    removeBtn.setEnabled(false);
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(cartTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));

        // Top Action Bar
        JPanel topBar = UIUtils.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        incBtn = UIUtils.createPrimaryButton("➕ Increase Qty");
        incBtn.setPreferredSize(new Dimension(140, 32));
        incBtn.setEnabled(false);
        incBtn.addActionListener(e -> updateQuantity(1));

        decBtn = UIUtils.createSecondaryButton("➖ Decrease Qty");
        decBtn.setPreferredSize(new Dimension(140, 32));
        decBtn.setEnabled(false);
        decBtn.addActionListener(e -> updateQuantity(-1));

        removeBtn = UIUtils.createDangerButton("🗑️ Remove Item");
        removeBtn.setPreferredSize(new Dimension(140, 32));
        removeBtn.setEnabled(false);
        removeBtn.addActionListener(e -> removeItem());

        clearBtn = UIUtils.createDangerButton("❌ Clear Entire Cart");
        clearBtn.setPreferredSize(new Dimension(160, 32));
        clearBtn.addActionListener(e -> clearCart());

        topBar.add(incBtn);
        topBar.add(decBtn);
        topBar.add(removeBtn);
        topBar.add(clearBtn);

        // Bottom Summary Bar
        JPanel bottomBar = UIUtils.createCardPanel();
        bottomBar.setLayout(new BorderLayout(15, 10));

        totalAmountLabel = new JLabel("Grand Total: $0.00");
        totalAmountLabel.setFont(UIUtils.FONT_HEADER_LARGE);
        totalAmountLabel.setForeground(UIUtils.PRIMARY_COLOR);

        checkoutBtn = UIUtils.createSuccessButton("💳 Proceed to Checkout");
        checkoutBtn.setPreferredSize(new Dimension(220, 42));
        checkoutBtn.setFont(UIUtils.FONT_HEADER_MEDIUM);
        checkoutBtn.addActionListener(e -> openCheckoutDialog());

        bottomBar.add(totalAmountLabel, BorderLayout.WEST);
        bottomBar.add(checkoutBtn, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    public void loadCart() {
        if (mainFrame.getCurrentUser() == null) return;
        int userId = mainFrame.getCurrentUser().getId();

        try {
            currentCartItems = cartService.getCartItems(userId);
            tableModel.setRowCount(0);

            double total = 0.0;
            for (CartItem item : currentCartItems) {
                double subtotal = item.getSubtotal();
                total += subtotal;
                tableModel.addRow(new Object[]{
                    item.getId(),
                    item.getProduct().getName(),
                    item.getProduct().getCategory(),
                    String.format("$%.2f", item.getProduct().getPrice()),
                    item.getQuantity(),
                    String.format("$%.2f", subtotal)
                });
            }

            totalAmountLabel.setText(String.format("Grand Total: $%.2f", total));
            checkoutBtn.setEnabled(!currentCartItems.isEmpty());
            clearBtn.setEnabled(!currentCartItems.isEmpty());

            selectedItem = null;
            incBtn.setEnabled(false);
            decBtn.setEnabled(false);
            removeBtn.setEnabled(false);

        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Error Loading Cart");
        }
    }

    private void updateQuantity(int delta) {
        if (selectedItem == null || mainFrame.getCurrentUser() == null) return;
        int newQty = selectedItem.getQuantity() + delta;

        try {
            cartService.updateCartQuantity(mainFrame.getCurrentUser().getId(), selectedItem.getId(), newQty);
            loadCart();
        } catch (InsufficientStockException ex) {
            UIUtils.showError(this, ex.getMessage(), "Stock Limit");
        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Database Error");
        }
    }

    private void removeItem() {
        if (selectedItem == null) return;
        try {
            cartService.removeFromCart(selectedItem.getId());
            loadCart();
        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Database Error");
        }
    }

    private void clearCart() {
        if (mainFrame.getCurrentUser() == null) return;
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to empty your shopping cart?", 
            "Clear Cart", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                cartService.clearCart(mainFrame.getCurrentUser().getId());
                loadCart();
            } catch (DatabaseException ex) {
                UIUtils.showError(this, ex.getMessage(), "Database Error");
            }
        }
    }

    private void openCheckoutDialog() {
        if (currentCartItems.isEmpty()) return;
        CheckoutDialog dialog = new CheckoutDialog(mainFrame, customerDashboard, currentCartItems);
        dialog.setVisible(true);
    }
}
