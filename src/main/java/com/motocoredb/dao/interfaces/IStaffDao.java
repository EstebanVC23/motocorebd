package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Staff;
import java.util.List;

public interface IStaffDao {
    boolean createStaff(Staff Staff);
    Staff getById(int id);
    List<Staff> listAll();
    boolean updateStaff(Staff Staff);
    boolean changeStatus(int id, String status);
    Staff getByStaffId(int userId);
}