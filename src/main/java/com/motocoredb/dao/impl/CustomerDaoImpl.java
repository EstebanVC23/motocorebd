package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.ICustomerDao;
import com.motocoredb.models.Customer;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements ICustomerDao {
    private final Connection connection = DBConnection.getConnection();

    @Override
    public boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO Customers (customerType, nameOrBusinessName, idDocument) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getCustomerType());
            stmt.setString(2, customer.getNameOrBusinessName());
            stmt.setString(3, customer.getIdDocument());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Customer getById(int id) {
        String sql = "SELECT * FROM Customers WHERE customerId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Customer mapCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customerId"));
        customer.setCustomerType(rs.getString("customerType"));
        customer.setNameOrBusinessName(rs.getString("nameOrBusinessName"));
        customer.setIdDocument(rs.getString("idDocument"));
        customer.setAddress(rs.getString("address"));
        customer.setPhone(rs.getString("phone"));
        customer.setEmail(rs.getString("email"));
        customer.setRegistrationDate(rs.getTimestamp("registrationDate"));
        customer.setPurchaseCount(rs.getInt("purchaseCount"));
        customer.setStatus(rs.getString("status"));
        return customer;
    }

    @Override
    public List<Customer> listAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(mapCustomer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE Customers SET nameOrBusinessName = ?, idDocument = ?, address = ?, phone = ?, email = ? WHERE customerId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getNameOrBusinessName());
            stmt.setString(2, customer.getIdDocument());
            stmt.setString(3, customer.getAddress());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getEmail());
            stmt.setInt(6, customer.getCustomerId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changeStatus(int id, String status) {
        String sql = "UPDATE Customers SET status = ? WHERE customerId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}