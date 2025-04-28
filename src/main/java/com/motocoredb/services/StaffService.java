package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IStaffDao;
import com.motocoredb.models.Staff;

import java.sql.SQLException;
import java.util.List;

public class StaffService {
    private final IStaffDao StaffDao;
    
    public StaffService(IStaffDao StaffDao) {
        this.StaffDao = StaffDao;
    }
    
    public boolean createStaff(Staff Staff) {
        return StaffDao.createStaff(Staff);
    }
    
    public List<Staff> getAllStaffs() {
        return StaffDao.listAll();
    }
    
    public Staff getStaffById(int id) {
        return StaffDao.getById(id);
    }
    
    public boolean updateStaff(Staff staff) throws SQLException {
        return StaffDao.updateStaff(staff);
    }
    
    public boolean deactivateStaff(int id) {
        return StaffDao.changeStatus(id, "Inactive");
    }
}