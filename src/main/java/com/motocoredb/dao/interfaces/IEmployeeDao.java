package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Employee;
import java.util.List;

public interface IEmployeeDao {
    boolean createEmployee(Employee employee);
    Employee getById(int id);
    List<Employee> listAll();
    boolean updateEmployee(Employee employee);
    boolean changeStatus(int id, String status);
}