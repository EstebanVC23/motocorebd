package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IAlertDao;
import com.motocoredb.models.Alert;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertDaoImpl implements IAlertDao {
    private final Connection connection = DBConnection.getConnection();

    @Override
    public List<Alert> getPendingAlerts() {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM Alerts WHERE status = 'Pending'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                alerts.add(mapAlert(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }

    private Alert mapAlert(ResultSet rs) throws SQLException {
        Alert alert = new Alert();
        alert.setAlertId(rs.getInt("alertId"));
        alert.setAlertType(rs.getString("alertType"));
        alert.setMessage(rs.getString("message"));
        alert.setGenerationDate(rs.getTimestamp("generationDate"));
        alert.setReadDate(rs.getTimestamp("readDate"));
        alert.setStatus(rs.getString("status"));
        alert.setReferenceId(rs.getInt("referenceId"));
        alert.setReferenceType(rs.getString("referenceType"));
        return alert;
    }

    @Override
    public boolean updateStatus(int alertId, String status) {
        String sql = "UPDATE Alerts SET status = ?, readDate = CURRENT_TIMESTAMP WHERE alertId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, alertId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}