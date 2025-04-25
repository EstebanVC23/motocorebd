package com.motocoredb.dao.interfaces;

import com.motocoredb.models.WorkshopService;
import java.util.List;

public interface IServiceDao {
    boolean createService(WorkshopService service);
    WorkshopService getById(int id);
    List<WorkshopService> listAll();
    boolean updateService(WorkshopService service);
    boolean changeStatus(int id, String status);
}