package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IAlertDao;
import com.motocoredb.models.Alert;

import java.util.List;

public class AlertService {
    private final IAlertDao alertDao;

    public AlertService(IAlertDao alertDao) {
        this.alertDao = alertDao;
    }

    /**
     * Obtiene todas las alertas según el filtro.
     * 
     * @param filterType El tipo de filtro (e.g., "Leídas", "Por Bajo Stock", "Citas Pendientes", "Citas Próximas", "Todas").
     * @return Lista de alertas filtradas.
     */
    public List<Alert> getAlertsByFilter(String filterType) {
        switch (filterType) {
            case "Leídas":
                return alertDao.getAlertsByStatus("Read");
            case "Por Bajo Stock":
                return alertDao.getAlertsByType("Low stock");
            case "Citas Pendientes":
                return alertDao.getAlertsByType("Upcoming appointment");
            case "Citas Próximas":
                return alertDao.getUpcomingAppointments();
            default:
                return alertDao.getAllAlerts(); // Retorna todas las alertas
        }
    }

    /**
     * Obtiene alertas pendientes.
     * 
     * @return Lista de alertas pendientes.
     */
    public List<Alert> getPendingAlerts() {
        return alertDao.getAlertsByStatus("Pending");
    }

    /**
     * Marca una alerta como leída.
     * 
     * @param alertId ID de la alerta.
     * @return true si la operación fue exitosa.
     */
    public boolean markAlertAsRead(int alertId) {
        return alertDao.updateStatus(alertId, "Read");
    }

    /**
     * Resuelve una alerta específica.
     * 
     * @param alertId ID de la alerta.
     * @return true si la operación fue exitosa.
     */
    public boolean resolveAlert(int alertId) {
        return alertDao.updateStatus(alertId, "Resolved");
    }

    /**
     * Obtiene una alerta específica por su ID.
     * 
     * @param alertId ID de la alerta.
     * @return Objeto Alert correspondiente al ID, o null si no se encuentra.
     */
    public Alert getAlertById(int alertId) {
        return alertDao.getAlertById(alertId); // Delegar al DAO
    }

    /**
     * Crea una nueva alerta.
     * 
     * @param alert Objeto Alert con los datos de la alerta.
     * @return true si la operación fue exitosa.
     */
    public boolean createAlert(Alert alert) {
        return alertDao.createAlert(alert); // Delegar al DAO
}
}