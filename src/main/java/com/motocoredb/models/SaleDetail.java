package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDetail {
    private int saleDetailId;
    private int saleId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double subtotal;
}