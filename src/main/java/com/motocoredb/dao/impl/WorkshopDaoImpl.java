package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IWorkshopDao;
import com.motocoredb.models.WorkshopAppointment;
import com.motocoredb.models.AppointmentService;
import com.motocoredb.models.Customer;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;



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
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int appointmentId = generatedKeys.getInt(1);

                        String serviceQuery = "INSERT INTO AppointmentServices (appointmentId, serviceId, chargedPrice) VALUES (?, ?, ?)";
                        try (PreparedStatement serviceStmt = connection.prepareStatement(serviceQuery)) {
                            for (AppointmentService service : services) {
                                serviceStmt.setInt(1, appointmentId);
                                serviceStmt.setDouble(2, service.getChargedPrice());
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
public int setStateOfAppointment(int appointmentId, String state) {
    String query = "UPDATE WorkshopAppointments SET status = ? WHERE appointmentId = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setString(1, state); 
        stmt.setInt(2, appointmentId); 
        return stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
        return 0;
    }
}

    @Override
    public boolean updateAppointment(WorkshopAppointment appointment, List<AppointmentService> services) {
        String updateAppointmentQuery = "UPDATE WorkshopAppointments SET customerId = ?, motorcycleDescription = ?, motorcyclePlate = ?, scheduledDate = ?, scheduledTime = ?, visitReason = ?, status = ?, notes = ? WHERE appointmentId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(updateAppointmentQuery)) {
            stmt.setInt(1, appointment.getCustomerId());
            stmt.setString(2, appointment.getMotorcycleDescription());
            stmt.setString(3, appointment.getMotorcyclePlate());
            stmt.setDate(4, appointment.getScheduledDate());
            stmt.setTime(5, appointment.getScheduledTime());
            stmt.setString(6, appointment.getVisitReason());
            stmt.setString(7, appointment.getStatus());
            stmt.setString(8, appointment.getNotes());
            stmt.setInt(9, appointment.getAppointmentId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                String deleteServicesQuery = "DELETE FROM AppointmentServices WHERE appointmentId = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteServicesQuery)) {
                    deleteStmt.setInt(1, appointment.getAppointmentId());
                    deleteStmt.executeUpdate();
                }

                String insertServiceQuery = "INSERT INTO AppointmentServices (appointmentId, serviceId, chargedPrice) VALUES (?, ?, ?)";
                try (PreparedStatement serviceStmt = connection.prepareStatement(insertServiceQuery)) {
                    for (AppointmentService service : services) {
                        serviceStmt.setInt(1, appointment.getAppointmentId());
                        serviceStmt.setDouble(2, service.getChargedPrice());
                        serviceStmt.addBatch();
                    }
                    serviceStmt.executeBatch();
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
            stmt.setDate(1, java.sql.Date.valueOf(date));
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
    public WorkshopAppointment getAppointmentById(int appointmentId) {
        String query = "SELECT * FROM WorkshopAppointments WHERE appointmentId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapWorkshopAppointment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Customer> getAllCustomers() {
        String query = "SELECT * FROM Customers WHERE status = 'Active'";
        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customerId"));
                customer.setNameOrCompany(rs.getString("nameOrCompany"));
                customer.setIdentityDocument(rs.getString("identityDocument"));
                customer.setAddress(rs.getString("address"));
                customer.setPhone(rs.getString("phone"));
                customer.setEmail(rs.getString("email"));
                customer.setStatus(rs.getString("status"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
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
        );
    }

    @Override
    public List<AppointmentService> getServicesByAppointmentId(int appointmentId) {
        List<AppointmentService> services = new ArrayList<>();
        String query = "SELECT * FROM appointment_services WHERE appointment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, appointmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AppointmentService service = new AppointmentService(
                            rs.getInt("appointment_service_id"), 
                            rs.getInt("appointment_id"),     
                            rs.getDouble("charged_price"),     
                            rs.getString("notes")             
                    );
                    services.add(service);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los servicios asociados a la cita: " + e.getMessage());
        }

        return services;
    }

    @Override
    public List<WorkshopAppointment> findByDateRange(Date startDate, Date endDate) {
        List<WorkshopAppointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM WorkshopAppointments WHERE scheduledDate BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, new java.sql.Date(startDate.getTime())); 
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapWorkshopAppointment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving appointments by date range: " + e.getMessage());
        }

        return appointments;
    }
}