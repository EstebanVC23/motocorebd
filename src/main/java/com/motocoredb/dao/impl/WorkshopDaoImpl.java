package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IWorkshopDao;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.utils.DBConnection;
import com.motocoredb.models.AppointmentService;
import com.motocoredb.models.UsedProduct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkshopDaoImpl implements IWorkshopDao {
    private final Connection connection;

    public WorkshopDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }


    @Override
    public boolean createAppointment(WorkshopAppointment appointment, List<AppointmentService> services) {
        String appointmentQuery = "INSERT INTO WorkshopAppointments (customerId, motorcycleDescription, motorcyclePlate, scheduledDate, scheduledTime, visitReason, userId, notes, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Scheduled')";

        try (PreparedStatement stmt = connection.prepareStatement(appointmentQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, appointment.getCustomerId());
            stmt.setString(2, appointment.getMotorcycleDescription());
            stmt.setString(3, appointment.getMotorcyclePlate());
            stmt.setDate(4, appointment.getScheduledDate());
            stmt.setTime(5, appointment.getScheduledTime());
            stmt.setString(6, appointment.getVisitReason());
            stmt.setInt(7, appointment.getUserId());
            stmt.setString(8, appointment.getNotes());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // Obtener ID de la cita creada
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int appointmentId = generatedKeys.getInt(1);

                        // Insertar servicios asociados
                        String serviceQuery = "INSERT INTO AppointmentServices (appointmentId, serviceId, chargedPrice) VALUES (?, ?, ?)";
                        try (PreparedStatement serviceStmt = connection.prepareStatement(serviceQuery)) {
                            for (AppointmentService service : services) {
                                serviceStmt.setInt(1, appointmentId);
                                serviceStmt.setInt(2, service.getServiceId());
                                serviceStmt.setDouble(3, service.getChargedPrice()); // Usamos chargedPrice del modelo
                                serviceStmt.addBatch();
                            }
                            serviceStmt.executeBatch();
                        }
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateStatus(int appointmentId, String status) {
        String query = "UPDATE WorkshopAppointments SET status = ? WHERE appointmentId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<WorkshopAppointment> getByDate(String date) {
        String query = "SELECT * FROM WorkshopAppointments WHERE scheduledDate = ?";
        List<WorkshopAppointment> appointments = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(date)); // Ahora usamos `Date.valueOf(String)`
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                appointments.add(mapWorkshopAppointment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    @Override
    public boolean addUsedProducts(int appointmentId, List<UsedProduct> products) {
        String query = "INSERT INTO UsedProducts (appointmentId, productId, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (UsedProduct product : products) {
                stmt.setInt(1, appointmentId);
                stmt.setInt(2, product.getProductId());
                stmt.setInt(3, product.getQuantity());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteAppointment(int appointmentId) {
        String query = "DELETE FROM WorkshopAppointments WHERE appointmentId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<WorkshopAppointment> getAllAppointments() {
        String query = "SELECT * FROM WorkshopAppointments";
        List<WorkshopAppointment> appointments = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                appointments.add(mapWorkshopAppointment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    @Override
    public int setStateOfAppointment(int appointmentId, String state) {
        return updateStatus(appointmentId, state) ? 1 : 0;
    }

    private WorkshopAppointment mapWorkshopAppointment(ResultSet rs) throws SQLException {
        return new WorkshopAppointment(
                rs.getInt("appointmentId"),
                rs.getInt("customerId"),
                rs.getDate("scheduledDate"),
                rs.getTime("scheduledTime"),
                rs.getString("visitReason"),
                rs.getString("motorcycleDescription"),
                rs.getString("motorcyclePlate"),
                rs.getString("status"),
                rs.getInt("userId"),
                rs.getString("notes")
        ); // Constructor corregido para coincidir con el modelo
    }

    @Override
    public WorkshopAppointment getAppointmentById(int appointmentId) {
        String query = "SELECT * FROM WorkshopAppointments WHERE appointmentId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new WorkshopAppointment(
                        rs.getInt("appointmentId"),
                        rs.getInt("customerId"),
                        rs.getDate("scheduledDate"),
                        rs.getTime("scheduledTime"),
                        rs.getString("visitReason"),
                        rs.getString("motorcycleDescription"),
                        rs.getString("motorcyclePlate"),
                        rs.getString("status"),
                        rs.getInt("userId"),
                        rs.getString("notes")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}