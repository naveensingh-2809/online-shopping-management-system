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
 * Swing Customer Order History Panel.
 * Demonstrates reading receipt files using File I/O.
 */
public class OrderHistoryPanel extends JPanel {

    private final MainFrame mainFrame;
    private final OrderService orderService;

    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton viewReceiptBtn;

    private List<Order> currentOrders = new ArrayList<>();
    private Order selectedOrder = null;

    public OrderHistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.orderService = new OrderService();

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIUtils.BG_LIGHT);

        initUI();
    }

    private void initUI() {
        String[] columnNames = {"Order ID", "Date", "Total Amount", "Status", "Shipping Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(tableModel);
        UIUtils.styleTable(orderTable);
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(300);

        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < currentOrders.size()) {
                    selectedOrder = currentOrders.get(selectedRow);
                    viewReceiptBtn.setEnabled(true);
                } else {
                    selectedOrder = null;
                    viewReceiptBtn.setEnabled(false);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));

        // Top Action Bar
        JPanel topBar = UIUtils.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        viewReceiptBtn = UIUtils.createPrimaryButton("📄 View & Print Text Receipt");
        viewReceiptBtn.setPreferredSize(new Dimension(220, 34));
        viewReceiptBtn.setEnabled(false);
        viewReceiptBtn.addActionListener(e -> displayReceiptModal());

        JButton refreshBtn = UIUtils.createSecondaryButton("🔄 Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 34));
        refreshBtn.addActionListener(e -> loadOrders());

        topBar.add(viewReceiptBtn);
        topBar.add(refreshBtn);

        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadOrders() {
        if (mainFrame.getCurrentUser() == null) return;

        try {
            currentOrders = orderService.getCustomerOrders(mainFrame.getCurrentUser().getId());
            tableModel.setRowCount(0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Order o : currentOrders) {
                String dateStr = o.getOrderDate() != null ? sdf.format(o.getOrderDate()) : "N/A";
                tableModel.addRow(new Object[]{
                    "#" + o.getId(),
                    dateStr,
                    String.format("$%.2f", o.getTotalAmount()),
                    o.getStatus(),
                    o.getShippingAddress()
                });
            }

            selectedOrder = null;
            viewReceiptBtn.setEnabled(false);

        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Error Loading Orders");
        }
    }

    private void displayReceiptModal() {
        if (selectedOrder == null) return;

        try {
            String receiptText = FileManager.readReceipt(selectedOrder.getId());

            JTextArea receiptArea = new JTextArea(receiptText, 22, 50);
            receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            receiptArea.setEditable(false);
            receiptArea.setCaretPosition(0);

            JScrollPane scrollPane = new JScrollPane(receiptArea);
            scrollPane.setPreferredSize(new Dimension(520, 420));

            JOptionPane.showMessageDialog(this, scrollPane, "Official Order Receipt #" + selectedOrder.getId(), JOptionPane.PLAIN_MESSAGE);

        } catch (IOException ex) {
            UIUtils.showError(this, "Could not read receipt file: " + ex.getMessage(), "File I/O Error");
        }
    }
}
