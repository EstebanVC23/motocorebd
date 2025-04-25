package com.motocoredb.controllers;

import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;
import com.motocoredb.services.SaleService;
import java.util.List;

public class SaleController {
    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    public boolean createSale(Sale sale, List<SaleDetail> details) {
        return saleService.createSale(sale, details);
    }

    public List<Sale> getSalesByDateRange(String startDate, String endDate) {
        return saleService.getSalesByDateRange(startDate, endDate);
    }

    public boolean cancelSale(int saleId) {
        return saleService.cancelSale(saleId);
    }

    public List<SaleDetail> getSaleDetails(int saleId) {
        return saleService.getSaleDetails(saleId);
    }
}