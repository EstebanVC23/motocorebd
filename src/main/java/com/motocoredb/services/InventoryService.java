package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IInventoryDao;
import com.motocoredb.models.InventoryMovement;
import com.motocoredb.models.Product;

import java.util.List;
import java.util.Map;

public class InventoryService {
    private final IInventoryDao inventoryDao;
    
    public InventoryService(IInventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
    /**
     * Obtiene los movimientos de inventario de un producto
     * @param productId ID del producto
     * @return Lista de movimientos
     */
    public List<InventoryMovement> getInventoryMovements(int productId) {
        return inventoryDao.getMovements(productId);
    }
    
    /**
     * Realiza un ajuste de inventario para un producto
     * @param productId ID del producto
     * @param quantity Cantidad a ajustar (positiva o negativa)
     * @param notes Notas explicativas del ajuste
     * @param userId ID del usuario que realiza el ajuste
     * @return true si la operación fue exitosa
     */
    public boolean makeInventoryAdjustment(int productId, int quantity, String notes, int userId) {
        return inventoryDao.adjustInventory(productId, quantity, notes, userId);
    }
    
    /**
     * Obtiene la lista de productos con stock bajo
     * @return Lista de productos con stock bajo
     */
    public List<Product> getLowStockProducts() {
        return inventoryDao.getLowStockProducts();
    }
    
    /**
     * Obtiene un mapa de productos con stock bajo
     * @return Mapa con nombres de productos y su stock actual
     */
    public Map<String, Integer> getLowStockProductsMap() {
        return inventoryDao.getLowStockProductsMap();
    }
    
    /**
     * Registra un movimiento de inventario genérico
     * @param movement Objeto InventoryMovement con los datos del movimiento
     * @return true si el registro fue exitoso
     */
    public boolean recordMovement(InventoryMovement movement) {
        return inventoryDao.recordMovement(movement);
    }
    
    /**
     * Obtiene el stock actual de un producto
     * @param productId ID del producto
     * @return Cantidad disponible en inventario
     */
    public int getCurrentStock(int productId) {
        return inventoryDao.getCurrentStock(productId);
    }
    
    /**
     * Obtiene el total de gastos por compras de inventario
     * @return Total de gastos por compras de inventario
     */
    public double getTotalIn() {
        return inventoryDao.getTotalIn();
    }
    
    /**
     * Obtiene el total de salidas de inventario al costo
     * @return Total de salidas de inventario
     */
    public double getTotalOut() {
        return inventoryDao.getTotalOut();
    }
    
    /**
     * Registra un movimiento de entrada de inventario (compra)
     * @param productId ID del producto
     * @param quantity Cantidad
     * @param userId ID del usuario que registra
     * @param referenceId ID de referencia (compra)
     * @return true si se registró correctamente
     */
    public boolean registerInMovement(int productId, int quantity, int userId, int referenceId) {
        return inventoryDao.registerInMovement(productId, quantity, userId, referenceId);
    }
    
    /**
     * Registra un movimiento de salida de inventario (venta)
     * @param productId ID del producto
     * @param quantity Cantidad
     * @param userId ID del usuario que registra
     * @param referenceId ID de referencia (venta)
     * @return true si se registró correctamente
     */
    public boolean registerOutMovement(int productId, int quantity, int userId, int referenceId) {
        return inventoryDao.registerOutMovement(productId, quantity, userId, referenceId);
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public void closeConnection() {
        inventoryDao.closeConnection();
    }

    /**
     * Obtiene el valor total de compras de inventario.
     * @return El valor total de compras de inventario.
     */
    public double getTotalPurchaseValue() {
        return inventoryDao.getTotalPurchaseValue(); // Delegar la consulta al DAO
    }
}