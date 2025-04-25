package com.motocoredb.controllers;

import com.motocoredb.models.InventoryMovement;
import com.motocoredb.models.Product;
import com.motocoredb.services.InventoryService;
import java.util.List;

public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public List<InventoryMovement> getInventoryMovements(int productId) {
        return inventoryService.getInventoryMovements(productId);
    }

    public boolean makeInventoryAdjustment(int productId, int quantity, String notes) {
        return inventoryService.makeInventoryAdjustment(productId, quantity, notes);
    }

    public List<Product> getLowStockProducts() {
        return inventoryService.getLowStockProducts();
    }
}