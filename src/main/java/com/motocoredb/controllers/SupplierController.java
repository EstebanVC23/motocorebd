package com.motocoredb.controllers;

import com.motocoredb.models.Supplier;
import com.motocoredb.services.SupplierService;
import java.util.List;

public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    public boolean createSupplier(Supplier supplier) {
        return supplierService.createSupplier(supplier);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    public Supplier getSupplierById(int id) {
        return supplierService.getSupplierById(id);
    }

    public boolean updateSupplier(Supplier supplier) {
        return supplierService.updateSupplier(supplier);
    }
}