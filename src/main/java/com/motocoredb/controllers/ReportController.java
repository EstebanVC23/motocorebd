package com.motocoredb.controllers;

import com.motocoredb.models.Statistic;
import com.motocoredb.services.ReportService;
import java.util.List;

public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    public List<Statistic> getSalesStatistics(String period) {
        return reportService.getSalesStatistics(period);
    }

    public List<Statistic> getInventoryStatistics() {
        return reportService.getInventoryStatistics();
    }

    public List<Statistic> getWorkshopStatistics(String period) {
        return reportService.getWorkshopStatistics(period);
    }
}