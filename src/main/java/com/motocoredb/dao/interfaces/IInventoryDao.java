package com.motocoredb.dao.interfaces;

import com.motocoredb.models.InventoryMovement;
import com.motocoredb.models.Product;
import java.util.List;

public interface IInventoryDao {
    List<InventoryMovement> getMovements(int productId);
    boolean adjustInventory(int productId, int quantity, String notes);
    List<Product> getLowStockProducts();
}