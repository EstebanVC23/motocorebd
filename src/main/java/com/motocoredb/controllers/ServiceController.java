package com.motocoredb.controllers;

import com.motocoredb.models.WorkshopService;
import com.motocoredb.services.ServiceService;
import java.util.List;

public class ServiceController {
    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    public boolean createService(WorkshopService service) {
        return serviceService.createService(service);
    }

    public List<WorkshopService> getAllServices() {
        return serviceService.getAllServices();
    }

    public WorkshopService getServiceById(int id) {
        return serviceService.getServiceById(id);
    }

    public boolean updateService(WorkshopService service) {
        return serviceService.updateService(service);
    }

    public boolean deactivateService(int id) {
        return serviceService.deactivateService(id);
    }
}