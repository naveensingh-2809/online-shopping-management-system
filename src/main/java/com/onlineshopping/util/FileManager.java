package com.onlineshopping.util;

import com.onlineshopping.model.Order;
import com.onlineshopping.model.OrderItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class demonstrating Java File I/O Streams / Reader-Writer.
 * Handles receipt generation and file reading.
 */
public class FileManager {

    private static final String RECEIPTS_DIR = "receipts";

    static {
        // Automatically create receipts directory if it doesn't exist
        File dir = new File(RECEIPTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Generates a detailed plain text receipt for a placed order using Character Writer Streams.
     * @param order The completed order object
     * @param customerName Name of the customer placing the order
     * @param paymentMethod Method used for payment
     * @return Absolute filepath of generated receipt
     * @throws IOException If file creation or writing fails
     */
    public static String generateReceipt(Order order, String customerName, String paymentMethod) throws IOException {
        String filename = RECEIPTS_DIR + File.separator + "order_" + order.getId() + ".txt";
        File file = new File(filename);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = order.getOrderDate() != null ? 
            dateFormat.format(order.getOrderDate()) : dateFormat.format(new Date());

        try (FileWriter fw = new FileWriter(file, false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter writer = new PrintWriter(bw)) {

            writer.println("==========================================================");
            writer.println("           ONLINE SHOPPING MANAGEMENT SYSTEM              ");
            writer.println("                   OFFICIAL RECEIPT                       ");
            writer.println("==========================================================");
            writer.println(" Order ID         : #" + order.getId());
            writer.println(" Customer Name    : " + customerName);
            writer.println(" Order Date       : " + formattedDate);
            writer.println(" Shipping Address : " + order.getShippingAddress());
            writer.println(" Order Status     : " + order.getStatus());
            writer.println(" Payment Method   : " + paymentMethod);
            writer.println("----------------------------------------------------------");
            writer.println(String.format("%-30s %-8s %-10s %-10s", "Item Name", "Qty", "Price", "Subtotal"));
            writer.println("----------------------------------------------------------");

            double total = 0.0;
            for (OrderItem item : order.getItems()) {
                double itemTotal = item.getSubtotal();
                total += itemTotal;
                writer.println(String.format("%-30s %-8d $%-9.2f $%-9.2f", 
                    truncate(item.getProductName(), 28),
                    item.getQuantity(),
                    item.getPrice(),
                    itemTotal));
            }

            writer.println("----------------------------------------------------------");
            writer.println(String.format("%-48s $%.2f", "Subtotal:", total));
            writer.println(String.format("%-48s $%.2f", "Grand Total:", order.getTotalAmount()));
            writer.println("==========================================================");
            writer.println("          Thank you for shopping with us!                 ");
            writer.println("==========================================================");
            writer.flush();
        }

        return file.getAbsolutePath();
    }

    /**
     * Reads and returns content of a receipt file using BufferedReader / FileReader.
     */
    public static String readReceipt(int orderId) throws IOException {
        String filename = RECEIPTS_DIR + File.separator + "order_" + orderId + ".txt";
        File file = new File(filename);

        if (!file.exists()) {
            return "Receipt file not found for Order #" + orderId;
        }

        StringBuilder content = new StringBuilder();
        try (FileReader fr = new FileReader(file);
             BufferedReader reader = new BufferedReader(fr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static String truncate(String text, int length) {
        if (text == null) return "";
        return text.length() <= length ? text : text.substring(0, length - 3) + "...";
    }
}
