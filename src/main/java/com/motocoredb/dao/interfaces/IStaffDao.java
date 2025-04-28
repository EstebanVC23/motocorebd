package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Staff;

import java.sql.SQLException;
import java.util.List;

public interface IStaffDao {
    boolean createStaff(Staff Staff);
    Staff getById(int id);
    List<Staff> listAll();
    public boolean updateStaff(Staff staff) throws SQLException;
    boolean changeStatus(int id, String status);
    Staff getByStaffId(int userId);
    boolean updateUserIdForStaff(int staffId, int userId);
    Staff findByIdentityDocument(String identityDocument);
    Staff findById(int id);
}