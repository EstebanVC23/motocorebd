package com.motocoredb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class WorkshopService {
    private int serviceId;
    private String serviceName;
    private String description;
    private double basePrice;
    private double estimatedTime;
    private String status; // Active, Inactive
}