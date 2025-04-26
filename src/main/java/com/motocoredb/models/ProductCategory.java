package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ProductCategory {
    private int categoryId;
    private String categoryName;
    private String description;
    private String status; // Active, Inactive
}