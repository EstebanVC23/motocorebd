package com.motocoredb.dao.interfaces;

import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.models.AppointmentService;
import com.motocoredb.models.UsedProduct;
import java.util.List;

public interface IWorkshopDao {
    
    boolean createAppointment(WorkshopAppointment appointment, List<AppointmentService> services);
    
    boolean updateStatus(int appointmentId, String status);
    
    List<WorkshopAppointment> getByDate(String date);
    
    boolean addUsedProducts(int appointmentId, List<UsedProduct> products);

    boolean deleteAppointment(int appointmentId);

    List<WorkshopAppointment> getAllAppointments();

    int setStateOfAppointment(int appointmentId, String state);

    WorkshopAppointment getAppointmentById(int appointmentId);
}
