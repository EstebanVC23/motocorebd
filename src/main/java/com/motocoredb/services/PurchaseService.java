package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IPurchaseDao;
import com.motocoredb.models.Purchase;
import com.motocoredb.models.PurchaseDetail;
import java.util.List;

public class PurchaseService {
    private final IPurchaseDao purchaseDao;

    public PurchaseService(IPurchaseDao purchaseDao) {
        this.purchaseDao = purchaseDao;
    }

    public boolean createPurchase(Purchase purchase, List<PurchaseDetail> details) {
        return purchaseDao.createPurchase(purchase, details);
    }

    public List<Purchase> getPurchasesByDateRange(String startDate, String endDate) {
        return purchaseDao.getByDateRange(startDate, endDate);
    }

    public boolean cancelPurchase(int purchaseId) {
        return purchaseDao.cancelPurchase(purchaseId);
    }

    public List<PurchaseDetail> getPurchaseDetails(int purchaseId) {
        return purchaseDao.getDetails(purchaseId);
    }
}