package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IWorkshopDao;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.models.AppointmentService;
import com.motocoredb.models.UsedProduct;
import java.util.List;

public class WorkshopDaoImpl implements IWorkshopDao {
    @Override
    public boolean createAppointment(WorkshopAppointment appointment, List<AppointmentService> services) {
        return true;
    }

    @Override
    public boolean updateStatus(int appointmentId, String status) {
        return true;
    }

    @Override
    public List<WorkshopAppointment> getByDate(String date) {
        return null;
    }

    @Override
    public boolean addUsedProducts(int appointmentId, List<UsedProduct> products) {
        return true;
    }

    @Override
    public boolean deleteAppointment(int appointmentId) {
        return true;
    }
}
