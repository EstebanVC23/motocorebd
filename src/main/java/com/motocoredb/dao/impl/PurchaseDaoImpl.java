package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IPurchaseDao;
import com.motocoredb.models.Purchase;
import com.motocoredb.models.PurchaseDetail;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDaoImpl implements IPurchaseDao {
    private final Connection connection = DBConnection.getConnection();

    @Override
    public boolean createPurchase(Purchase purchase, List<PurchaseDetail> details) {
        try {
            connection.setAutoCommit(false);
            
            String purchaseSql = "INSERT INTO Purchases (invoiceNumber, supplierId, userId, subtotal, tax, total) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(purchaseSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, purchase.getInvoiceNumber());
                stmt.setInt(2, purchase.getSupplierId());
                stmt.setInt(3, purchase.getUserId());
                stmt.setDouble(4, purchase.getSubtotal());
                stmt.setDouble(5, purchase.getTax());
                stmt.setDouble(6, purchase.getTotal());
                stmt.executeUpdate();
                
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int purchaseId = rs.getInt(1);
                    createPurchaseDetails(purchaseId, details);
                }
            }
            
            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createPurchaseDetails(int purchaseId, List<PurchaseDetail> details) throws SQLException {
        String detailSql = "INSERT INTO PurchaseDetails (purchaseId, productId, quantity, unitPrice, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(detailSql)) {
            for (PurchaseDetail detail : details) {
                stmt.setInt(1, purchaseId);
                stmt.setInt(2, detail.getProductId());
                stmt.setInt(3, detail.getQuantity());
                stmt.setDouble(4, detail.getUnitPrice());
                stmt.setDouble(5, detail.getSubtotal());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    @Override
    public List<Purchase> getByDateRange(String startDate, String endDate) {
        List<Purchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM Purchases WHERE DATE(purchaseDate) BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                purchases.add(mapPurchase(rs));
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
        String sql = "UPDATE Purchases SET status = 'Canceled' WHERE purchaseId = ?";
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
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                details.add(mapPurchaseDetail(rs));
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