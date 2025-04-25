package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int userId;
    private String fullName;
    private String username;
    private String password;
    private String role; // 'Administrator', 'Seller', 'Mechanic'
    private String status; // 'Active', 'Inactive'
    private Timestamp registrationDate;
    private Timestamp lastAccess;
}