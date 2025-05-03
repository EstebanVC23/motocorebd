package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IAlertDao;
import com.motocoredb.models.Alert;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la interfaz IAlertDao para gestionar alertas en la base de datos.
 * Proporciona métodos para crear, leer, actualizar y eliminar alertas.
 */
public class AlertDaoImpl implements IAlertDao {
    private final Connection connection;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    public AlertDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Obtiene una lista de alertas filtradas por estado.
     * @param status el estado de las alertas a filtrar.
     * @return una lista de alertas que coinciden con el estado proporcionado.
     */
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

    /**
     * Obtiene una lista de alertas filtradas por tipo.
     * @param type el tipo de las alertas a filtrar.
     * @return una lista de alertas que coinciden con el tipo proporcionado.
     */
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

    /**
     * Obtiene una lista de citas próximas ordenadas por fecha de generación.
     *
     * @return una lista de alertas correspondientes a citas próximas.
     */

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

   /**
     * Obtiene una lista de todas las alertas de la base de datos.
     *
     * @return una lista de todas las alertas registradas.
     */
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

    /**
     * Actualiza el estado de una alerta específica.
     *
     * @param alertId el identificador de la alerta a actualizar.
     * @param status el nuevo estado que se asignará a la alerta.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
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

    /**
     * Mapea un objeto ResultSet a una instancia de {@link Alert}.
     *
     * @param rs el ResultSet obtenido de la consulta.
     * @return una instancia de {@link Alert} con los datos del ResultSet.
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
     */

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

    /**
     * Obtiene una alerta específica por su identificador.
     *
     * @param alertId el identificador de la alerta a obtener.
     * @return una instancia de {@link Alert} si se encuentra; null en caso contrario.
     */

    @Override
    public Alert getAlertById(int alertId) {
        String sql = "SELECT * FROM Alerts WHERE alertId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, alertId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapAlert(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Crea una nueva alerta en la base de datos.
     *
     * @param alert la instancia de {@link Alert} a insertar en la base de datos.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
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