package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IStaffDao;
import com.motocoredb.models.Staff;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDaoImpl implements IStaffDao {
    private final Connection connection;

    public StaffDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean createStaff(Staff staff) {
        String sql = "INSERT INTO Staff (fullName, identityDocument, position, specialty, " +
                    "phone, email, address, hireDate, status, userId) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, staff.getFullName());
            stmt.setString(2, staff.getIdentityDocument());
            stmt.setString(3, staff.getPosition());
            stmt.setString(4, staff.getSpecialty());
            stmt.setString(5, staff.getPhone());
            stmt.setString(6, staff.getEmail());
            stmt.setString(7, staff.getAddress());
            stmt.setDate(8, new java.sql.Date(staff.getHireDate().getTime()));
            stmt.setString(9, staff.getStatus());

            // Convertir `userId = 0` a `NULL` para la base de datos
            if (staff.getUserId() == 0) {
                stmt.setNull(10, Types.INTEGER);
            } else {
                stmt.setInt(10, staff.getUserId());
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        staff.setStaffId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Staff getById(int id) {
        String sql = "SELECT * FROM Staff WHERE staffId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapStaff(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Staff mapStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("staffId"));
        staff.setFullName(rs.getString("fullName"));
        staff.setIdentityDocument(rs.getString("identityDocument"));
        staff.setPosition(rs.getString("position"));
        staff.setSpecialty(rs.getString("specialty"));
        staff.setPhone(rs.getString("phone"));
        staff.setEmail(rs.getString("email"));
        staff.setAddress(rs.getString("address"));
        staff.setHireDate(rs.getDate("hireDate"));
        staff.setStatus(rs.getString("status"));
        staff.setUserId(rs.getInt("userId"));
        return staff;
    }

    @Override
    public List<Staff> listAll() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM Staff";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                staffList.add(mapStaff(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }

    @Override
    public boolean updateStaff(Staff staff) throws SQLException {
        // Validar que el documento de identidad sea único
        if (!isIdentityDocumentUnique(staff.getIdentityDocument(), staff.getStaffId())) {
            throw new IllegalArgumentException("El documento de identidad ya está en uso por otro empleado.");
        }
    
        String sql = "UPDATE Staff SET fullName = ?, identityDocument = ?, position = ?, specialty = ?, phone = ?, email = ?, address = ?, status = ? WHERE staffId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, staff.getFullName());
            stmt.setString(2, staff.getIdentityDocument());
            stmt.setString(3, staff.getPosition());
            stmt.setString(4, staff.getSpecialty());
            stmt.setString(5, staff.getPhone());
            stmt.setString(6, staff.getEmail());
            stmt.setString(7, staff.getAddress());
            stmt.setString(8, staff.getStatus() != null ? staff.getStatus() : "Active");
            stmt.setInt(9, staff.getStaffId());
            return stmt.executeUpdate() > 0;
        }
    }

    private boolean isIdentityDocumentUnique(String identityDocument, int staffId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Staff WHERE identityDocument = ? AND staffId != ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, identityDocument);
            stmt.setInt(2, staffId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // El valor no es único
                }
            }
        }
        return true; // El valor es único
    }
    
    @Override
    public boolean changeStatus(int id, String status) {
        String sql = "UPDATE Staff SET status = ? WHERE staffId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Staff getByStaffId(int userId) {
        String sql = "SELECT * FROM Staff WHERE userId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapStaff(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateUserIdForStaff(int staffId, int userId) {
        String sql = "UPDATE Staff SET userId = ? WHERE staffId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, staffId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Staff findByIdentityDocument(String identityDocument) {
        String sql = "SELECT * FROM Staff WHERE identityDocument = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, identityDocument);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapStaff(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Staff findById(int id) {
        // This could simply call getById since they're identical
        return getById(id);
    }
}