package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Alert;
import java.util.List;

public interface IAlertDao {
    List<Alert> getPendingAlerts();
    boolean updateStatus(int alertId, String status);
}