package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IEmployeeDao;
import com.motocoredb.models.Employee;
import java.util.List;

public class EmployeeService {
    private final IEmployeeDao employeeDao;
    
    public EmployeeService(IEmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }
    
    public boolean createEmployee(Employee employee) {
        return employeeDao.createEmployee(employee);
    }
    
    public List<Employee> getAllEmployees() {
        return employeeDao.listAll();
    }
    
    public Employee getEmployeeById(int id) {
        return employeeDao.getById(id);
    }
    
    public boolean updateEmployee(Employee employee) {
        return employeeDao.updateEmployee(employee);
    }
    
    public boolean deactivateEmployee(int id) {
        return employeeDao.changeStatus(id, "Inactive");
    }
}