package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Statistic;
import java.util.List;

public interface IReportDao {
    List<Statistic> getSalesStats(String period);
    List<Statistic> getInventoryStats();
    List<Statistic> getWorkshopStats(String period);
}