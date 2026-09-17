package com.onlineshopping.gui;

import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.exception.InvalidProductException;
import com.onlineshopping.model.Product;
import com.onlineshopping.service.ProductService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin Panel for Product CRUD Operations.
 */
public class ProductManagementPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AdminDashboard adminDashboard;
    private final ProductService productService;

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    private List<Product> productList = new ArrayList<>();
    private Product selectedProduct = null;

    public ProductManagementPanel(MainFrame mainFrame, AdminDashboard adminDashboard) {
        this.mainFrame = mainFrame;
        this.adminDashboard = adminDashboard;
        this.productService = new ProductService();

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIUtils.BG_LIGHT);

        initUI();
    }

    private void initUI() {
        String[] columnNames = {"ID", "Product Name", "Category", "Description", "Price", "Stock"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        UIUtils.styleTable(productTable);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(280);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(70);

        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < productList.size()) {
                    selectedProduct = productList.get(selectedRow);
                    editBtn.setEnabled(true);
                    deleteBtn.setEnabled(true);
                } else {
                    selectedProduct = null;
                    editBtn.setEnabled(false);
                    deleteBtn.setEnabled(false);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));

        // Top Control Bar
        JPanel topBar = UIUtils.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        addBtn = UIUtils.createSuccessButton("➕ Add Product");
        addBtn.setPreferredSize(new Dimension(140, 34));
        addBtn.addActionListener(e -> showProductFormDialog(null));

        editBtn = UIUtils.createPrimaryButton("✏️ Edit Selected");
        editBtn.setPreferredSize(new Dimension(140, 34));
        editBtn.setEnabled(false);
        editBtn.addActionListener(e -> {
            if (selectedProduct != null) showProductFormDialog(selectedProduct);
        });

        deleteBtn = UIUtils.createDangerButton("🗑️ Delete Selected");
        deleteBtn.setPreferredSize(new Dimension(150, 34));
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(e -> deleteSelectedProduct());

        JButton refreshBtn = UIUtils.createSecondaryButton("🔄 Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 34));
        refreshBtn.addActionListener(e -> loadProducts());

        topBar.add(addBtn);
        topBar.add(editBtn);
        topBar.add(deleteBtn);
        topBar.add(refreshBtn);

        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadProducts() {
        try {
            productList = productService.getAllProducts();
            tableModel.setRowCount(0);

            for (Product p : productList) {
                tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategory(),
                    p.getDescription(),
                    String.format("$%.2f", p.getPrice()),
                    p.getStock()
                });
            }

            selectedProduct = null;
            editBtn.setEnabled(false);
            deleteBtn.setEnabled(false);

        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Error Loading Products");
        }
    }

    private void showProductFormDialog(Product productToEdit) {
        boolean isEdit = (productToEdit != null);
        String title = isEdit ? "Edit Product #" + productToEdit.getId() : "Add New Product";

        JTextField nameF = UIUtils.createTextField();
        JTextField catF = UIUtils.createTextField();
        JTextField priceF = UIUtils.createTextField();
        JTextField stockF = UIUtils.createTextField();
        JTextArea descF = new JTextArea(4, 20);
        descF.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));

        if (isEdit) {
            nameF.setText(productToEdit.getName());
            catF.setText(productToEdit.getCategory());
            priceF.setText(String.valueOf(productToEdit.getPrice()));
            stockF.setText(String.valueOf(productToEdit.getStock()));
            descF.setText(productToEdit.getDescription());
        } else {
            catF.setText("Electronics");
            priceF.setText("49.99");
            stockF.setText("20");
        }

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        formPanel.add(new JLabel("Product Name *"));
        formPanel.add(nameF);
        formPanel.add(new JLabel("Category *"));
        formPanel.add(catF);
        formPanel.add(new JLabel("Price ($) *"));
        formPanel.add(priceF);
        formPanel.add(new JLabel("Stock Quantity *"));
        formPanel.add(stockF);
        formPanel.add(new JLabel("Description"));
        formPanel.add(new JScrollPane(descF));

        int result = JOptionPane.showConfirmDialog(this, formPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameF.getText();
                String category = catF.getText();
                double price = Double.parseDouble(priceF.getText().trim());
                int stock = Integer.parseInt(stockF.getText().trim());
                String desc = descF.getText();

                if (isEdit) {
                    productService.updateProduct(productToEdit.getId(), name, category, desc, price, stock);
                    UIUtils.showInfo(this, "Product updated successfully!", "Success");
                } else {
                    productService.addProduct(name, category, desc, price, stock);
                    UIUtils.showInfo(this, "Product added successfully!", "Success");
                }

                loadProducts();
                adminDashboard.loadDashboardData();

            } catch (NumberFormatException ex) {
                UIUtils.showError(this, "Please enter valid numeric values for price and stock.", "Input Error");
            } catch (InvalidProductException | DatabaseException ex) {
                UIUtils.showError(this, ex.getMessage(), "Error Saving Product");
            }
        }
    }

    private void deleteSelectedProduct() {
        if (selectedProduct == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete product '" + selectedProduct.getName() + "'?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productService.deleteProduct(selectedProduct.getId());
                UIUtils.showInfo(this, "Product deleted successfully.", "Deleted");
                loadProducts();
                adminDashboard.loadDashboardData();
            } catch (InvalidProductException | DatabaseException ex) {
                UIUtils.showError(this, ex.getMessage(), "Error Deleting Product");
            }
        }
    }
}
