package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Date;
import java.sql.Time;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkshopAppointment {
    private int appointmentId;
    private int customerId;
    private Date scheduledDate;
    private Time scheduledTime;
    private String visitReason;
    private String motorcycleDescription;
    private String licensePlate;
    private String status; // 'Scheduled', 'InProgress', 'Completed', 'Canceled'
    private int userId; // assigned mechanic
    private String notes;
}