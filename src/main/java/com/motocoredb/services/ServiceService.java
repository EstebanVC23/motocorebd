package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IServiceDao;
import com.motocoredb.models.WorkshopService;
import java.util.List;

public class ServiceService {
    private final IServiceDao serviceDao;
    
    public ServiceService(IServiceDao serviceDao) {
        this.serviceDao = serviceDao;
    }
    
    public boolean createService(WorkshopService service) {
        return serviceDao.createService(service);
    }
    
    public List<WorkshopService> getAllServices() {
        return serviceDao.listAll();
    }
    
    public WorkshopService getServiceById(int id) {
        return serviceDao.getById(id);
    }
    
    public boolean updateService(WorkshopService service) {
        return serviceDao.updateService(service);
    }
    
    public boolean deactivateService(int id) {
        return serviceDao.changeStatus(id, "Inactive");
    }
}