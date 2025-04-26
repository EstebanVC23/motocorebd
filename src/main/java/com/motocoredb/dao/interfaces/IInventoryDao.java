package com.motocoredb.dao.interfaces;

import com.motocoredb.models.InventoryMovement;
import com.motocoredb.models.Product;
import java.util.List;

public interface IInventoryDao {
    /**
     * Obtiene todos los movimientos de inventario para un producto específico
     * @param productId ID del producto
     * @return Lista de movimientos ordenados por fecha descendente
     */
    List<InventoryMovement> getMovements(int productId);
    
    /**
     * Ajusta el inventario de un producto y registra el movimiento
     * @param productId ID del producto
     * @param quantity Cantidad a ajustar (positiva o negativa)
     * @param notes Notas explicativas del ajuste
     * @param userId ID del usuario que realiza el ajuste
     * @return true si la operación fue exitosa
     */
    boolean adjustInventory(int productId, int quantity, String notes, int userId);
    
    /**
     * Obtiene productos con stock por debajo del mínimo requerido
     * @return Lista de productos con stock bajo
     */
    List<Product> getLowStockProducts();
    
    /**
     * Registra un movimiento de inventario
     * @param movement Objeto InventoryMovement con los datos del movimiento
     * @return true si el registro fue exitoso
     */
    boolean recordMovement(InventoryMovement movement);
    
    /**
     * Obtiene el stock actual de un producto
     * @param productId ID del producto
     * @return Cantidad disponible en inventario
     */
    int getCurrentStock(int productId);
}