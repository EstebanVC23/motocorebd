package com.motocoredb.services;

import com.motocoredb.dao.interfaces.ISaleDao;
import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;
import java.util.List;

public class SaleService {
    private final ISaleDao saleDao;
    
    public SaleService(ISaleDao saleDao) {
        this.saleDao = saleDao;
    }
    
    public boolean createSale(Sale sale, List<SaleDetail> details) {
        return saleDao.createSale(sale, details);
    }
    
    public List<Sale> getSalesByDateRange(String startDate, String endDate) {
        return saleDao.getByDateRange(startDate, endDate);
    }
    
    public boolean cancelSale(int saleId) {
        return saleDao.cancelSale(saleId);
    }
    
    public List<SaleDetail> getSaleDetails(int saleId) {
        return saleDao.getDetails(saleId);
    }
}