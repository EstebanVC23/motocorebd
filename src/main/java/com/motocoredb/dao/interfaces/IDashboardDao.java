package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Statistic;
import java.util.List;

public interface IDashboardDao {
    List<Statistic> getTodaySales();
    List<Statistic> getMonthlySales();
    List<Statistic> getTodayAppointments();
    List<Statistic> getLowStockAlerts();
}