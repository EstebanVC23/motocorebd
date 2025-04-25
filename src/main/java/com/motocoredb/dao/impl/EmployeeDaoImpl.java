package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IEmployeeDao;
import com.motocoredb.models.Employee;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDaoImpl implements IEmployeeDao {
    private final Connection connection;

    public EmployeeDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean createEmployee(Employee employee) {
        String sql = "INSERT INTO Employees (fullName, idDocument, position) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getIdDocument());
            stmt.setString(3, employee.getPosition());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Employee getById(int id) {
        String sql = "SELECT * FROM Employees WHERE employeeId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapEmployee(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employeeId"));
        employee.setFullName(rs.getString("fullName"));
        employee.setIdDocument(rs.getString("idDocument"));
        employee.setPosition(rs.getString("position"));
        employee.setSpecialty(rs.getString("specialty"));
        employee.setPhone(rs.getString("phone"));
        employee.setEmail(rs.getString("email"));
        employee.setAddress(rs.getString("address"));
        employee.setHireDate(rs.getDate("hireDate"));
        employee.setStatus(rs.getString("status"));
        employee.setUserId(rs.getInt("userId"));
        return employee;
    }

    @Override
    public List<Employee> listAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employees";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE Employees SET fullName = ?, position = ?, phone = ? WHERE employeeId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getPosition());
            stmt.setString(3, employee.getPhone());
            stmt.setInt(4, employee.getEmployeeId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changeStatus(int id, String status) {
        String sql = "UPDATE Employees SET status = ? WHERE employeeId = ?";
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