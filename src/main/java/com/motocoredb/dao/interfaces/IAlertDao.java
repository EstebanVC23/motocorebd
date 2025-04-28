package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Alert;

import java.util.List;

public interface IAlertDao {
    List<Alert> getAlertsByStatus(String status);
    List<Alert> getAlertsByType(String type);
    List<Alert> getUpcomingAppointments();
    List<Alert> getAllAlerts();
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