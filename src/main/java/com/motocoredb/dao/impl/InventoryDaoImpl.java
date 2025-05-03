package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IInventoryDao;
import com.motocoredb.models.InventoryMovement;
import com.motocoredb.models.Product;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Implementación de la interfaz IInventoryDao para manejar operaciones de inventario 
 */
public class InventoryDaoImpl implements IInventoryDao {
    private Connection connection;

    
    public InventoryDaoImpl() {
        try {
            this.connection = DBConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage(), 
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Obtiene una lista de movimientos de inventario para un producto específico
     * 
     * @param productId ID del producto
     * @return Lista de movimientos de inventario
     */
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
            JOptionPane.showMessageDialog(null, "Error al obtener movimientos: " + e.getMessage(), 
                    "Error de consulta", JOptionPane.ERROR_MESSAGE);
        }
        return movements;
    }

    /**
     * Ajusta el inventario de un producto
     * 
     * @param productId ID del producto
     * @param quantity Cantidad a ajustar (puede ser negativa)
     * @param notes Notas del ajuste
     * @param userId ID del usuario que realiza el ajuste
     * @return true si se ajustó correctamente, false en caso contrario
     */
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

            if (quantity < 0) {
                checkLowStock(productId);
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
            JOptionPane.showMessageDialog(null, "Error al ajustar inventario: " + e.getMessage(), 
                    "Error de operación", JOptionPane.ERROR_MESSAGE);
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

    /**
     * Obtiene una lista de productos con stock bajo
     * 
     * @return Lista de productos con stock bajo
     */
    @Override
    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.* FROM Products p WHERE p.currentStock <= p.minStock AND p.status = 'Active'";
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
            JOptionPane.showMessageDialog(null, "Error al obtener productos con stock bajo: " + e.getMessage(), 
                    "Error de consulta", JOptionPane.ERROR_MESSAGE);
        }
        return products;
    }

    /**
     * Registra un movimiento de inventario
     * 
     * @param movement Movimiento a registrar
     * @return true si se registró correctamente, false en caso contrario
     */
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
            JOptionPane.showMessageDialog(null, "Error al registrar movimiento: " + e.getMessage(), 
                    "Error de registro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Obtiene el stock actual de un producto
     * 
     * @param productId ID del producto
     * @return Stock actual del producto
     */
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
            JOptionPane.showMessageDialog(null, "Error al obtener stock actual: " + e.getMessage(), 
                    "Error de consulta", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }

    /**
     * Obtiene el total de gastos por compras de inventario.
     * Este método considera los movimientos tipo 'In' (entradas) como gastos,
     * ya que representan compras de productos que implican salida de dinero.
     * 
     * @return Total de gastos por compras de inventario
     */
    @Override
    public double getTotalIn() {
        double total = 0.0;
        String query = "SELECT SUM(im.quantity * p.purchasePrice) as totalCost " +
                       "FROM InventoryMovements im " +
                       "JOIN Products p ON im.productId = p.productId " +
                       "WHERE im.movementType = 'In' " +
                       "AND im.referenceType = 'Purchase'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                total = rs.getDouble("totalCost");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener gastos de inventario: " + e.getMessage(), 
                    "Error de consulta", JOptionPane.ERROR_MESSAGE);
        }
        
        return total;
    }
    
    /**
     * Obtiene el total de salidas de inventario.
     * Este método NO se utiliza para calcular ingresos, ya que las salidas de inventario
     * no siempre representan ingresos económicos (pueden ser ajustes, pérdidas, etc.)
     * 
     * @return Total de salidas de inventario al costo
     */
    @Override
    public double getTotalOut() {
        double total = 0.0;
        String query = "SELECT SUM(im.quantity * p.purchasePrice) as totalCost " +
                       "FROM InventoryMovements im " +
                       "JOIN Products p ON im.productId = p.productId " +
                       "WHERE im.movementType = 'Out'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                total = rs.getDouble("totalCost");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener salidas de inventario: " + e.getMessage(), 
                    "Error de consulta", JOptionPane.ERROR_MESSAGE);
        }
        
        return total;
    }
    
    /**
     * Registra un movimiento de entrada de inventario (compra)
     * 
     * @param productId ID del producto
     * @param quantity Cantidad
     * @param userId ID del usuario que registra
     * @param referenceId ID de referencia (compra)
     * @return true si se registró correctamente
     */
    @Override
    public boolean registerInMovement(int productId, int quantity, int userId, int referenceId) {
        String query = "INSERT INTO InventoryMovements (productId, movementType, quantity, userId, referenceId, referenceType) " +
                       "VALUES (?, 'In', ?, ?, ?, 'Purchase')";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, quantity);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, referenceId);
            
            int result = pstmt.executeUpdate();
            
            // Actualizar el stock del producto
            if (result > 0) {
                updateProductStock(productId, quantity, true);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar entrada de inventario: " + e.getMessage(), 
                    "Error de registro", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Registra un movimiento de salida de inventario (venta)
     * 
     * @param productId ID del producto
     * @param quantity Cantidad
     * @param userId ID del usuario que registra
     * @param referenceId ID de referencia (venta)
     * @return true si se registró correctamente
     */
    @Override
    public boolean registerOutMovement(int productId, int quantity, int userId, int referenceId) {
        String query = "INSERT INTO InventoryMovements (productId, movementType, quantity, userId, referenceId, referenceType) " +
                       "VALUES (?, 'Out', ?, ?, ?, 'Sale')";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            pstmt.setInt(2, quantity);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, referenceId);
            
            int result = pstmt.executeUpdate();

            if (result > 0) {
                updateProductStock(productId, quantity, false);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar salida de inventario: " + e.getMessage(), 
                    "Error de registro", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Actualiza el stock de un producto
     * 
     * @param productId ID del producto
     * @param quantity Cantidad
     * @param isAddition true si es una adición, false si es una reducción
     */
    private void updateProductStock(int productId, int quantity, boolean isAddition) {
        String query = "UPDATE Products SET currentStock = currentStock " + (isAddition ? "+" : "-") + " ? WHERE productId = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
            
            if (!isAddition) {
                checkLowStock(productId);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar stock: " + e.getMessage(), 
                    "Error de actualización", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Verifica si el stock de un producto está por debajo del mínimo
     * 
     * @param productId ID del producto
     */
    private void checkLowStock(int productId) {
        String query = "SELECT name, currentStock, minStock FROM Products WHERE productId = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int currentStock = rs.getInt("currentStock");
                    int minStock = rs.getInt("minStock");
                    String name = rs.getString("name");
                    
                    if (currentStock <= minStock) {
                        createLowStockAlert(productId, name, currentStock, minStock);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar stock bajo: " + e.getMessage());
        }
    }
    
    /**
     * Crea una alerta de stock bajo
     */
    private void createLowStockAlert(int productId, String productName, int currentStock, int minStock) {
        String message = "Stock bajo para el producto " + productName + 
                         ". Stock actual: " + currentStock + 
                         ", Stock mínimo: " + minStock;
        
        String query = "INSERT INTO Alerts (alertType, message, referenceId, referenceType) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "Low stock");
            pstmt.setString(2, message);
            pstmt.setInt(3, productId);
            pstmt.setString(4, "Product");
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al crear alerta de stock bajo: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene un mapa de productos con stock bajo
     * 
     * @return Mapa con nombres de productos y su stock actual
     */
    @Override
    public Map<String, Integer> getLowStockProductsMap() {
        Map<String, Integer> lowStockProducts = new HashMap<>();
        String query = "SELECT name, currentStock FROM Products WHERE currentStock <= minStock AND status = 'Active'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                lowStockProducts.put(rs.getString("name"), rs.getInt("currentStock"));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener productos con stock bajo: " + e.getMessage(), 
                    "Error de consulta", JOptionPane.ERROR_MESSAGE);
        }
        
        return lowStockProducts;
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    @Override
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
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

    @Override
    public double getTotalPurchaseValue() {
        double totalPurchaseValue = 0.0;
        String sql = "SELECT SUM(quantity * purchasePrice) AS totalPurchaseValue " +
                    "FROM InventoryMovements im " +
                    "JOIN Products p ON im.productId = p.productId " +
                    "WHERE im.movementType = 'In'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalPurchaseValue = rs.getDouble("totalPurchaseValue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalPurchaseValue;
    }
}