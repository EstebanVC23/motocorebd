package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IPurchaseDao;
import com.motocoredb.models.Purchase;
import com.motocoredb.models.PurchaseDetail;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementación de la interfaz {@link IPurchaseDao} para gestionar las compras en la base de datos.
 * Proporciona métodos para crear, leer, cancelar y obtener detalles de compras.
 */
public class PurchaseDaoImpl implements IPurchaseDao {
    private final Connection connection;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     *
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    public PurchaseDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Crea una nueva compra junto con los detalles correspondientes.
     *
     * @param purchase la instancia de {@link Purchase} a insertar.
     * @param details la lista de detalles de la compra.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean createPurchase(Purchase purchase, List<PurchaseDetail> details) {
        String purchaseSql = "INSERT INTO Purchases (invoiceNumber, supplierId, userId, subtotal, tax, total, status, notes) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String detailSql = "INSERT INTO PurchaseDetails (purchaseId, productId, quantity, unitPrice, subtotal) " +
                           "VALUES (?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement purchaseStmt = connection.prepareStatement(purchaseSql, Statement.RETURN_GENERATED_KEYS)) {
                purchaseStmt.setString(1, purchase.getInvoiceNumber());
                purchaseStmt.setInt(2, purchase.getSupplierId());
                purchaseStmt.setInt(3, purchase.getUserId());
                purchaseStmt.setDouble(4, purchase.getSubtotal());
                purchaseStmt.setDouble(5, purchase.getTax());
                purchaseStmt.setDouble(6, purchase.getTotal());
                purchaseStmt.setString(7, purchase.getStatus());
                purchaseStmt.setString(8, purchase.getNotes());

                int affectedRows = purchaseStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Error al insertar en Purchases: no se generó ninguna fila.");
                }

                int purchaseId;
                try (ResultSet generatedKeys = purchaseStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        purchaseId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Error al obtener el ID generado para la compra.");
                    }
                }

                try (PreparedStatement detailStmt = connection.prepareStatement(detailSql)) {
                    for (PurchaseDetail detail : details) {
                        detailStmt.setInt(1, purchaseId);
                        detailStmt.setInt(2, detail.getProductId());
                        detailStmt.setInt(3, detail.getQuantity());
                        detailStmt.setDouble(4, detail.getUnitPrice());
                        detailStmt.setDouble(5, detail.getSubtotal());
                        detailStmt.addBatch();
                    }
                    detailStmt.executeBatch();
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException autoCommitEx) {
                autoCommitEx.printStackTrace();
            }
        }
    }

    /**
     * Obtiene una lista de compras en un rango de fechas específico.
     *
     * @param startDate la fecha de inicio del rango.
     * @param endDate la fecha de fin del rango.
     * @return una lista de compras realizadas en el rango de fechas proporcionado.
     */
    @Override
    public List<Purchase> getByDateRange(String startDate, String endDate) {
        List<Purchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM Purchases WHERE DATE(purchaseDate) BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    purchases.add(mapPurchase(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchases;
    }

    /**
     * Cancela una compra especificando su identificador.
     *
     * @param purchaseId el identificador de la compra a cancelar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean cancelPurchase(int purchaseId) {
        String sql = "UPDATE Purchases SET status = 'Cancelled' WHERE purchaseId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, purchaseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene los detalles de una compra específica.
     *
     * @param purchaseId el identificador de la compra.
     * @return una lista de detalles de la compra.
     */
    @Override
    public List<PurchaseDetail> getDetails(int purchaseId) {
        List<PurchaseDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM PurchaseDetails WHERE purchaseId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, purchaseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    details.add(mapPurchaseDetail(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    /**
     * Obtiene una lista de compras realizadas entre dos fechas específicas.
     *
     * @param startDate la fecha de inicio.
     * @param endDate la fecha de fin.
     * @return una lista de compras en el rango de fechas.
     */
    @Override
    public List<Purchase> findByDateRange(Date startDate, Date endDate) {
        List<Purchase> purchases = new ArrayList<>();
        String query = "SELECT * FROM Purchases WHERE purchaseDate BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Purchase purchase = new Purchase(
                            rs.getInt("purchaseId"),
                            rs.getString("invoiceNumber"),
                            rs.getTimestamp("purchaseDate"),
                            rs.getInt("supplierId"),
                            rs.getInt("userId"),
                            rs.getDouble("subtotal"),
                            rs.getDouble("tax"),
                            rs.getDouble("total"),
                            rs.getString("status"),
                            rs.getString("notes")
                    );
                    purchases.add(purchase);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchases;
    }

    /**
     * Obtiene el nombre del proveedor asociado a una compra específica.
     *
     * @param purchaseId el identificador de la compra.
     * @return el nombre del proveedor o un mensaje indicando que es desconocido.
     */
    @Override
    public String getSupplierNameByPurchaseId(int purchaseId) {
        String query = "SELECT s.companyName FROM Suppliers s JOIN Purchases p ON s.supplierId = p.supplierId WHERE p.purchaseId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, purchaseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("companyName");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Proveedor desconocido";
    }

    /**
     * Lista todas las compras registradas en la base de datos.
     *
     * @return una lista de todas las compras.
     */
    @Override
    public List<Purchase> listAllPurchases() {
        String query = "SELECT * FROM Purchases";
        List<Purchase> purchases = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Purchase purchase = new Purchase();
                purchase.setPurchaseId(rs.getInt("purchaseId"));
                purchase.setSupplierId(rs.getInt("supplierId"));
                purchase.setTotal(rs.getDouble("total"));
                purchase.setPurchaseDate(rs.getTimestamp("purchaseDate"));
                purchases.add(purchase);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchases;
    }

    /**
     * Mapea un objeto {@link ResultSet} a una instancia de {@link Purchase}.
     *
     * @param rs el {@link ResultSet} obtenido de la consulta.
     * @return una instancia de {@link Purchase} con los datos del ResultSet.
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
     */
    private Purchase mapPurchase(ResultSet rs) throws SQLException {
        Purchase purchase = new Purchase();
        purchase.setPurchaseId(rs.getInt("purchaseId"));
        purchase.setInvoiceNumber(rs.getString("invoiceNumber"));
        purchase.setPurchaseDate(rs.getTimestamp("purchaseDate"));
        purchase.setSupplierId(rs.getInt("supplierId"));
        purchase.setUserId(rs.getInt("userId"));
        purchase.setSubtotal(rs.getDouble("subtotal"));
        purchase.setTax(rs.getDouble("tax"));
        purchase.setTotal(rs.getDouble("total"));
        purchase.setStatus(rs.getString("status"));
        purchase.setNotes(rs.getString("notes"));
        return purchase;
    }

    /**
     * Mapea un objeto {@link ResultSet} a una instancia de {@link PurchaseDetail}.
     *
     * @param rs el {@link ResultSet} obtenido de la consulta.
     * @return una instancia de {@link PurchaseDetail} con los datos del ResultSet.
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
     */
    private PurchaseDetail mapPurchaseDetail(ResultSet rs) throws SQLException {
        PurchaseDetail detail = new PurchaseDetail();
        detail.setPurchaseDetailId(rs.getInt("purchaseDetailId"));
        detail.setPurchaseId(rs.getInt("purchaseId"));
        detail.setProductId(rs.getInt("productId"));
        detail.setQuantity(rs.getInt("quantity"));
        detail.setUnitPrice(rs.getDouble("unitPrice"));
        detail.setSubtotal(rs.getDouble("subtotal"));
        return detail;
    }
}