package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Customer;
import java.util.List;

public interface ICustomerDao {
    boolean createCustomer(Customer customer);
    Customer getById(int id);
    List<Customer> listAllAdmin();
    List<Customer> listAll();
    boolean updateCustomer(Customer customer);
    boolean changeStatus(int id, String status);
}