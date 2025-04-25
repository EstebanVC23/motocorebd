package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsedProduct {
    private int usedProductId;
    private int appointmentId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double subtotal;
}