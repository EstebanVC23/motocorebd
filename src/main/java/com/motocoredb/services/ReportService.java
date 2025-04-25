package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IReportDao;
import com.motocoredb.models.Statistic;
import java.util.List;

public class ReportService {
    private final IReportDao reportDao;
    
    public ReportService(IReportDao reportDao) {
        this.reportDao = reportDao;
    }
    
    public List<Statistic> getSalesStatistics(String period) {
        return reportDao.getSalesStats(period);
    }
    
    public List<Statistic> getInventoryStatistics() {
        return reportDao.getInventoryStats();
    }
    
    public List<Statistic> getWorkshopStatistics(String period) {
        return reportDao.getWorkshopStats(period);
    }
}