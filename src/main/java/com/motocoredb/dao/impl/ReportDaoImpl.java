package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IReportDao;
import com.motocoredb.models.Statistic;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la interfaz {@link IReportDao} para gestionar los informes y estadísticas.
 * Proporciona métodos para generar estadísticas de ventas, inventario y talleres.
 */
public class ReportDaoImpl implements IReportDao {
    private final Connection connection;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     *
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    public ReportDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Obtiene estadísticas de ventas en función del período especificado.
     *
     * @param period el período (por ejemplo, semanal o mensual) para obtener las estadísticas.
     * @return una lista de estadísticas que contiene las ventas agrupadas por fecha.
     */
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

    /**
     * Mapea un objeto {@link ResultSet} a una instancia de {@link Statistic}.
     *
     * @param rs el {@link ResultSet} obtenido de la consulta.
     * @param type el tipo de estadística (por ejemplo, ventas, inventario).
     * @return una instancia de {@link Statistic} con los datos del ResultSet.
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
     */
    private Statistic mapStatistic(ResultSet rs, String type) throws SQLException {
        Statistic stat = new Statistic();
        stat.setStatisticType(type);
        stat.setNumericValue(rs.getDouble("value"));
        stat.setStartDate(rs.getDate("date"));
        return stat;
    }

    /**
     * Obtiene la fecha inicial basada en el período especificado.
     *
     * @param period el período (por ejemplo, semanal o mensual).
     * @return la fecha inicial como cadena de texto.
     */
    private String getStartDate(String period) {
        switch (period) {
            case "Weekly": return "DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
            case "Monthly": return "DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";
            default: return "CURDATE()";
        }
    }

    /**
     * Obtiene la fecha final basada en el período especificado.
     *
     * @param period el período (por ejemplo, semanal o mensual).
     * @return la fecha final como cadena de texto.
     */
    private String getEndDate(String period) {
        return "CURDATE()";
    }

    /**
     * Obtiene estadísticas de inventario, como productos con stock bajo.
     *
     * @return una lista de estadísticas que contiene los productos con stock bajo.
     */
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

    /**
     * Obtiene estadísticas del taller, como citas programadas agrupadas por estado.
     *
     * @param period el período para obtener las estadísticas.
     * @return una lista de estadísticas agrupadas por el estado de las citas.
     */
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