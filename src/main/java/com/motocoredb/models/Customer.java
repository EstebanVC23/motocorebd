package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private int customerId;
    private String customerType; // 'Individual', 'Company'
    private String nameOrBusinessName;
    private String idDocument;
    private String address;
    private String phone;
    private String email;
    private Timestamp registrationDate;
    private int purchaseCount;
    private String status; // 'Active', 'Inactive'
}