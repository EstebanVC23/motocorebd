package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Statistic;
import java.util.List;

/**
 * Interfaz para gestionar las estadísticas del panel de control.
 * Define métodos para obtener estadísticas clave como ventas, citas y alertas de inventario.
 */
public interface IDashboardDao {

    /**
     * Obtiene las estadísticas de ventas del día actual.
     *
     * @return una lista de estadísticas con la suma total de las ventas del día actual.
     */
    List<Statistic> getTodaySales();

    /**
     * Obtiene las estadísticas de ventas del mes actual.
     *
     * @return una lista de estadísticas con la suma total de las ventas del mes actual.
     */
    List<Statistic> getMonthlySales();

    /**
     * Obtiene las estadísticas de citas programadas para el día actual.
     *
     * @return una lista de estadísticas con la cantidad de citas programadas para el día actual.
     */
    List<Statistic> getTodayAppointments();

    /**
     * Obtiene las alertas de bajo inventario.
     *
     * @return una lista de estadísticas con la cantidad de productos con stock por debajo del mínimo.
     */
    List<Statistic> getLowStockAlerts();
}