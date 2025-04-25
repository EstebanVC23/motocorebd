package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IAlertDao;
import com.motocoredb.models.Alert;
import java.util.List;

public class AlertService {
    private final IAlertDao alertDao;
    
    public AlertService(IAlertDao alertDao) {
        this.alertDao = alertDao;
    }
    
    public List<Alert> getPendingAlerts() {
        return alertDao.getPendingAlerts();
    }
    
    public boolean markAlertAsRead(int alertId) {
        return alertDao.updateStatus(alertId, "Read");
    }
    
    public boolean resolveAlert(int alertId) {
        return alertDao.updateStatus(alertId, "Resolved");
    }
}