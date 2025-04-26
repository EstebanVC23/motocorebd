package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Supplier;
import java.util.List;

public interface ISupplierDao {
    boolean createSupplier(Supplier supplier);
    Supplier getById(int id);
    List<Supplier> listAll();
    boolean updateSupplier(Supplier supplier);
    boolean changeStatus(int id, String status);
    Supplier getByTaxId(String taxId);
}