package com.onlineshopping.gui;

import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.model.Order;
import com.onlineshopping.service.OrderService;
import com.onlineshopping.util.FileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin Panel for viewing customer orders and updating status.
 */
public class OrderManagementPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AdminDashboard adminDashboard;
    private final OrderService orderService;

    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusComboBox;
    private JButton updateStatusBtn;
    private JButton viewReceiptBtn;

    private List<Order> allOrders = new ArrayList<>();
    private Order selectedOrder = null;

    public OrderManagementPanel(MainFrame mainFrame, AdminDashboard adminDashboard) {
        this.mainFrame = mainFrame;
        this.adminDashboard = adminDashboard;
        this.orderService = new OrderService();

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIUtils.BG_LIGHT);

        initUI();
    }

    private void initUI() {
        String[] columnNames = {"Order ID", "Customer", "Date", "Total Amount", "Status", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(tableModel);
        UIUtils.styleTable(orderTable);
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(260);

        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < allOrders.size()) {
                    selectedOrder = allOrders.get(selectedRow);
                    statusComboBox.setSelectedItem(selectedOrder.getStatus());
                    statusComboBox.setEnabled(true);
                    updateStatusBtn.setEnabled(true);
                    viewReceiptBtn.setEnabled(true);
                } else {
                    selectedOrder = null;
                    statusComboBox.setEnabled(false);
                    updateStatusBtn.setEnabled(false);
                    viewReceiptBtn.setEnabled(false);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));

        // Top Control Bar
        JPanel topBar = UIUtils.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel changeStatusLabel = new JLabel("Update Order Status:");
        changeStatusLabel.setFont(UIUtils.FONT_BODY_BOLD);

        statusComboBox = new JComboBox<>(new String[]{"PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"});
        statusComboBox.setFont(UIUtils.FONT_BODY);
        statusComboBox.setPreferredSize(new Dimension(140, 32));
        statusComboBox.setEnabled(false);

        updateStatusBtn = UIUtils.createPrimaryButton("💾 Save Status");
        updateStatusBtn.setPreferredSize(new Dimension(130, 32));
        updateStatusBtn.setEnabled(false);
        updateStatusBtn.addActionListener(e -> updateStatus());

        viewReceiptBtn = UIUtils.createSecondaryButton("📄 View Receipt");
        viewReceiptBtn.setPreferredSize(new Dimension(130, 32));
        viewReceiptBtn.setEnabled(false);
        viewReceiptBtn.addActionListener(e -> viewReceipt());

        JButton refreshBtn = UIUtils.createSecondaryButton("🔄 Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 32));
        refreshBtn.addActionListener(e -> loadOrders());

        topBar.add(changeStatusLabel);
        topBar.add(statusComboBox);
        topBar.add(updateStatusBtn);
        topBar.add(viewReceiptBtn);
        topBar.add(refreshBtn);

        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadOrders() {
        try {
            allOrders = orderService.getAllOrders();
            tableModel.setRowCount(0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Order o : allOrders) {
                String dateStr = o.getOrderDate() != null ? sdf.format(o.getOrderDate()) : "N/A";
                tableModel.addRow(new Object[]{
                    "#" + o.getId(),
                    o.getCustomerName() != null ? o.getCustomerName() : "User #" + o.getUserId(),
                    dateStr,
                    String.format("$%.2f", o.getTotalAmount()),
                    o.getStatus(),
                    o.getShippingAddress()
                });
            }

            selectedOrder = null;
            statusComboBox.setEnabled(false);
            updateStatusBtn.setEnabled(false);
            viewReceiptBtn.setEnabled(false);

        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Error Loading Orders");
        }
    }

    private void updateStatus() {
        if (selectedOrder == null) return;
        String newStatus = (String) statusComboBox.getSelectedItem();

        try {
            orderService.updateOrderStatus(selectedOrder.getId(), newStatus);
            UIUtils.showInfo(this, "Order #" + selectedOrder.getId() + " status updated to '" + newStatus + "'", "Status Updated");
            loadOrders();
            adminDashboard.loadDashboardData();
        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Database Error");
        }
    }

    private void viewReceipt() {
        if (selectedOrder == null) return;
        try {
            String receiptText = FileManager.readReceipt(selectedOrder.getId());

            JTextArea receiptArea = new JTextArea(receiptText, 22, 50);
            receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            receiptArea.setEditable(false);
            receiptArea.setCaretPosition(0);

            JScrollPane scrollPane = new JScrollPane(receiptArea);
            scrollPane.setPreferredSize(new Dimension(520, 420));

            JOptionPane.showMessageDialog(this, scrollPane, "Official Receipt - Order #" + selectedOrder.getId(), JOptionPane.PLAIN_MESSAGE);

        } catch (IOException ex) {
            UIUtils.showError(this, "Could not load receipt: " + ex.getMessage(), "File I/O Error");
        }
    }
}
