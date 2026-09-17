package com.onlineshopping.gui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Modern UI Styling & Helper Utilities for Java Swing.
 * Centralized button styling ensuring high contrast, opaque backgrounds, and cross-platform visibility.
 */
public class UIUtils {

    // Color Palette - High Contrast Ecommerce Theme
    public static final Color PRIMARY_COLOR = new Color(20, 100, 220);       // Strong Royal Blue
    public static final Color PRIMARY_HOVER = new Color(10, 75, 175);
    
    public static final Color ACCENT_COLOR = new Color(30, 145, 55);         // Solid Emerald Green
    public static final Color ACCENT_HOVER = new Color(20, 110, 40);
    
    public static final Color DANGER_COLOR = new Color(200, 35, 50);         // Solid Crimson Red
    public static final Color DANGER_HOVER = new Color(155, 20, 35);
    
    public static final Color SECONDARY_COLOR = new Color(75, 85, 100);     // Dark Slate Grey
    public static final Color SECONDARY_HOVER = new Color(50, 60, 75);

    public static final Color WARNING_COLOR = new Color(215, 145, 0);       // Dark Gold Yellow
    public static final Color BG_DARK = new Color(25, 30, 38);               // Dark Header
    public static final Color BG_LIGHT = new Color(242, 244, 248);           // App Background
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_DARK = new Color(30, 35, 42);
    public static final Color TEXT_MUTED = new Color(90, 98, 108);
    public static final Color BORDER_COLOR = new Color(210, 215, 222);

    // Fonts
    public static final Font FONT_HEADER_LARGE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER_MEDIUM = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.BOLD, 11);

    /**
     * Creates styled primary button with solid opaque background and white text.
     */
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY_COLOR, Color.WHITE, PRIMARY_HOVER);
    }

    /**
     * Creates styled success action button (e.g. Checkout, Save).
     */
    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, ACCENT_COLOR, Color.WHITE, ACCENT_HOVER);
    }

    /**
     * Creates styled danger action button (e.g. Delete, Remove).
     */
    public static JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER_COLOR, Color.WHITE, DANGER_HOVER);
    }

    /**
     * Creates styled secondary action button (e.g. Cancel, Reset).
     */
    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, SECONDARY_COLOR, Color.WHITE, SECONDARY_HOVER);
    }

    /**
     * Core button constructor enforcing opacity, solid background, high contrast,
     * crisp borders, and hover/disabled state handlers.
     */
    private static JButton createStyledButton(String text, Color bg, Color fg, Color hoverBg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        
        // Explicit Swing look & feel flags for solid opaque rendering
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Compound line border with darker outline for maximum visibility
        Color borderColor = bg.darker();
        btn.setBorder(new CompoundBorder(
            new LineBorder(borderColor, 1, true),
            new EmptyBorder(7, 14, 7, 14)
        ));

        // Mouse Hover Feedback
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(hoverBg);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(bg);
                }
            }
        });

        // Property Change Listener for smooth disabled state styling
        btn.addPropertyChangeListener("enabled", evt -> {
            boolean enabled = (Boolean) evt.getNewValue();
            if (enabled) {
                btn.setBackground(bg);
                btn.setForeground(fg);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else {
                btn.setBackground(new Color(210, 215, 222));
                btn.setForeground(new Color(120, 125, 135));
                btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return btn;
    }

    /**
     * Style JTable for modern appearance.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(215, 230, 250));
        table.setSelectionForeground(TEXT_DARK);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(new Color(230, 234, 240));
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * Creates styled text field with padding.
     */
    public static JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    /**
     * Creates styled password field with padding.
     */
    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    /**
     * Creates a card panel with standard border & background.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    /**
     * Displays informative user alert.
     */
    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays error alert.
     */
    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
