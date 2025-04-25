package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    private int supplierId;
    private String companyName;
    private String taxId;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private String status; // 'Active', 'Inactive'
    private Timestamp registrationDate;
}