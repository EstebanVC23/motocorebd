package com.motocoredb.services;

import com.motocoredb.dao.interfaces.ISaleDao;
import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;

/**
 * Clase de servicio para gestionar las operaciones relacionadas con ventas.
 */
public class SaleService {

    private final ISaleDao saleDao;

    /**
     * Constructor de SaleService.
     *
     * @param saleDao Instancia del DAO de ventas.
     */
    public SaleService(ISaleDao saleDao) {
        this.saleDao = saleDao;
    }

    /**
     * Crea una nueva venta junto con sus detalles.
     *
     * @param sale     La venta principal.
     * @param details  Lista de detalles de la venta.
     * @return true si la operación fue exitosa, false de lo contrario.
     */
    public boolean createSale(Sale sale, List<SaleDetail> details) {
        if (sale == null || details == null || details.isEmpty()) {
            throw new IllegalArgumentException("La venta y los detalles no deben ser nulos o vacíos");
        }
        try {
            return saleDao.createSale(sale, details);
        } catch (Exception e) {
            System.err.println("Error al crear la venta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene una lista de ventas dentro de un rango de fechas (usando Strings como argumentos).
     *
     * @param startDate Fecha de inicio en formato yyyy-MM-dd.
     * @param endDate   Fecha de fin en formato yyyy-MM-dd.
     * @return Lista de ventas dentro del rango de fechas.
     */
    public List<Sale> getSalesByDateRangeAsString(String startDate, String endDate) {
        try {
            if ((startDate == null || startDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
                return saleDao.getByDateRange(null, null);
            }
            return saleDao.getByDateRange(startDate, endDate);
        } catch (Exception e) {
            System.err.println("Error al obtener ventas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene una lista de ventas dentro de un rango de fechas (usando Dates como argumentos).
     *
     * @param startDate Fecha de inicio.
     * @param endDate   Fecha de fin.
     * @return Lista de ventas dentro del rango de fechas.
     */
    public List<Sale> getSalesByDateRange(Date startDate, Date endDate) {
        try {
            return saleDao.findByDateRange(startDate, endDate);
        } catch (Exception e) {
            System.err.println("Error al obtener las ventas por rango de fechas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Cancela una venta específica.
     *
     * @param saleId ID de la venta a cancelar.
     * @return true si la operación fue exitosa, false de lo contrario.
     */
    public boolean cancelSale(int saleId) {
        if (saleId <= 0) {
            throw new IllegalArgumentException("El ID de la venta debe ser mayor que cero");
        }
        try {
            return saleDao.cancelSale(saleId);
        } catch (Exception e) {
            System.err.println("Error al cancelar la venta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene los detalles de una venta específica.
     *
     * @param saleId ID de la venta.
     * @return Lista de detalles de la venta.
     */
    public List<SaleDetail> getSaleDetails(int saleId) {
        if (saleId <= 0) {
            throw new IllegalArgumentException("El ID de la venta debe ser mayor que cero");
        }
        try {
            return saleDao.getDetails(saleId);
        } catch (Exception e) {
            System.err.println("Error al obtener los detalles de la venta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Actualiza una venta específica.
     *
     * @param sale La venta actualizada.
     * @return true si la operación fue exitosa, false de lo contrario.
     */
    public boolean updateSale(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("La venta no debe ser nula");
        }
        try {
            return saleDao.updateSale(sale);
        } catch (Exception e) {
            System.err.println("Error al actualizar la venta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene una venta específica por su ID.
     *
     * @param saleId ID de la venta.
     * @return La venta obtenida.
     */
    public Sale getSaleById(int saleId) {
        if (saleId <= 0) {
            throw new IllegalArgumentException("El ID de la venta debe ser mayor que cero");
        }
        try {
            return saleDao.getSaleById(saleId);
        } catch (Exception e) {
            System.err.println("Error al obtener la venta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene un detalle específico de una venta.
     *
     * @param saleId ID de la venta.
     * @return Detalle de la venta.
     */
    public SaleDetail getSaleDetailBySaleId(int saleId) {
        return saleDao.getSaleDetailBySaleId(saleId);
    }

    /**
     * Obtiene los empleados principales por ventas.
     *
     * @return Mapa con nombres de empleados y la cantidad de productos vendidos.
     */
    public Map<String, Integer> getTopEmployees() {
        try {
            return saleDao.getTopEmployees();
        } catch (Exception e) {
            System.err.println("Error al obtener los empleados principales: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Obtiene la cantidad de productos vendidos.
     *
     * @return Mapa con nombres de productos y cantidades vendidas.
     */
    public Map<String, Integer> getSoldProducts() {
        try {
            return saleDao.getSoldProducts();
        } catch (Exception e) {
            System.err.println("Error al obtener productos vendidos: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Obtiene las ventas totales por tiempo.
     *
     * @return Mapa con el mes y el total de ventas.
     */
    public Map<java.time.Month, Double> getSalesOverTime() {
        try {
            return saleDao.getSalesOverTime();
        } catch (Exception e) {
            System.err.println("Error al obtener ventas por tiempo: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Obtiene los ingresos totales.
     *
     * @return Total de ingresos.
     */
    public double getTotalIncome() {
        try {
            return saleDao.getTotalIncome();
        } catch (Exception e) {
            System.err.println("Error al obtener ingresos totales: " + e.getMessage());
            return 0.0;
        }
    }
}