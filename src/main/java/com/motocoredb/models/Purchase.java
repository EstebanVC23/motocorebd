package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data @NoArgsConstructor @AllArgsConstructor
public class Purchase {
    private int purchaseId;
    private String invoiceNumber;
    private Timestamp purchaseDate;
    private int supplierId;
    private int userId;
    private double subtotal;
    private double tax;
    private double total;
    private String status; // Pending, Received, Cancelled
    private String notes;
}