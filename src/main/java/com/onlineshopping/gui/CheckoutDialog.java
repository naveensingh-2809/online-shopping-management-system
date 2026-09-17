package com.onlineshopping.gui;

import com.onlineshopping.exception.EmptyCartException;
import com.onlineshopping.exception.InsufficientStockException;
import com.onlineshopping.model.CartItem;
import com.onlineshopping.model.Customer;
import com.onlineshopping.model.User;
import com.onlineshopping.payment.CardPayment;
import com.onlineshopping.payment.CashOnDelivery;
import com.onlineshopping.payment.PaymentMethod;
import com.onlineshopping.payment.UPIPayment;
import com.onlineshopping.service.CartService;
import com.onlineshopping.service.OrderService;
import com.onlineshopping.thread.OrderProcessingThread;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Swing Modal Dialog for Order Checkout & Payment Method Selection.
 * Demonstrates:
 * - Polymorphism with PaymentMethod strategy instances
 * - Exception Handling
 * - Multithreading callback handling
 */
public class CheckoutDialog extends JDialog {

    private final MainFrame mainFrame;
    private final CustomerDashboard customerDashboard;
    private final OrderService orderService;
    private final CartService cartService;
    private final List<CartItem> cartItems;

    private JTextArea shippingArea;
    private JComboBox<String> paymentMethodCombo;
    private JPanel paymentDetailsCardPanel;
    private CardLayout paymentCardLayout;

    // Card Input Fields
    private JTextField cardNumberField;
    private JTextField cardHolderField;
    private JTextField cardExpiryField;
    private JTextField cardCvvField;

    // UPI Input Field
    private JTextField upiIdField;

    // COD Label
    private JLabel codInfoLabel;

    private JLabel grandTotalLabel;
    private JButton placeOrderBtn;

    public CheckoutDialog(MainFrame mainFrame, CustomerDashboard customerDashboard, List<CartItem> cartItems) {
        super(mainFrame, "Checkout & Payment Confirmation", true);
        this.mainFrame = mainFrame;
        this.customerDashboard = customerDashboard;
        this.cartItems = cartItems;
        this.orderService = new OrderService();
        this.cartService = new CartService();

        setSize(580, 640);
        setLocationRelativeTo(mainFrame);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(UIUtils.BG_LIGHT);

        // 1. Order Summary Section
        JPanel summaryCard = UIUtils.createCardPanel();
        summaryCard.setLayout(new BorderLayout());

        JLabel summaryHeader = new JLabel("1. Order Summary");
        summaryHeader.setFont(UIUtils.FONT_HEADER_MEDIUM);
        summaryHeader.setForeground(UIUtils.PRIMARY_COLOR);

        double total = cartService.calculateCartTotal(cartItems);

        StringBuilder sb = new StringBuilder();
        for (CartItem item : cartItems) {
            sb.append(String.format("• %s x %d = $%.2f\n", item.getProduct().getName(), item.getQuantity(), item.getSubtotal()));
        }

        JTextArea summaryArea = new JTextArea(sb.toString(), 4, 30);
        summaryArea.setFont(UIUtils.FONT_BODY);
        summaryArea.setEditable(false);
        summaryArea.setBackground(UIUtils.BG_LIGHT);
        summaryArea.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane summaryScroll = new JScrollPane(summaryArea);

        summaryCard.add(summaryHeader, BorderLayout.NORTH);
        summaryCard.add(summaryScroll, BorderLayout.CENTER);

        // 2. Shipping Address Section
        JPanel shippingCard = UIUtils.createCardPanel();
        shippingCard.setLayout(new BorderLayout(5, 5));

        JLabel shippingHeader = new JLabel("2. Delivery Address");
        shippingHeader.setFont(UIUtils.FONT_HEADER_MEDIUM);
        shippingHeader.setForeground(UIUtils.PRIMARY_COLOR);

        User currentUser = mainFrame.getCurrentUser();
        String initialAddress = (currentUser instanceof Customer) ? ((Customer) currentUser).getAddress() : "";

        shippingArea = new JTextArea(initialAddress, 2, 30);
        shippingArea.setFont(UIUtils.FONT_BODY);
        shippingArea.setLineWrap(true);
        shippingArea.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));

        shippingCard.add(shippingHeader, BorderLayout.NORTH);
        shippingCard.add(new JScrollPane(shippingArea), BorderLayout.CENTER);

        // 3. Polymorphic Payment Method Section
        JPanel paymentCard = UIUtils.createCardPanel();
        paymentCard.setLayout(new BorderLayout(10, 10));

        JLabel paymentHeader = new JLabel("3. Select Payment Method");
        paymentHeader.setFont(UIUtils.FONT_HEADER_MEDIUM);
        paymentHeader.setForeground(UIUtils.PRIMARY_COLOR);

        paymentMethodCombo = new JComboBox<>(new String[]{"Credit / Debit Card", "UPI Payment (GPay / PhonePe)", "Cash On Delivery (COD)"});
        paymentMethodCombo.setFont(UIUtils.FONT_BODY_BOLD);
        paymentMethodCombo.setPreferredSize(new Dimension(300, 32));

        // Dynamic Payment Options Panel using CardLayout (Demonstrates Strategy / Polymorphism!)
        paymentCardLayout = new CardLayout();
        paymentDetailsCardPanel = new JPanel(paymentCardLayout);
        paymentDetailsCardPanel.setOpaque(false);

        // Card 1: Credit/Debit Form
        JPanel cardForm = new JPanel(new GridLayout(4, 2, 5, 5));
        cardForm.setOpaque(false);
        cardNumberField = UIUtils.createTextField();
        cardNumberField.setText("4532 8912 3456 7890");
        cardHolderField = UIUtils.createTextField();
        cardHolderField.setText(currentUser != null ? currentUser.getName() : "John Doe");
        cardExpiryField = UIUtils.createTextField();
        cardExpiryField.setText("12/28");
        cardCvvField = UIUtils.createTextField();
        cardCvvField.setText("888");

        cardForm.add(new JLabel("Card Number:"));
        cardForm.add(cardNumberField);
        cardForm.add(new JLabel("Cardholder Name:"));
        cardForm.add(cardHolderField);
        cardForm.add(new JLabel("Expiry (MM/YY):"));
        cardForm.add(cardExpiryField);
        cardForm.add(new JLabel("CVV:"));
        cardForm.add(cardCvvField);

        // Card 2: UPI Form
        JPanel upiForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        upiForm.setOpaque(false);
        upiIdField = UIUtils.createTextField();
        upiIdField.setPreferredSize(new Dimension(220, 32));
        upiIdField.setText("john@okaxis");
        upiForm.add(new JLabel("Enter Virtual Payment Address (UPI ID):"));
        upiForm.add(upiIdField);

        // Card 3: COD Info
        JPanel codForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        codForm.setOpaque(false);
        codInfoLabel = new JLabel("💵 Pay total amount in cash when shipment arrives at your address.");
        codInfoLabel.setFont(UIUtils.FONT_BODY_BOLD);
        codForm.add(codInfoLabel);

        paymentDetailsCardPanel.add(cardForm, "CARD");
        paymentDetailsCardPanel.add(upiForm, "UPI");
        paymentDetailsCardPanel.add(codForm, "COD");

        paymentMethodCombo.addActionListener(e -> {
            int idx = paymentMethodCombo.getSelectedIndex();
            if (idx == 0) paymentCardLayout.show(paymentDetailsCardPanel, "CARD");
            else if (idx == 1) paymentCardLayout.show(paymentDetailsCardPanel, "UPI");
            else paymentCardLayout.show(paymentDetailsCardPanel, "COD");
        });

        JPanel comboWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboWrapper.setOpaque(false);
        comboWrapper.add(new JLabel("Payment Type:"));
        comboWrapper.add(paymentMethodCombo);

        paymentCard.add(paymentHeader, BorderLayout.NORTH);
        paymentCard.add(comboWrapper, BorderLayout.CENTER);
        paymentCard.add(paymentDetailsCardPanel, BorderLayout.SOUTH);

        contentPanel.add(summaryCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(shippingCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(paymentCard);

        // Bottom Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBorder(new EmptyBorder(15, 20, 15, 20));
        bottomBar.setBackground(UIUtils.CARD_BG);

        grandTotalLabel = new JLabel(String.format("Total to Pay: $%.2f", total));
        grandTotalLabel.setFont(UIUtils.FONT_HEADER_LARGE);
        grandTotalLabel.setForeground(UIUtils.ACCENT_COLOR);

        placeOrderBtn = UIUtils.createSuccessButton("🔒 Confirm & Pay");
        placeOrderBtn.setPreferredSize(new Dimension(180, 40));
        placeOrderBtn.setFont(UIUtils.FONT_HEADER_MEDIUM);
        placeOrderBtn.addActionListener(e -> performCheckout());

        bottomBar.add(grandTotalLabel, BorderLayout.WEST);
        bottomBar.add(placeOrderBtn, BorderLayout.EAST);

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private void performCheckout() {
        String shipping = shippingArea.getText();
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        // Instantiate Polymorphic PaymentMethod Strategy based on user selection!
        PaymentMethod paymentMethod = null;
        int selectedIndex = paymentMethodCombo.getSelectedIndex();

        if (selectedIndex == 0) {
            String cardNo = cardNumberField.getText();
            String name = cardHolderField.getText();
            String exp = cardExpiryField.getText();
            String cvv = cardCvvField.getText();
            paymentMethod = new CardPayment(cardNo, name, exp, cvv);
        } else if (selectedIndex == 1) {
            String upi = upiIdField.getText();
            paymentMethod = new UPIPayment(upi);
        } else {
            paymentMethod = new CashOnDelivery(shipping);
        }

        // Show progress indicator
        placeOrderBtn.setEnabled(false);
        placeOrderBtn.setText("Processing...");

        try {
            // Execute checkout (DB Transaction + Multithreaded Receipt Generation)
            orderService.checkout(user.getId(), user.getName(), shipping, paymentMethod, new OrderProcessingThread.OrderProcessingCallback() {
                @Override
                public void onSuccess(String receiptPath) {
                    placeOrderBtn.setEnabled(true);
                    placeOrderBtn.setText("🔒 Confirm & Pay");
                    dispose(); // Close modal dialog

                    // Show success confirmation
                    JOptionPane.showMessageDialog(mainFrame, 
                        "🎉 Order Placed Successfully!\n\n" +
                        "A text receipt has been generated in background thread:\n" + receiptPath, 
                        "Order Confirmed", 
                        JOptionPane.INFORMATION_MESSAGE);

                    // Refresh cart and switch to order history tab
                    customerDashboard.loadCustomerData(user);
                    customerDashboard.switchToOrdersTab();
                }

                @Override
                public void onError(String errorMessage) {
                    placeOrderBtn.setEnabled(true);
                    placeOrderBtn.setText("🔒 Confirm & Pay");
                    UIUtils.showError(CheckoutDialog.this, errorMessage, "Checkout Failed");
                }
            });

        } catch (EmptyCartException | InsufficientStockException | IllegalArgumentException ex) {
            placeOrderBtn.setEnabled(true);
            placeOrderBtn.setText("🔒 Confirm & Pay");
            UIUtils.showError(this, ex.getMessage(), "Checkout Validation Failed");
        } catch (Exception ex) {
            placeOrderBtn.setEnabled(true);
            placeOrderBtn.setText("🔒 Confirm & Pay");
            UIUtils.showError(this, "Unexpected error: " + ex.getMessage(), "Error");
        }
    }
}
