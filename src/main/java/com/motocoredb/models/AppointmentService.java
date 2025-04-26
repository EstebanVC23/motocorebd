package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class AppointmentService {
    private int appointmentServiceId;
    private int appointmentId;
    private int serviceId;
    private double chargedPrice;
    private String notes;
}