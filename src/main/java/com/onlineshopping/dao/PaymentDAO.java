package com.onlineshopping.dao;

import com.onlineshopping.exception.DatabaseException;
import com.onlineshopping.model.Payment;
import com.onlineshopping.util.FileStorageManager;

import java.util.List;

/**
 * Data Access Object for Payments using local File I/O storage.
 */
public class PaymentDAO {

    public Payment getPaymentByOrderId(int orderId) throws DatabaseException {
        List<Payment> payments = FileStorageManager.loadPayments();
        for (Payment p : payments) {
            if (p.getOrderId() == orderId) {
                return p;
            }
        }
        return null;
    }
}
