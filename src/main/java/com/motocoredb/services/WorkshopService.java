package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IWorkshopDao;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.models.AppointmentService;
import com.motocoredb.models.UsedProduct;

import java.util.List;

public class WorkshopService {
    private final IWorkshopDao workshopDao;

    public WorkshopService(IWorkshopDao workshopDao) {
        this.workshopDao = workshopDao;
    }

    public boolean createAppointment(WorkshopAppointment appointment, List<AppointmentService> services) {
        try {
            return workshopDao.createAppointment(appointment, services);
        } catch (Exception e) {
            System.err.println("Error al crear la cita: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        try {
            return workshopDao.updateStatus(appointmentId, status);
        } catch (Exception e) {
            System.err.println("Error al actualizar el estado de la cita: " + e.getMessage());
            return false;
        }
    }

    public List<WorkshopAppointment> getAppointmentsByDate(String date) {
        try {
            return workshopDao.getByDate(date);
        } catch (Exception e) {
            System.err.println("Error al obtener citas por fecha: " + e.getMessage());
            return null;
        }
    }

    public boolean addUsedProducts(int appointmentId, List<UsedProduct> products) {
        try {
            return workshopDao.addUsedProducts(appointmentId, products);
        } catch (Exception e) {
            System.err.println("Error al agregar productos usados: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAppointment(int appointmentId) {
        try {
            return workshopDao.deleteAppointment(appointmentId);
        } catch (Exception e) {
            System.err.println("Error al eliminar la cita: " + e.getMessage());
            return false;
        }
    }

    public List<WorkshopAppointment> getAllAppointments() {
        try {
            return workshopDao.getAllAppointments();
        } catch (Exception e) {
            System.err.println("Error al obtener todas las citas: " + e.getMessage());
            return null;
        }
    }

    public boolean setState(int appointmentId, String status) {
        try {
            return workshopDao.updateStatus(appointmentId, status);
        } catch (Exception e) {
            System.err.println("Error al actualizar el estado de la cita: " + e.getMessage());
            return false;
        }
    }
    
    public WorkshopAppointment getAppointmentById(int appointmentId) {
        try {
            return workshopDao.getAppointmentById(appointmentId);
        } catch (Exception e) {
            System.err.println("Error al obtener la cita por ID: " + e.getMessage());
            return null;
        }
    }
}