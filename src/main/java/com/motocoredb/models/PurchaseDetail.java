package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDetail {
    private int purchaseDetailId;
    private int purchaseId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double subtotal;
}