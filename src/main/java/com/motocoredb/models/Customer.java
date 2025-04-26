package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data @NoArgsConstructor @AllArgsConstructor
public class Customer {
    private int customerId;
    private String customerType; // Individual, Company
    private String nameOrCompany;
    private String identityDocument;
    private String address;
    private String phone;
    private String email;
    private Timestamp createdAt;
    private int purchaseCount;
    private String status; // Active, Inactive
}