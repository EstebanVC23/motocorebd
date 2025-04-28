package com.motocoredb.dao.interfaces;

import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.models.AppointmentService;
import com.motocoredb.models.Customer;

import java.util.List;
import java.util.Date;

public interface IWorkshopDao {

    boolean createAppointment(WorkshopAppointment appointment, List<AppointmentService> services);

    boolean updateAppointment(WorkshopAppointment appointment, List<AppointmentService> services);

    boolean updateStatus(int appointmentId, String status);

    List<WorkshopAppointment> getByDate(String date);

    boolean deleteAppointment(int appointmentId);

    List<WorkshopAppointment> getAllAppointments();

    int setStateOfAppointment(int appointmentId, String state);

    WorkshopAppointment getAppointmentById(int appointmentId);

    List<Customer> getAllCustomers();

    List<AppointmentService> getServicesByAppointmentId(int appointmentId);

    List<WorkshopAppointment> findByDateRange(Date startDate, Date endDate);;
}