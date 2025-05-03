package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.ISaleDao;
import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;

public class SaleDaoImpl implements ISaleDao {

    private final Connection connection;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     */
    public SaleDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean createSale(Sale sale, List<SaleDetail> details) {
        try {
            connection.setAutoCommit(false);

            String saleQuery = "INSERT INTO Sales (invoiceNumber, saleDate, customerId, userId, subtotal, tax, discount, total, paymentMethod, status, notes) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement saleStmt = connection.prepareStatement(saleQuery, Statement.RETURN_GENERATED_KEYS);

            saleStmt.setString(1, sale.getInvoiceNumber());
            saleStmt.setTimestamp(2, sale.getSaleDate());
            saleStmt.setInt(3, sale.getCustomerId());
            saleStmt.setInt(4, sale.getUserId());
            saleStmt.setDouble(5, sale.getSubtotal());
            saleStmt.setDouble(6, sale.getTax());
            saleStmt.setDouble(7, sale.getDiscount());
            saleStmt.setDouble(8, sale.getTotal());
            saleStmt.setString(9, sale.getPaymentMethod());
            saleStmt.setString(10, sale.getStatus());
            saleStmt.setString(11, sale.getNotes());

            int saleRows = saleStmt.executeUpdate();

            ResultSet generatedKeys = saleStmt.getGeneratedKeys();
            int saleId = -1;
            if (generatedKeys.next()) {
                saleId = generatedKeys.getInt(1);
            }

            String detailQuery = "INSERT INTO SaleDetails (saleId, productId, quantity, unitPrice, subtotal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement detailStmt = connection.prepareStatement(detailQuery);

            for (SaleDetail detail : details) {
                detailStmt.setInt(1, saleId);
                detailStmt.setInt(2, detail.getProductId());
                detailStmt.setInt(3, detail.getQuantity());
                detailStmt.setDouble(4, detail.getUnitPrice());
                detailStmt.setDouble(5, detail.getSubtotal());
                detailStmt.addBatch();
            }

            int[] detailRows = detailStmt.executeBatch();

            connection.commit();
            return saleRows > 0 && detailRows.length == details.size();

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
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public List<Sale> getByDateRange(String startDate, String endDate) {
        List<Sale> sales = new ArrayList<>();
        try {
            String query;
            PreparedStatement stmt;

            if (startDate == null && endDate == null) {
                query = "SELECT * FROM Sales";
                stmt = connection.prepareStatement(query);
            } else {
                query = "SELECT * FROM Sales WHERE saleDate BETWEEN ? AND ?";
                stmt = connection.prepareStatement(query);
                stmt.setString(1, startDate);
                stmt.setString(2, endDate);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Sale sale = new Sale(
                    rs.getInt("saleId"),
                    rs.getString("invoiceNumber"),
                    rs.getTimestamp("saleDate"),
                    rs.getInt("customerId"),
                    rs.getInt("userId"),
                    rs.getDouble("subtotal"),
                    rs.getDouble("tax"),
                    rs.getDouble("discount"),
                    rs.getDouble("total"),
                    rs.getString("paymentMethod"),
                    rs.getString("status"),
                    rs.getString("notes")
                );
                sales.add(sale);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }

    @Override
    public boolean cancelSale(int saleId) {
        try {
            String query = "UPDATE Sales SET status = 'Cancelled' WHERE saleId = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, saleId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<SaleDetail> getDetails(int saleId) {
        List<SaleDetail> details = new ArrayList<>();
        try {
            String query = "SELECT * FROM SaleDetails WHERE saleId = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, saleId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SaleDetail detail = new SaleDetail(
                    rs.getInt("saleDetailId"),
                    rs.getInt("saleId"),
                    rs.getInt("productId"),
                    rs.getInt("quantity"),
                    rs.getDouble("unitPrice"),
                    rs.getDouble("subtotal")
                );
                details.add(detail);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public boolean updateSale(Sale sale) {
        String query = "UPDATE Sales SET invoiceNumber = ?, saleDate = ?, customerId = ?, userId = ?, "
                    + "subtotal = ?, tax = ?, discount = ?, total = ?, paymentMethod = ?, status = ?, notes = ? "
                    + "WHERE saleId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, sale.getInvoiceNumber());
            stmt.setTimestamp(2, sale.getSaleDate());
            stmt.setInt(3, sale.getCustomerId());
            stmt.setInt(4, sale.getUserId());
            stmt.setDouble(5, sale.getSubtotal());
            stmt.setDouble(6, sale.getTax());
            stmt.setDouble(7, sale.getDiscount());
            stmt.setDouble(8, sale.getTotal());
            stmt.setString(9, sale.getPaymentMethod());
            stmt.setString(10, sale.getStatus());
            stmt.setString(11, sale.getNotes());
            stmt.setInt(12, sale.getSaleId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Sale getSaleById(int saleId) {
        String query = "SELECT * FROM Sales WHERE saleId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, saleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Sale(
                        rs.getInt("saleId"),
                        rs.getString("invoiceNumber"),
                        rs.getTimestamp("saleDate"),
                        rs.getInt("customerId"),
                        rs.getInt("userId"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("tax"),
                        rs.getDouble("discount"),
                        rs.getDouble("total"),
                        rs.getString("paymentMethod"),
                        rs.getString("status"),
                        rs.getString("notes")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SaleDetail getSaleDetailBySaleId(int saleId) {
        System.out.println("Obteniendo primer detalle para venta ID: " + saleId);
        List<SaleDetail> details = getDetails(saleId);
        if (details != null && !details.isEmpty()) {
            System.out.println("Detalle encontrado para venta ID: " + saleId);
            return details.get(0);
        }
        System.out.println("No se encontraron detalles para venta ID: " + saleId);
        return null;
    }

    @Override
    public List<Sale> findByDateRange(Date startDate, Date endDate) {
        List<Sale> sales = new ArrayList<>();
        String sql;
        PreparedStatement stmt = null;
        
        try {
            if (startDate == null && endDate == null) {
                sql = "SELECT * FROM Sales ORDER BY saleDate DESC";
                stmt = connection.prepareStatement(sql);
            } else {
                sql = "SELECT * FROM Sales WHERE saleDate BETWEEN ? AND ? ORDER BY saleDate DESC";
                stmt = connection.prepareStatement(sql);
                
                java.sql.Date sqlStartDate = startDate != null 
                    ? new java.sql.Date(startDate.getTime()) 
                    : new java.sql.Date(0); // 1970-01-01
                
                java.sql.Date sqlEndDate = endDate != null 
                    ? new java.sql.Date(endDate.getTime()) 
                    : new java.sql.Date(System.currentTimeMillis());
                
                stmt.setDate(1, sqlStartDate);
                stmt.setDate(2, sqlEndDate);
            }
            
            System.out.println("Ejecutando SQL: " + stmt);
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                Sale sale = mapSale(rs);
                sales.add(sale);
            }
            System.out.println("Ventas encontradas: " + count);
            
        } catch (SQLException e) {
            System.err.println("Error en findByDateRange: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return sales;
    }


    @Override
    public double getTotalIncome() {
        double totalIncome = 0.0;
        String sql = "SELECT SUM(total) AS totalIncome FROM Sales";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalIncome = rs.getDouble("totalIncome");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalIncome;
    }

    @Override
    public Map<String, Integer> getTopEmployees() {
        Map<String, Integer> topEmployees = new HashMap<>();
        String sql = "SELECT u.fullName AS employeeName, COUNT(s.saleId) AS salesCount " +
                    "FROM Users u " +
                    "JOIN Sales s ON u.userId = s.userId " +
                    "GROUP BY u.fullName " +
                    "ORDER BY salesCount DESC LIMIT 5";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                topEmployees.put(rs.getString("employeeName"), rs.getInt("salesCount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topEmployees;
    }

    @Override
    public Map<String, Integer> getSoldProducts() {
        Map<String, Integer> soldProducts = new HashMap<>();
        String sql = "SELECT p.name AS productName, SUM(sd.quantity) AS quantitySold " +
                     "FROM Products p " +
                     "JOIN SaleDetails sd ON p.productId = sd.productId " +
                     "GROUP BY p.name " +
                     "ORDER BY quantitySold DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                soldProducts.put(rs.getString("productName"), rs.getInt("quantitySold"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soldProducts;
    }

    @Override
    public Map<java.time.Month, Double> getSalesOverTime() {
        Map<java.time.Month, Double> salesOverTime = new HashMap<>();
        String sql = "SELECT MONTH(s.saleDate) AS saleMonth, YEAR(s.saleDate) AS saleYear, SUM(s.total) AS totalSales " +
                     "FROM Sales s " +
                     "GROUP BY saleYear, saleMonth " +
                     "ORDER BY saleYear, saleMonth";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int month = rs.getInt("saleMonth");
                double total = rs.getDouble("totalSales");
                salesOverTime.put(java.time.Month.of(month), total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salesOverTime;
    }

    private Sale mapSale(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setSaleId(rs.getInt("saleId"));
        sale.setInvoiceNumber(rs.getString("invoiceNumber"));
        
        Timestamp timestamp = rs.getTimestamp("saleDate");
        sale.setSaleDate(timestamp);
        
        sale.setCustomerId(rs.getInt("customerId"));
        sale.setUserId(rs.getInt("userId"));
        
        try {
            sale.setSubtotal(rs.getDouble("subtotal"));
            sale.setTax(rs.getDouble("tax"));
            sale.setDiscount(rs.getDouble("discount"));
            sale.setTotal(rs.getDouble("total"));
        } catch (Exception e) {
            System.err.println("Error al convertir valores decimales: " + e.getMessage());
            sale.setSubtotal(0.0);
            sale.setTax(0.0);
            sale.setDiscount(0.0);
            sale.setTotal(0.0);
        }
        
        sale.setPaymentMethod(rs.getString("paymentMethod"));
        sale.setStatus(rs.getString("status"));
        sale.setNotes(rs.getString("notes"));
        
        return sale;
    }

}