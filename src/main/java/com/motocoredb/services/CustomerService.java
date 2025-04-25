package com.motocoredb.services;

import com.motocoredb.dao.interfaces.ICustomerDao;
import com.motocoredb.models.Customer;
import java.util.List;

public class CustomerService {
    private final ICustomerDao customerDao;
    
    public CustomerService(ICustomerDao customerDao) {
        this.customerDao = customerDao;
    }
    
    public boolean createCustomer(Customer customer) {
        return customerDao.createCustomer(customer);
    }
    
    public List<Customer> getAllCustomers() {
        return customerDao.listAll();
    }
    
    public Customer getCustomerById(int id) {
        return customerDao.getById(id);
    }
    
    public boolean updateCustomer(Customer customer) {
        return customerDao.updateCustomer(customer);
    }
    
    public boolean deactivateCustomer(int id) {
        return customerDao.changeStatus(id, "Inactive");
    }
}