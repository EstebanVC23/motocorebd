package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IAlertDao;
import com.motocoredb.models.Alert;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertDaoImpl implements IAlertDao {
    private final Connection connection;

    public AlertDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public List<Alert> getAlertsByStatus(String status) {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM Alerts WHERE status = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                alerts.add(mapAlert(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }

    @Override
    public List<Alert> getAlertsByType(String type) {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM Alerts WHERE alertType = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                alerts.add(mapAlert(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }

    @Override
    public List<Alert> getUpcomingAppointments() {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM Alerts WHERE alertType = 'Upcoming appointment' ORDER BY generatedAt ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                alerts.add(mapAlert(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }

    @Override
    public List<Alert> getAllAlerts() {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM Alerts";
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

    @Override
    public boolean updateStatus(int alertId, String status) {
        String sql = "UPDATE Alerts SET status = ? WHERE alertId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, alertId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Alert mapAlert(ResultSet rs) throws SQLException {
        return new Alert(
                rs.getInt("alertId"),
                rs.getString("alertType"),
                rs.getString("message"),
                rs.getTimestamp("generatedAt"),
                rs.getTimestamp("readAt"),
                rs.getString("status"),
                rs.getInt("referenceId"),
                rs.getString("referenceType")
        );
    }

    @Override
    public Alert getAlertById(int alertId) {
        String sql = "SELECT * FROM Alerts WHERE alertId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, alertId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapAlert(rs); // Usa el método mapAlert para transformar el ResultSet en un objeto Alert
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retorna null si no se encuentra la alerta
    }

    @Override
    public boolean createAlert(Alert alert) {
        String sql = "INSERT INTO Alerts (alertType, message, generatedAt, status, referenceId, referenceType) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, alert.getAlertType());
            stmt.setString(2, alert.getMessage());
            stmt.setTimestamp(3, alert.getGeneratedAt());
            stmt.setString(4, alert.getStatus());
            stmt.setInt(5, alert.getReferenceId());
            stmt.setString(6, alert.getReferenceType());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}