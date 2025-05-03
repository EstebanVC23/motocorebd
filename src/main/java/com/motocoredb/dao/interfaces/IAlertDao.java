package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Alert;

import java.util.List;

public interface IAlertDao {

    /**
     * Interfaz para la gestión de alertas en la base de datos.
     * Proporciona métodos para crear, leer, actualizar y eliminar alertas.
     */
    List<Alert> getAlertsByStatus(String status);

    /**
     * Obtiene una lista de alertas filtradas por su tipo.
     * 
     * @param type Tipo de alerta a filtrar.
     * @return Lista de alertas que coinciden con el tipo especificado.
     */
    List<Alert> getAlertsByType(String type);

    /**
     * Obtiene una lista de alertas filtradas por su tipo y estado.
     * 
     * @param type Tipo de alerta a filtrar.
     * @param status Estado de la alerta a filtrar.
     * @return Lista de alertas que coinciden con el tipo y estado especificados.
     */
    List<Alert> getUpcomingAppointments();

    /**
     * Obtiene una lista de todas las alertas en la base de datos.
     * 
     * @return Lista de todas las alertas.
     */
    List<Alert> getAllAlerts();

    /**
     * Actualiza el estado de una alerta específica por su ID.
     * 
     * @param alertId ID de la alerta a actualizar.
     * @param status Nuevo estado de la alerta.
     * @return true si la operación fue exitosa.
     */
    boolean updateStatus(int alertId, String status);

    /**
     * Obtiene una alerta específica por su ID.
     * 
     * @param alertId ID de la alerta.
     * @return Objeto Alert correspondiente al ID, o null si no se encuentra.
     */
    Alert getAlertById(int alertId);

    /**
     * Crea una nueva alerta en la base de datos.
     * 
     * @param alert Objeto Alert con los datos de la alerta.
     * @return true si la operación fue exitosa.
     */
    boolean createAlert(Alert alert);
}