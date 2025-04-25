package com.motocoredb.controllers;

import com.motocoredb.models.Statistic;
import com.motocoredb.services.DashboardService;
import java.util.List;

public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    public List<Statistic> getTodaySales() {
        return dashboardService.getTodaySales();
    }

    public List<Statistic> getMonthlySales() {
        return dashboardService.getMonthlySales();
    }

    public List<Statistic> getTodayAppointments() {
        return dashboardService.getTodayAppointments();
    }

    public List<Statistic> getLowStockAlerts() {
        return dashboardService.getLowStockAlerts();
    }
}