package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IReportDao;
import com.motocoredb.models.Statistic;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDaoImpl implements IReportDao {
    private final Connection connection = DBConnection.getConnection();

    @Override
    public List<Statistic> getSalesStats(String period) {
        List<Statistic> stats = new ArrayList<>();
        String sql = "SELECT DATE(saleDate) as date, SUM(total) as value FROM Sales WHERE DATE(saleDate) BETWEEN ? AND ? GROUP BY DATE(saleDate)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, getStartDate(period));
            stmt.setString(2, getEndDate(period));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.add(mapStatistic(rs, "Sales"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    private Statistic mapStatistic(ResultSet rs, String type) throws SQLException {
        Statistic stat = new Statistic();
        stat.setStatisticType(type);
        stat.setNumericValue(rs.getDouble("value"));
        stat.setStartDate(rs.getDate("date"));
        return stat;
    }

    private String getStartDate(String period) {
        switch (period) {
            case "Weekly": return "DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
            case "Monthly": return "DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";
            default: return "CURDATE()";
        }
    }

    private String getEndDate(String period) {
        return "CURDATE()";
    }

    @Override
    public List<Statistic> getInventoryStats() {
        List<Statistic> stats = new ArrayList<>();
        String sql = "SELECT 'Inventory' as type, COUNT(*) as count FROM Products WHERE currentStock < minimumStock";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Statistic stat = new Statistic();
                stat.setStatisticType("Inventory");
                stat.setNumericValue(rs.getDouble("count"));
                stats.add(stat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    @Override
    public List<Statistic> getWorkshopStats(String period) {
        List<Statistic> stats = new ArrayList<>();
        String sql = "SELECT status, COUNT(*) as count FROM WorkshopAppointments WHERE scheduledDate BETWEEN ? AND ? GROUP BY status";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, getStartDate(period));
            stmt.setString(2, getEndDate(period));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Statistic stat = new Statistic();
                stat.setStatisticType("Workshop-" + rs.getString("status"));
                stat.setNumericValue(rs.getDouble("count"));
                stats.add(stat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}