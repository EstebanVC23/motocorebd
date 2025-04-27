package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IInventoryDao;
import com.motocoredb.models.InventoryMovement;
import com.motocoredb.models.Product;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDaoImpl implements IInventoryDao {
    private final Connection connection;

    public InventoryDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public List<InventoryMovement> getMovements(int productId) {
        List<InventoryMovement> movements = new ArrayList<>();
        String sql = "SELECT * FROM InventoryMovements WHERE productId = ? ORDER BY movementDate DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movements.add(mapMovement(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movements;
    }

    @Override
    public boolean adjustInventory(int productId, int quantity, String notes, int userId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String movementSql = "INSERT INTO InventoryMovements (productId, movementType, quantity, userId, referenceType, notes) " +
                               "VALUES (?, 'Adjustment', ?, ?, 'Adjustment', ?)";
            try (PreparedStatement stmt = conn.prepareStatement(movementSql)) {
                stmt.setInt(1, productId);
                stmt.setInt(2, quantity);
                stmt.setInt(3, userId);
                stmt.setString(4, notes);
                stmt.executeUpdate();
            }
            
            String updateSql = "UPDATE Products SET currentStock = currentStock + ? WHERE productId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, quantity);
                stmt.setInt(2, productId);
                stmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.* FROM Products p WHERE p.currentStock < p.minStock AND p.status = 'Active'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("productId"));
                product.setProductName(rs.getString("name"));
                product.setCurrentStock(rs.getInt("currentStock"));
                product.setMinStock(rs.getInt("minStock"));
                product.setProductCode(rs.getString("productCode"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public boolean recordMovement(InventoryMovement movement) {
        String sql = "INSERT INTO InventoryMovements (productId, movementType, quantity, userId, referenceId, referenceType, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, movement.getProductId());
            stmt.setString(2, movement.getMovementType());
            stmt.setInt(3, movement.getQuantity());
            stmt.setInt(4, movement.getUserId());
            stmt.setInt(5, movement.getReferenceId());
            stmt.setString(6, movement.getReferenceType());
            stmt.setString(7, movement.getNotes());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getCurrentStock(int productId) {
        String sql = "SELECT currentStock FROM Products WHERE productId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("currentStock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Retorna 0 si hay error o no encuentra el producto
    }

    private InventoryMovement mapMovement(ResultSet rs) throws SQLException {
        InventoryMovement movement = new InventoryMovement();
        movement.setMovementId(rs.getInt("movementId"));
        movement.setProductId(rs.getInt("productId"));
        movement.setMovementType(rs.getString("movementType"));
        movement.setQuantity(rs.getInt("quantity"));
        movement.setMovementDate(rs.getTimestamp("movementDate"));
        movement.setUserId(rs.getInt("userId"));
        movement.setReferenceId(rs.getInt("referenceId"));
        movement.setReferenceType(rs.getString("referenceType"));
        movement.setNotes(rs.getString("notes"));
        return movement;
    }
}