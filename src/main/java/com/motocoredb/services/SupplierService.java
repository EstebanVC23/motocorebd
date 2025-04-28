package com.motocoredb.services;

import com.motocoredb.dao.interfaces.ISupplierDao;
import com.motocoredb.models.Supplier;

import java.sql.SQLException;
import java.util.List;

public class SupplierService {
    private final ISupplierDao supplierDao;
    
    public SupplierService(ISupplierDao supplierDao) {
        this.supplierDao = supplierDao;
    }
    
    public boolean createSupplier(Supplier supplier) {
        try {
            return supplierDao.createSupplier(supplier); // Llama al método del DAO que lanza SQLException
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el proveedor", e); // Envolver en una excepción más genérica si es necesario
        }
    }
    
    public List<Supplier> getAllSuppliers() {
        return supplierDao.listAll();
    }
    
    public Supplier getSupplierById(int id) {
        return supplierDao.getById(id);
    }
    
    public boolean updateSupplier(Supplier supplier) {
        return supplierDao.updateSupplier(supplier);
    }
}