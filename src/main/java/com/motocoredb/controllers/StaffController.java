package com.motocoredb.controllers;

import com.motocoredb.models.Staff;
import com.motocoredb.services.StaffService;
import java.util.List;

public class StaffController {
    private final StaffService StaffService;

    public StaffController(StaffService StaffService) {
        this.StaffService = StaffService;
    }

    public boolean createStaff(Staff Staff) {
        return StaffService.createStaff(Staff);
    }

    public List<Staff> getAllStaffs() {
        return StaffService.getAllStaffs();
    }

    public Staff getStaffById(int id) {
        return StaffService.getStaffById(id);
    }

    public boolean updateStaff(Staff Staff) {
        return StaffService.updateStaff(Staff);
    }

    public boolean deactivateStaff(int id) {
        return StaffService.deactivateStaff(id);
    }
}