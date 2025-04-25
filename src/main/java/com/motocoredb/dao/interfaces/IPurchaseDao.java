package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Purchase;
import com.motocoredb.models.PurchaseDetail;
import java.util.List;

public interface IPurchaseDao {
    boolean createPurchase(Purchase purchase, List<PurchaseDetail> details);
    List<Purchase> getByDateRange(String startDate, String endDate);
    boolean cancelPurchase(int purchaseId);
    List<PurchaseDetail> getDetails(int purchaseId);
}