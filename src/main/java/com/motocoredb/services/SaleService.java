package com.motocoredb.services;

import com.motocoredb.dao.interfaces.ISaleDao;
import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;

import java.util.ArrayList;
import java.util.List;

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
     * Obtiene una lista de ventas dentro de un rango de fechas.
     *
     * @param startDate Fecha de inicio en formato yyyy-MM-dd.
     * @param endDate   Fecha de fin en formato yyyy-MM-dd.
     * @return Lista de ventas dentro del rango de fechas.
     */
    public List<Sale> getSalesByDateRange(String startDate, String endDate) {
        try {
            if ((startDate == null || startDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
                // Si las fechas son nulas o vacías, devuelve todas las ventas sin filtro
                return saleDao.getByDateRange(null, null);
            }
            return saleDao.getByDateRange(startDate, endDate);
        } catch (Exception e) {
            System.err.println("Error al obtener ventas: " + e.getMessage());
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
            return saleDao.updateSale(sale); // Llama al método del DAO para actualizar la venta
        } catch (Exception e) {
            System.err.println("Error al actualizar la venta: " + e.getMessage());
            return false;
        }
    }

    public Sale getSaleById(int saleId) {
        if (saleId <= 0) {
            throw new IllegalArgumentException("El ID de la venta debe ser mayor que cero");
        }
        try {
            return saleDao.getSaleById(saleId); // Llama al DAO para obtener la venta
        } catch (Exception e) {
            System.err.println("Error al obtener la venta: " + e.getMessage());
            return null;
        }
    }

    public SaleDetail getSaleDetailBySaleId(int saleId) {
        return saleDao.getSaleDetailBySaleId(saleId);
    }
}