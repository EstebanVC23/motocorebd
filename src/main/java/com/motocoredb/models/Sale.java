package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data @NoArgsConstructor @AllArgsConstructor
public class Sale {
    private int saleId;
    private String invoiceNumber;
    private Timestamp saleDate;
    private int customerId;
    private int userId;
    private double subtotal;
    private double tax;
    private double discount;
    private double total;
    private String paymentMethod; // Cash, Card, Transfer, Other
    private String status; // Completed, Cancelled
    private String notes;
}