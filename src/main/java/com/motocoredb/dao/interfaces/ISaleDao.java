package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;
import java.util.List;

public interface ISaleDao {
    boolean createSale(Sale sale, List<SaleDetail> details);
    List<Sale> getByDateRange(String startDate, String endDate);
    boolean cancelSale(int saleId);
    List<SaleDetail> getDetails(int saleId);
    boolean updateSale(Sale sale);
    Sale getSaleById(int saleId);
    SaleDetail getSaleDetailBySaleId(int saleId);
}