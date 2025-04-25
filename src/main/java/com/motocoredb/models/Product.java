package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private int productId;
    private String productCode;
    private String name;
    private String description;
    private int categoryId;
    private double purchasePrice;
    private double salePrice;
    private int currentStock;
    private int minimumStock;
    private int supplierId;
    private String status; // 'Active', 'Inactive'
    private Timestamp registrationDate;
    private Timestamp lastUpdate;
}