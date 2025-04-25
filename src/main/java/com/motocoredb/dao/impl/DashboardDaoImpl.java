package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IDashboardDao;
import com.motocoredb.models.Statistic;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDaoImpl implements IDashboardDao {
    private final Connection connection;

    public DashboardDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public List<Statistic> getTodaySales() {
        List<Statistic> stats = new ArrayList<>();
        String sql = "SELECT SUM(total) as value FROM Sales WHERE DATE(saleDate) = CURDATE()";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Statistic stat = new Statistic();
                stat.setStatisticType("TodaySales");
                stat.setNumericValue(rs.getDouble("value"));
                stats.add(stat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    @Override
    public List<Statistic> getMonthlySales() {
        List<Statistic> stats = new ArrayList<>();
        String sql = "SELECT SUM(total) as value FROM Sales WHERE MONTH(saleDate) = MONTH(CURRENT_DATE())";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Statistic stat = new Statistic();
                stat.setStatisticType("MonthlySales");
                stat.setNumericValue(rs.getDouble("value"));
                stats.add(stat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    @Override
    public List<Statistic> getTodayAppointments() {
        List<Statistic> stats = new ArrayList<>();
        String sql = "SELECT COUNT(*) as count FROM WorkshopAppointments WHERE DATE(scheduledDate) = CURDATE()";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Statistic stat = new Statistic();
                stat.setStatisticType("TodayAppointments");
                stat.setNumericValue(rs.getDouble("count"));
                stats.add(stat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    @Override
    public List<Statistic> getLowStockAlerts() {
        List<Statistic> stats = new ArrayList<>();
        String sql = "SELECT COUNT(*) as count FROM Products WHERE currentStock < minimumStock";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Statistic stat = new Statistic();
                stat.setStatisticType("LowStockAlerts");
                stat.setNumericValue(rs.getDouble("count"));
                stats.add(stat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}