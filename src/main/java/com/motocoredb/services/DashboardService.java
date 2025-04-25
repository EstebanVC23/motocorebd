package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IDashboardDao;
import com.motocoredb.models.Statistic;
import java.util.List;

public class DashboardService {
    private final IDashboardDao dashboardDao;
    
    public DashboardService(IDashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }
    
    public List<Statistic> getTodaySales() {
        return dashboardDao.getTodaySales();
    }
    
    public List<Statistic> getMonthlySales() {
        return dashboardDao.getMonthlySales();
    }
    
    public List<Statistic> getTodayAppointments() {
        return dashboardDao.getTodayAppointments();
    }
    
    public List<Statistic> getLowStockAlerts() {
        return dashboardDao.getLowStockAlerts();
    }
}