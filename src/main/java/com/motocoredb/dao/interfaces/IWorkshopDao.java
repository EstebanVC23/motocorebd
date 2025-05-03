package com.motocoredb.dao.interfaces;

import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.models.AppointmentService;
import com.motocoredb.models.Customer;

import java.util.List;
import java.util.Date;

/**
 * Interfaz para gestionar citas de taller en la base de datos.
 * Define métodos para crear, actualizar, consultar y eliminar citas, así como gestionar servicios asociados.
 */
public interface IWorkshopDao {

    /**
     * Crea una nueva cita de taller junto con los servicios asociados.
     *
     * @param appointment la instancia de {@link WorkshopAppointment} a insertar.
     * @param services la lista de servicios asociados a la cita.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean createAppointment(WorkshopAppointment appointment, List<AppointmentService> services);

    /**
     * Actualiza una cita de taller existente junto con sus servicios asociados.
     *
     * @param appointment la instancia de {@link WorkshopAppointment} con los datos actualizados.
     * @param services la lista de servicios actualizados para la cita.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean updateAppointment(WorkshopAppointment appointment, List<AppointmentService> services);

    /**
     * Actualiza el estado de una cita específica.
     *
     * @param appointmentId el identificador de la cita.
     * @param status el nuevo estado que se asignará a la cita.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean updateStatus(int appointmentId, String status);

    /**
     * Obtiene una lista de citas programadas en una fecha específica.
     *
     * @param date la fecha para buscar citas.
     * @return una lista de instancias de {@link WorkshopAppointment} en la fecha especificada.
     */
    List<WorkshopAppointment> getByDate(String date);

    /**
     * Elimina una cita de taller especificada por su identificador.
     *
     * @param appointmentId el identificador de la cita.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean deleteAppointment(int appointmentId);

    /**
     * Obtiene una lista de todas las citas registradas en la base de datos.
     *
     * @return una lista de instancias de {@link WorkshopAppointment}.
     */
    List<WorkshopAppointment> getAllAppointments();

    /**
     * Establece el estado de una cita específica.
     *
     * @param appointmentId el identificador de la cita.
     * @param state el nuevo estado que se asignará a la cita.
     * @return un entero indicando el resultado de la operación.
     */
    int setStateOfAppointment(int appointmentId, String state);

    /**
     * Obtiene una cita específica por su identificador.
     *
     * @param appointmentId el identificador de la cita.
     * @return una instancia de {@link WorkshopAppointment} si se encuentra; null en caso contrario.
     */
    WorkshopAppointment getAppointmentById(int appointmentId);

    /**
     * Obtiene una lista de todos los clientes asociados al taller.
     *
     * @return una lista de instancias de {@link Customer}.
     */
    List<Customer> getAllCustomers();

    /**
     * Obtiene una lista de servicios asociados a una cita específica.
     *
     * @param appointmentId el identificador de la cita.
     * @return una lista de instancias de {@link AppointmentService}.
     */
    List<AppointmentService> getServicesByAppointmentId(int appointmentId);

    /**
     * Encuentra una lista de citas programadas entre dos fechas específicas.
     *
     * @param startDate la fecha de inicio del rango.
     * @param endDate la fecha de fin del rango.
     * @return una lista de instancias de {@link WorkshopAppointment} en el rango de fechas.
     */
    List<WorkshopAppointment> findByDateRange(Date startDate, Date endDate);
}