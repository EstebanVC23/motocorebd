package com.motocoredb.controllers;

import com.motocoredb.models.Employee;
import com.motocoredb.services.EmployeeService;
import java.util.List;

public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public boolean createEmployee(Employee employee) {
        return employeeService.createEmployee(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    public Employee getEmployeeById(int id) {
        return employeeService.getEmployeeById(id);
    }

    public boolean updateEmployee(Employee employee) {
        return employeeService.updateEmployee(employee);
    }

    public boolean deactivateEmployee(int id) {
        return employeeService.deactivateEmployee(id);
    }
}