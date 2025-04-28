package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IPurchaseDao;
import com.motocoredb.models.Purchase;
import com.motocoredb.models.PurchaseDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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

    public List<Purchase> getPurchasesByDateRange(Date startDate, Date endDate) {
        try {
            return purchaseDao.findByDateRange(startDate, endDate); // Delegamos la consulta al DAO
        } catch (Exception e) {
            System.err.println("Error al obtener compras por rango de fechas: " + e.getMessage());
            return new ArrayList<>(); // Retorna una lista vacía en caso de error
        }
    }

    public String getSupplierNameByPurchaseId(int purchaseId) {
        return purchaseDao.getSupplierNameByPurchaseId(purchaseId);
    }

    public List<Purchase> getAllPurchases() {
        return purchaseDao.listAllPurchases(); // Método en el DAO para consultar todas las compras
    }
}