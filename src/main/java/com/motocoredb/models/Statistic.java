package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistic {
    private int statisticId;
    private String statisticType; // 'Sales', 'Inventory', 'Appointments'
    private String period; // 'Daily', 'Weekly', 'Monthly'
    private Date startDate;
    private Date endDate;
    private double numericValue;
    private String description;
    private Timestamp generationDate;
}