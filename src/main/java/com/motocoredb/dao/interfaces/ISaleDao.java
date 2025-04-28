package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;
import java.util.List;
import java.util.Map;
import java.util.Date;

public interface ISaleDao {
    boolean createSale(Sale sale, List<SaleDetail> details);
    List<Sale> getByDateRange(String startDate, String endDate);
    boolean cancelSale(int saleId);
    List<SaleDetail> getDetails(int saleId);
    boolean updateSale(Sale sale);
    Sale getSaleById(int saleId);
    SaleDetail getSaleDetailBySaleId(int saleId);
    List<Sale> findByDateRange(Date startDate, Date endDate);
    double getTotalIncome();
    Map<String, Integer> getTopEmployees();
    Map<String, Integer> getSoldProducts();
    Map<java.time.Month, Double> getSalesOverTime();
}