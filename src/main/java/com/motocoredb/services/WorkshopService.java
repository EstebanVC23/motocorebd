package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IWorkshopDao;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.models.AppointmentService;
import com.motocoredb.models.Customer;
import com.motocoredb.models.UsedProduct;
import com.motocoredb.dao.interfaces.ICustomerDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class WorkshopService {
    private final IWorkshopDao workshopDao;
    private final ICustomerDao customerDao;

    public WorkshopService(IWorkshopDao workshopDao, ICustomerDao customerDao) {
        this.workshopDao = workshopDao;
        this.customerDao = customerDao;
    }

    public boolean createAppointment(WorkshopAppointment appointment, List<AppointmentService> services) {
        try {
            return workshopDao.createAppointment(appointment, services);
        } catch (Exception e) {
            System.err.println("Error al crear la cita: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAppointment(WorkshopAppointment appointment, List<AppointmentService> services) {
        try {
            return workshopDao.updateAppointment(appointment, services);
        } catch (Exception e) {
            System.err.println("Error al actualizar la cita: " + e.getMessage());
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

    public List<Customer> getAllCustomers() {
        try {
            return customerDao.listAllAdmin(); // Delegamos directamente al método listAll
        } catch (Exception e) {
            System.err.println("Error al obtener la lista de clientes: " + e.getMessage());
            return new ArrayList<>(); // Devuelve una lista vacía en caso de error
        }
    }

    /**
     * Obtiene los servicios asociados a una cita específica
     * @param appointmentId ID de la cita
     * @return Lista de servicios asociados a la cita
     */
    public List<AppointmentService> getAppointmentServices(int appointmentId) {
        try {
            return workshopDao.getServicesByAppointmentId(appointmentId);
        } catch (Exception e) {
            System.err.println("Error al obtener los servicios de la cita: " + e.getMessage());
            return new ArrayList<>(); // Devuelve una lista vacía en caso de error
        }
    }

    public List<WorkshopAppointment> getAppointmentsByDateRange(Date startDate, Date endDate) {
        try {
            return workshopDao.findByDateRange(startDate, endDate); // Delegamos la consulta al DAO
        } catch (Exception e) {
            System.err.println("Error al obtener citas por rango de fechas: " + e.getMessage());
            return new ArrayList<>(); // Retorna una lista vacía en caso de error
        }
    }
}