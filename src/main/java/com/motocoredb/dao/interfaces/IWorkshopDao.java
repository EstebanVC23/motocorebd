package com.motocoredb.dao.interfaces;

import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.models.ServiceDetail;
import com.motocoredb.models.UsedProduct;
import java.util.List;

public interface IWorkshopDao {
    boolean createAppointment(WorkshopAppointment appointment, List<ServiceDetail> services);
    boolean updateStatus(int appointmentId, String status);
    List<WorkshopAppointment> getByDate(String date);
    boolean addUsedProducts(int appointmentId, List<UsedProduct> products);
}