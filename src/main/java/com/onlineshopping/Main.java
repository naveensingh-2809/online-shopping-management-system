package com.onlineshopping;

import com.onlineshopping.gui.MainFrame;
import com.onlineshopping.util.FileStorageManager;

import javax.swing.*;

/**
 * Entry point for the Standalone Online Shopping Management System.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize local File I/O data storage on application startup
        FileStorageManager.loadUsers();

        // Set Look and Feel to System Look and Feel for modern OS styling
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        // Launch GUI application safely on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
