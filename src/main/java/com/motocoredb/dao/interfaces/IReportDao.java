package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Statistic;
import java.util.List;

/**
 * Interfaz para gestionar los informes y estadísticas en la base de datos.
 * Define métodos para generar estadísticas de ventas, inventario y talleres.
 */
public interface IReportDao {

    /**
     * Obtiene las estadísticas de ventas en función del período especificado.
     *
     * @param period el período para el que se desean las estadísticas (por ejemplo, semanal o mensual).
     * @return una lista de estadísticas agrupadas por fecha.
     */
    List<Statistic> getSalesStats(String period);

    /**
     * Obtiene las estadísticas de inventario, como productos con stock bajo.
     *
     * @return una lista de estadísticas relacionadas con el estado del inventario.
     */
    List<Statistic> getInventoryStats();

    /**
     * Obtiene estadísticas del taller, agrupadas por estado de las citas y período especificado.
     *
     * @param period el período para el que se desean las estadísticas (por ejemplo, semanal o mensual).
     * @return una lista de estadísticas agrupadas por estado.
     */
    List<Statistic> getWorkshopStats(String period);
}