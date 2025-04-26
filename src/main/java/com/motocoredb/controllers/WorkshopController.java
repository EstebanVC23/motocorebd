package com.motocoredb.controllers;

import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.models.AppointmentService;
import com.motocoredb.services.WorkshopService;
import com.motocoredb.models.UsedProduct;

import java.util.List;

public class WorkshopController {
    private final WorkshopService workshopService;

    public WorkshopController(WorkshopService workshopService) {
        this.workshopService = workshopService;
    }

    public boolean createAppointment(WorkshopAppointment appointment, List<AppointmentService> services) {
        return workshopService.createAppointment(appointment, services);
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        return workshopService.updateAppointmentStatus(appointmentId, status);
    }

    public List<WorkshopAppointment> getAppointmentsByDate(String date) {
        return workshopService.getAppointmentsByDate(date);
    }

    public boolean addUsedProducts(int appointmentId, List<UsedProduct> products) {
        return workshopService.addUsedProducts(appointmentId, products);
    }
}