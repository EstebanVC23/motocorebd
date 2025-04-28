package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Purchase;
import com.motocoredb.models.PurchaseDetail;
import java.util.List;
import java.util.Date;

public interface IPurchaseDao {
    boolean createPurchase(Purchase purchase, List<PurchaseDetail> details);
    List<Purchase> getByDateRange(String startDate, String endDate);
    boolean cancelPurchase(int purchaseId);
    List<PurchaseDetail> getDetails(int purchaseId);
    public List<Purchase> findByDateRange(Date startDate, Date endDate);
    String getSupplierNameByPurchaseId(int purchaseId);
    List<Purchase> listAllPurchases(); // Método para listar todas las compras
}