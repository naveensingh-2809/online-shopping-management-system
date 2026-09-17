package com.onlineshopping.gui;

import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.exception.InsufficientStockException;
import com.onlineshopping.model.Product;
import com.onlineshopping.service.CartService;
import com.onlineshopping.service.ProductService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing Customer Product Browsing & Search Panel.
 */
public class ProductPanel extends JPanel {

    private final MainFrame mainFrame;
    private final CustomerDashboard customerDashboard;
    private final ProductService productService;
    private final CartService cartService;

    private JTextField searchField;
    private JComboBox<String> categoryComboBox;
    private JTable productTable;
    private DefaultTableModel tableModel;

    // Detail Panel Components
    private JLabel detailNameLabel;
    private JLabel detailCategoryLabel;
    private JLabel detailPriceLabel;
    private JLabel detailStockLabel;
    private JTextArea detailDescArea;
    private JSpinner quantitySpinner;
    private JButton addToCartBtn;

    private List<Product> currentProducts = new ArrayList<>();
    private Product selectedProduct = null;

    public ProductPanel(MainFrame mainFrame, CustomerDashboard customerDashboard) {
        this.mainFrame = mainFrame;
        this.customerDashboard = customerDashboard;
        this.productService = new ProductService();
        this.cartService = new CartService();

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIUtils.BG_LIGHT);

        initUI();
    }

    private void initUI() {
        // Top Filter Bar
        JPanel filterPanel = UIUtils.createCardPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 5));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UIUtils.FONT_BODY_BOLD);

        searchField = UIUtils.createTextField();
        searchField.setPreferredSize(new Dimension(200, 32));

        JLabel catLabel = new JLabel("Category:");
        catLabel.setFont(UIUtils.FONT_BODY_BOLD);

        categoryComboBox = new JComboBox<>(new String[]{"All"});
        categoryComboBox.setFont(UIUtils.FONT_BODY);
        categoryComboBox.setPreferredSize(new Dimension(150, 32));

        JButton searchBtn = UIUtils.createPrimaryButton("🔍 Search");
        searchBtn.setPreferredSize(new Dimension(110, 32));
        searchBtn.addActionListener(e -> filterProducts());

        JButton refreshBtn = UIUtils.createSecondaryButton("🔄 Reset");
        refreshBtn.setPreferredSize(new Dimension(100, 32));
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            if (categoryComboBox.getItemCount() > 0) categoryComboBox.setSelectedIndex(0);
            loadProducts();
        });

        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(catLabel);
        filterPanel.add(categoryComboBox);
        filterPanel.add(searchBtn);
        filterPanel.add(refreshBtn);

        add(filterPanel, BorderLayout.NORTH);

        // Center SplitPane (Left: Table, Right: Details Panel)
        String[] columnNames = {"ID", "Product Name", "Category", "Price", "Stock"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        UIUtils.styleTable(productTable);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(70);

        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0 && selectedRow < currentProducts.size()) {
                    selectedProduct = currentProducts.get(selectedRow);
                    displayProductDetails(selectedProduct);
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(productTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));

        // Right Detail Card Panel
        JPanel detailCard = UIUtils.createCardPanel();
        detailCard.setLayout(new BorderLayout(10, 10));
        detailCard.setPreferredSize(new Dimension(340, 400));

        JLabel detailHeader = new JLabel("Product Details");
        detailHeader.setFont(UIUtils.FONT_HEADER_MEDIUM);
        detailHeader.setForeground(UIUtils.PRIMARY_COLOR);
        detailHeader.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel detailBody = new JPanel();
        detailBody.setLayout(new BoxLayout(detailBody, BoxLayout.Y_AXIS));
        detailBody.setOpaque(false);

        detailNameLabel = new JLabel("Select a product to view details");
        detailNameLabel.setFont(UIUtils.FONT_BODY_BOLD);

        detailCategoryLabel = new JLabel("Category: -");
        detailCategoryLabel.setFont(UIUtils.FONT_BODY);
        detailCategoryLabel.setForeground(UIUtils.TEXT_MUTED);

        detailPriceLabel = new JLabel("Price: -");
        detailPriceLabel.setFont(UIUtils.FONT_HEADER_MEDIUM);
        detailPriceLabel.setForeground(UIUtils.ACCENT_COLOR);

        detailStockLabel = new JLabel("Stock: -");
        detailStockLabel.setFont(UIUtils.FONT_BODY_BOLD);

        detailDescArea = new JTextArea(5, 20);
        detailDescArea.setFont(UIUtils.FONT_BODY);
        detailDescArea.setLineWrap(true);
        detailDescArea.setWrapStyleWord(true);
        detailDescArea.setEditable(false);
        detailDescArea.setBackground(UIUtils.BG_LIGHT);
        detailDescArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane descScrollPane = new JScrollPane(detailDescArea);
        descScrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        qtyPanel.setOpaque(false);
        JLabel qtyLabel = new JLabel("Quantity:");
        qtyLabel.setFont(UIUtils.FONT_BODY_BOLD);
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 99, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.setPreferredSize(new Dimension(70, 30));
        qtyPanel.add(qtyLabel);
        qtyPanel.add(quantitySpinner);

        addToCartBtn = UIUtils.createSuccessButton("🛒 Add to Cart");
        addToCartBtn.setPreferredSize(new Dimension(300, 40));
        addToCartBtn.setEnabled(false);
        addToCartBtn.addActionListener(e -> performAddToCart());

        detailBody.add(detailNameLabel);
        detailBody.add(Box.createRigidArea(new Dimension(0, 5)));
        detailBody.add(detailCategoryLabel);
        detailBody.add(Box.createRigidArea(new Dimension(0, 10)));
        detailBody.add(detailPriceLabel);
        detailBody.add(Box.createRigidArea(new Dimension(0, 5)));
        detailBody.add(detailStockLabel);
        detailBody.add(Box.createRigidArea(new Dimension(0, 10)));
        detailBody.add(new JLabel("Description:"));
        detailBody.add(Box.createRigidArea(new Dimension(0, 5)));
        detailBody.add(descScrollPane);
        detailBody.add(Box.createRigidArea(new Dimension(0, 10)));
        detailBody.add(qtyPanel);

        detailCard.add(detailHeader, BorderLayout.NORTH);
        detailCard.add(detailBody, BorderLayout.CENTER);
        detailCard.add(addToCartBtn, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, detailCard);
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerLocation(650);

        add(splitPane, BorderLayout.CENTER);
    }

    public void loadProducts() {
        try {
            // Load Categories into ComboBox
            List<String> categories = productService.getAllCategories();
            categoryComboBox.removeAllItems();
            for (String cat : categories) {
                categoryComboBox.addItem(cat);
            }

            filterProducts();
        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Error Loading Products");
        }
    }

    private void filterProducts() {
        String keyword = searchField.getText();
        String selectedCat = (String) categoryComboBox.getSelectedItem();

        try {
            currentProducts = productService.searchProducts(keyword, selectedCat);
            tableModel.setRowCount(0);

            for (Product p : currentProducts) {
                tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategory(),
                    String.format("$%.2f", p.getPrice()),
                    p.getStock() > 0 ? p.getStock() + " units" : "OUT OF STOCK"
                });
            }

            selectedProduct = null;
            addToCartBtn.setEnabled(false);
            detailNameLabel.setText("Select a product to view details");
            detailCategoryLabel.setText("Category: -");
            detailPriceLabel.setText("Price: -");
            detailStockLabel.setText("Stock: -");
            detailDescArea.setText("");

        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Error Filtering Products");
        }
    }

    private void displayProductDetails(Product p) {
        if (p == null) return;
        detailNameLabel.setText(p.getName());
        detailCategoryLabel.setText("Category: " + p.getCategory());
        detailPriceLabel.setText(String.format("$%.2f", p.getPrice()));

        if (p.getStock() <= 0) {
            detailStockLabel.setText("Stock: OUT OF STOCK");
            detailStockLabel.setForeground(UIUtils.DANGER_COLOR);
            addToCartBtn.setEnabled(false);
        } else if (p.getStock() <= 5) {
            detailStockLabel.setText("Stock: Only " + p.getStock() + " left in stock!");
            detailStockLabel.setForeground(UIUtils.WARNING_COLOR);
            addToCartBtn.setEnabled(true);
        } else {
            detailStockLabel.setText("Stock: " + p.getStock() + " available");
            detailStockLabel.setForeground(UIUtils.ACCENT_COLOR);
            addToCartBtn.setEnabled(true);
        }

        detailDescArea.setText(p.getDescription() != null ? p.getDescription() : "No detailed description available.");
        quantitySpinner.setModel(new SpinnerNumberModel(1, 1, Math.max(1, p.getStock()), 1));
    }

    private void performAddToCart() {
        if (selectedProduct == null || mainFrame.getCurrentUser() == null) return;
        int qty = (Integer) quantitySpinner.getValue();

        try {
            cartService.addToCart(mainFrame.getCurrentUser().getId(), selectedProduct, qty);
            UIUtils.showInfo(this, "Added " + qty + " x '" + selectedProduct.getName() + "' to your shopping cart!", "Cart Updated");
        } catch (InsufficientStockException ex) {
            UIUtils.showError(this, ex.getMessage(), "Stock Error");
        } catch (DatabaseException ex) {
            UIUtils.showError(this, ex.getMessage(), "Database Error");
        }
    }
}
