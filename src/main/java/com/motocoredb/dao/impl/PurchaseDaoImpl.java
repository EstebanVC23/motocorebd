package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IPurchaseDao;
import com.motocoredb.models.Purchase;
import com.motocoredb.models.PurchaseDetail;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDaoImpl implements IPurchaseDao {
    private final Connection connection;

    public PurchaseDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean createPurchase(Purchase purchase, List<PurchaseDetail> details) {
        String purchaseSql = "INSERT INTO Purchases (invoiceNumber, supplierId, userId, subtotal, tax, total, status, notes) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String detailSql = "INSERT INTO PurchaseDetails (purchaseId, productId, quantity, unitPrice, subtotal) " +
                           "VALUES (?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false); // Inicio de la transacción

            // Insertar en la tabla Purchases
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

                // Obtener el ID de la compra recién generada
                int purchaseId;
                try (ResultSet generatedKeys = purchaseStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        purchaseId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Error al obtener el ID generado para la compra.");
                    }
                }

                // Insertar en la tabla PurchaseDetails
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

            connection.commit(); // Confirmar la transacción
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback(); // Revertir la transacción en caso de error
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true); // Restaurar el modo de auto-commit
            } catch (SQLException autoCommitEx) {
                autoCommitEx.printStackTrace();
            }
        }
    }

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