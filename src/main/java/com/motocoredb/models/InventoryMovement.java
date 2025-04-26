package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data @NoArgsConstructor @AllArgsConstructor
public class InventoryMovement {
    private int movementId;
    private int productId;
    private String movementType; // In, Out
    private int quantity;
    private Timestamp movementDate;
    private int userId;
    private int referenceId;
    private String referenceType; // Purchase, Sale, Adjustment
    private String notes;
}