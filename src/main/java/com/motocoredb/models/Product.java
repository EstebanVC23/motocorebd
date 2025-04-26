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
    private ProductCategory category; // Relación con la tabla ProductCategories
    private double purchasePrice;
    private double salePrice;
    private int currentStock;
    private int minStock;
    private Supplier supplier; // Relación con la tabla Suppliers
    private String status; // Active, Inactive
    private Timestamp createdAt;
    private Timestamp updatedAt;
}