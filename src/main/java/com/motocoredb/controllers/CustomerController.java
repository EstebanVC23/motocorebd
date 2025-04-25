package com.motocoredb.controllers;

import com.motocoredb.models.Customer;
import com.motocoredb.services.CustomerService;
import java.util.List;

public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    public boolean createCustomer(Customer customer) {
        return customerService.createCustomer(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    public Customer getCustomerById(int id) {
        return customerService.getCustomerById(id);
    }

    public boolean updateCustomer(Customer customer) {
        return customerService.updateCustomer(customer);
    }

    public boolean deactivateCustomer(int id) {
        return customerService.deactivateCustomer(id);
    }
}