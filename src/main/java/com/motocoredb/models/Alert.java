package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    private int alertId;
    private String alertType; // 'LowStock', 'UpcomingAppointment'
    private String message;
    private Timestamp generationDate;
    private Timestamp readDate;
    private String status; // 'Pending', 'Read', 'Resolved'
    private int referenceId;
    private String referenceType; // 'Product', 'Appointment'
}