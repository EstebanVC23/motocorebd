package com.motocoredb.controllers;

import com.motocoredb.models.Purchase;
import com.motocoredb.models.PurchaseDetail;
import com.motocoredb.services.PurchaseService;
import java.util.List;

public class PurchaseController {
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    public boolean createPurchase(Purchase purchase, List<PurchaseDetail> details) {
        return purchaseService.createPurchase(purchase, details);
    }

    public List<Purchase> getPurchasesByDateRange(String startDate, String endDate) {
        return purchaseService.getPurchasesByDateRange(startDate, endDate);
    }

    public boolean cancelPurchase(int purchaseId) {
        return purchaseService.cancelPurchase(purchaseId);
    }

    public List<PurchaseDetail> getPurchaseDetails(int purchaseId) {
        return purchaseService.getPurchaseDetails(purchaseId);
    }
}