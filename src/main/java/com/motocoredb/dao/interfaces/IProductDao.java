package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Product;
import java.util.List;

/**
 * Interfaz para gestionar productos en la base de datos.
 * Define métodos para crear, leer, actualizar y gestionar el inventario de productos.
 */
public interface IProductDao {

    /**
     * Crea un nuevo producto en la base de datos.
     *
     * @param product la instancia de {@link Product} a insertar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean createProduct(Product product);

    /**
     * Obtiene un producto por su identificador.
     *
     * @param id el identificador del producto.
     * @return una instancia de {@link Product} si se encuentra; null en caso contrario.
     */
    Product getById(int id);

    /**
     * Obtiene una lista de todos los productos registrados en la base de datos.
     *
     * @return una lista de instancias de {@link Product}.
     */
    List<Product> listAll();

    /**
     * Actualiza los datos de un producto en la base de datos.
     *
     * @param product la instancia de {@link Product} con los datos actualizados.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean updateProduct(Product product);

    /**
     * Actualiza el stock de un producto específico.
     *
     * @param productId el identificador del producto.
     * @param quantity la cantidad a actualizar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean updateStock(int productId, int quantity);

    /**
     * Cambia el estado de un producto especificado.
     *
     * @param productId el identificador del producto.
     * @param status el nuevo estado que se asignará al producto.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean changeStatus(int productId, String status);

    /**
     * Reduce el stock de un producto específico en la base de datos.
     *
     * @param productId el identificador del producto.
     * @param quantity la cantidad a reducir del stock.
     */
    void reduceStock(int productId, int quantity);

    /**
     * Incrementa el stock de un producto específico en la base de datos.
     *
     * @param productId el identificador del producto.
     * @param quantity la cantidad a incrementar en el stock.
     */
    void increaseStock(int productId, int quantity);

    /**
     * Encuentra los productos con stock bajo en la base de datos.
     *
     * @return una lista de productos cuyo stock está por debajo del mínimo establecido.
     */
    List<Product> findLowStockProducts();
}