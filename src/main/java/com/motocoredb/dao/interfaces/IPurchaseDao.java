package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Purchase;
import com.motocoredb.models.PurchaseDetail;
import java.util.List;
import java.util.Date;

/**
 * Interfaz para gestionar las compras en la base de datos.
 * Define métodos para crear, consultar, cancelar y listar detalles relacionados con las compras.
 */
public interface IPurchaseDao {

    /**
     * Crea una nueva compra junto con sus detalles en la base de datos.
     *
     * @param purchase la instancia de {@link Purchase} a insertar.
     * @param details la lista de detalles asociados a la compra.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean createPurchase(Purchase purchase, List<PurchaseDetail> details);

    /**
     * Obtiene una lista de compras realizadas en un rango de fechas especificado.
     *
     * @param startDate la fecha de inicio del rango.
     * @param endDate la fecha de fin del rango.
     * @return una lista de instancias de {@link Purchase} en el rango de fechas.
     */
    List<Purchase> getByDateRange(String startDate, String endDate);

    /**
     * Cancela una compra especificada por su identificador.
     *
     * @param purchaseId el identificador de la compra.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean cancelPurchase(int purchaseId);

    /**
     * Obtiene los detalles de una compra específica.
     *
     * @param purchaseId el identificador de la compra.
     * @return una lista de instancias de {@link PurchaseDetail} asociadas a la compra.
     */
    List<PurchaseDetail> getDetails(int purchaseId);

    /**
     * Encuentra una lista de compras realizadas entre dos fechas específicas.
     *
     * @param startDate la fecha de inicio.
     * @param endDate la fecha de fin.
     * @return una lista de instancias de {@link Purchase} en el rango de fechas.
     */
    List<Purchase> findByDateRange(Date startDate, Date endDate);

    /**
     * Obtiene el nombre del proveedor asociado a una compra específica.
     *
     * @param purchaseId el identificador de la compra.
     * @return el nombre del proveedor asociado, o null si no se encuentra.
     */
    String getSupplierNameByPurchaseId(int purchaseId);

    /**
     * Lista todas las compras registradas en la base de datos.
     *
     * @return una lista de todas las instancias de {@link Purchase}.
     */
    List<Purchase> listAllPurchases();
}