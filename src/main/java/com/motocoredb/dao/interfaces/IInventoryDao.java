package com.motocoredb.dao.interfaces;

import com.motocoredb.models.InventoryMovement;
import com.motocoredb.models.Product;
import java.util.List;
import java.util.Map;

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
    
    /**
     * Obtiene el total de gastos por compras de inventario
     * @return Total de gastos por compras de inventario
     */
    double getTotalIn();
    
    /**
     * Obtiene el total de salidas de inventario al costo
     * @return Total de salidas de inventario
     */
    double getTotalOut();
    
    /**
     * Registra un movimiento de entrada de inventario (compra)
     * @param productId ID del producto
     * @param quantity Cantidad
     * @param userId ID del usuario que registra
     * @param referenceId ID de referencia (compra)
     * @return true si se registró correctamente
     */
    boolean registerInMovement(int productId, int quantity, int userId, int referenceId);
    
    /**
     * Registra un movimiento de salida de inventario (venta)
     * @param productId ID del producto
     * @param quantity Cantidad
     * @param userId ID del usuario que registra
     * @param referenceId ID de referencia (venta)
     * @return true si se registró correctamente
     */
    boolean registerOutMovement(int productId, int quantity, int userId, int referenceId);
    
    /**
     * Obtiene un mapa de productos con stock bajo
     * @return Mapa con nombres de productos y su stock actual
     */
    Map<String, Integer> getLowStockProductsMap();
    
    /**
     * Cierra la conexión a la base de datos
     */
    void closeConnection();

    /**
    * Obtiene el valor total de las compras en el inventario.
    * @return El valor total de compras.
    */
    double getTotalPurchaseValue();
}