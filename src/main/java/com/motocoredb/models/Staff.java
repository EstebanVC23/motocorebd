package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Date;

@Data @NoArgsConstructor @AllArgsConstructor
public class Staff {
    private int staffId;
    private String fullName;
    private String identityDocument;
    private String position;
    private String specialty;
    private String phone;
    private String email;
    private String address;
    private Date hireDate;
    private String status; // Active, Inactive
    private int userId;
}