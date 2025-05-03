package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.time.Month;

/**
 * Interfaz para gestionar ventas en la base de datos.
 * Define métodos para crear, consultar, actualizar y analizar información relacionada con las ventas.
 */
public interface ISaleDao {

    /**
     * Crea una nueva venta junto con los detalles asociados.
     *
     * @param sale la instancia de {@link Sale} a insertar.
     * @param details la lista de detalles asociados a la venta.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean createSale(Sale sale, List<SaleDetail> details);

    /**
     * Obtiene una lista de ventas realizadas en un rango de fechas específico.
     *
     * @param startDate la fecha de inicio del rango.
     * @param endDate la fecha de fin del rango.
     * @return una lista de instancias de {@link Sale} en el rango de fechas.
     */
    List<Sale> getByDateRange(String startDate, String endDate);

    /**
     * Cancela una venta especificada por su identificador.
     *
     * @param saleId el identificador de la venta.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean cancelSale(int saleId);

    /**
     * Obtiene los detalles de una venta específica.
     *
     * @param saleId el identificador de la venta.
     * @return una lista de instancias de {@link SaleDetail} asociadas a la venta.
     */
    List<SaleDetail> getDetails(int saleId);

    /**
     * Actualiza los datos de una venta en la base de datos.
     *
     * @param sale la instancia de {@link Sale} con los datos actualizados.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean updateSale(Sale sale);

    /**
     * Obtiene una venta específica por su identificador.
     *
     * @param saleId el identificador de la venta.
     * @return una instancia de {@link Sale} si se encuentra; null en caso contrario.
     */
    Sale getSaleById(int saleId);

    /**
     * Obtiene un detalle de venta específica por el identificador de la venta.
     *
     * @param saleId el identificador de la venta.
     * @return una instancia de {@link SaleDetail} si se encuentra; null en caso contrario.
     */
    SaleDetail getSaleDetailBySaleId(int saleId);

    /**
     * Encuentra una lista de ventas realizadas entre dos fechas específicas.
     *
     * @param startDate la fecha de inicio.
     * @param endDate la fecha de fin.
     * @return una lista de instancias de {@link Sale} en el rango de fechas.
     */
    List<Sale> findByDateRange(Date startDate, Date endDate);

    /**
     * Obtiene el ingreso total generado por todas las ventas.
     *
     * @return el ingreso total como un valor decimal.
     */
    double getTotalIncome();

    /**
     * Obtiene los empleados más destacados con base en sus ventas.
     *
     * @return un mapa donde las claves son nombres de empleados y los valores son sus totales de ventas.
     */
    Map<String, Integer> getTopEmployees();

    /**
     * Obtiene los productos más vendidos.
     *
     * @return un mapa donde las claves son nombres de productos y los valores son las cantidades vendidas.
     */
    Map<String, Integer> getSoldProducts();

    /**
     * Obtiene las estadísticas de ventas a lo largo del tiempo.
     *
     * @return un mapa donde las claves son meses y los valores son los ingresos correspondientes.
     */
    Map<Month, Double> getSalesOverTime();
}