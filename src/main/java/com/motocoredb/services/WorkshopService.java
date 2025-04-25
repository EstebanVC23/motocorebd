package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IWorkshopDao;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.models.ServiceDetail;
import com.motocoredb.models.UsedProduct;
import java.util.List;

public class WorkshopService {
    private final IWorkshopDao workshopDao;
    
    public WorkshopService(IWorkshopDao workshopDao) {
        this.workshopDao = workshopDao;
    }
    
    public boolean createAppointment(WorkshopAppointment appointment, List<ServiceDetail> services) {
        return workshopDao.createAppointment(appointment, services);
    }
    
    public boolean updateAppointmentStatus(int appointmentId, String status) {
        return workshopDao.updateStatus(appointmentId, status);
    }
    
    public List<WorkshopAppointment> getAppointmentsByDate(String date) {
        return workshopDao.getByDate(date);
    }
    
    public boolean addUsedProducts(int appointmentId, List<UsedProduct> products) {
        return workshopDao.addUsedProducts(appointmentId, products);
    }
}