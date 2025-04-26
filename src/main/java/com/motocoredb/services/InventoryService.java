package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IInventoryDao;
import com.motocoredb.models.InventoryMovement;
import com.motocoredb.models.Product;
import java.util.List;

public class InventoryService {
    private final IInventoryDao inventoryDao;
    
    public InventoryService(IInventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
    public List<InventoryMovement> getInventoryMovements(int productId) {
        return inventoryDao.getMovements(productId);
    }
    
    public boolean makeInventoryAdjustment(int productId, int quantity, String notes, int userId) {
        return inventoryDao.adjustInventory(productId, quantity, notes, userId);
    }
    
    public List<Product> getLowStockProducts() {
        return inventoryDao.getLowStockProducts();
    }
    
    public boolean recordMovement(InventoryMovement movement) {
        return inventoryDao.recordMovement(movement);
    }
    
    public int getCurrentStock(int productId) {
        return inventoryDao.getCurrentStock(productId);
    }
}