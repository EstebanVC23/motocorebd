package com.motocoredb.controllers;

import com.motocoredb.models.Alert;
import com.motocoredb.services.AlertService;
import java.util.List;

public class AlertController {
    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    public List<Alert> getPendingAlerts() {
        return alertService.getPendingAlerts();
    }

    public boolean markAlertAsRead(int alertId) {
        return alertService.markAlertAsRead(alertId);
    }

    public boolean resolveAlert(int alertId) {
        return alertService.resolveAlert(alertId);
    }
}