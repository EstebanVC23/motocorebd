package com.motocoredb.controllers;

import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;
import com.motocoredb.services.SaleService;

import java.util.List;
import java.util.Date;

public class SaleController {
    private final SaleService saleService;

    /**
     * Constructor de SaleController.
     *
     * @param saleService Instancia del servicio de ventas.
     */
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    /**
     * Crea una nueva venta junto con sus detalles.
     *
     * @param sale    La venta principal.
     * @param details Lista de detalles de la venta.
     * @return true si la operación fue exitosa, false de lo contrario.
     */
    public boolean createSale(Sale sale, List<SaleDetail> details) {
        return saleService.createSale(sale, details);
    }

    /**
     * Obtiene una lista de ventas dentro de un rango de fechas (usando Strings).
     *
     * @param startDate Fecha de inicio en formato yyyy-MM-dd.
     * @param endDate   Fecha de fin en formato yyyy-MM-dd.
     * @return Lista de ventas dentro del rango de fechas.
     */
    public List<Sale> getSalesByDateRangeAsString(String startDate, String endDate) {
        return saleService.getSalesByDateRangeAsString(startDate, endDate);
    }

    /**
     * Obtiene una lista de ventas dentro de un rango de fechas (usando Dates).
     *
     * @param startDate Fecha de inicio.
     * @param endDate   Fecha de fin.
     * @return Lista de ventas dentro del rango de fechas.
     */
    public List<Sale> getSalesByDateRange(Date startDate, Date endDate) {
        return saleService.getSalesByDateRange(startDate, endDate);
    }

    /**
     * Cancela una venta específica.
     *
     * @param saleId ID de la venta a cancelar.
     * @return true si la operación fue exitosa, false de lo contrario.
     */
    public boolean cancelSale(int saleId) {
        return saleService.cancelSale(saleId);
    }

    /**
     * Obtiene los detalles de una venta específica.
     *
     * @param saleId ID de la venta.
     * @return Lista de detalles de la venta.
     */
    public List<SaleDetail> getSaleDetails(int saleId) {
        return saleService.getSaleDetails(saleId);
    }
}