package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IServiceDao;
import com.motocoredb.models.WorkshopService;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDaoImpl implements IServiceDao {
    private final Connection connection = DBConnection.getConnection();

    @Override
    public boolean createService(WorkshopService service) {
        String sql = "INSERT INTO WorkshopServices (serviceName, basePrice, estimatedTime) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, service.getServiceName());
            stmt.setDouble(2, service.getBasePrice());
            stmt.setDouble(3, service.getEstimatedTime());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public WorkshopService getById(int id) {
        String sql = "SELECT * FROM WorkshopServices WHERE serviceId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapService(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private WorkshopService mapService(ResultSet rs) throws SQLException {
        WorkshopService service = new WorkshopService();
        service.setServiceId(rs.getInt("serviceId"));
        service.setServiceName(rs.getString("serviceName"));
        service.setDescription(rs.getString("description"));
        service.setBasePrice(rs.getDouble("basePrice"));
        service.setEstimatedTime(rs.getDouble("estimatedTime"));
        service.setStatus(rs.getString("status"));
        return service;
    }

    @Override
    public List<WorkshopService> listAll() {
        List<WorkshopService> services = new ArrayList<>();
        String sql = "SELECT * FROM WorkshopServices";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                services.add(mapService(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    @Override
    public boolean updateService(WorkshopService service) {
        String sql = "UPDATE WorkshopServices SET serviceName = ?, basePrice = ?, estimatedTime = ? WHERE serviceId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, service.getServiceName());
            stmt.setDouble(2, service.getBasePrice());
            stmt.setDouble(3, service.getEstimatedTime());
            stmt.setInt(4, service.getServiceId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changeStatus(int id, String status) {
        String sql = "UPDATE WorkshopServices SET status = ? WHERE serviceId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}