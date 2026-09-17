package com.onlineshopping.gui;

import com.onlineshopping.dao.UserDAO;
import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.model.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin Panel for viewing registered customer accounts.
 */
public class CustomerManagementPanel extends JPanel {

    private final MainFrame mainFrame;
    private final UserDAO userDAO;

    private JTable customerTable;
    private DefaultTableModel tableModel;

    public CustomerManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userDAO = new UserDAO();

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIUtils.BG_LIGHT);

        initUI();
    }

    private void initUI() {
        String[] columnNames = {"Customer ID", "Full Name", "Email Address", "Phone Number", "Shipping Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        UIUtils.styleTable(customerTable);
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(280);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));

        // Top Bar
        JPanel topBar = UIUtils.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton refreshBtn = UIUtils.createSecondaryButton("🔄 Refresh Customers List");
        refreshBtn.setPreferredSize(new Dimension(200, 32));
        refreshBtn.addActionListener(e -> loadCustomers());

        topBar.add(refreshBtn);

        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadCustomers() {
        try {
            List<Customer> customers = userDAO.getAllCustomers();
            tableModel.setRowCount(0);

            for (Customer c : customers) {
                tableModel.addRow(new Object[]{
                    "#" + c.getId(),
                    c.getName(),
                    c.getEmail(),
                    c.getPhone() != null ? c.getPhone() : "-",
                    c.getAddress() != null ? c.getAddress() : "-"
                });
            }
        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Error Loading Customers");
        }
    }
}
