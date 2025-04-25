package com.motocoredb.dao.interfaces;

import com.motocoredb.models.ProductCategory;
import java.util.List;

public interface ICategoryDao {
    boolean createCategory(ProductCategory category);
    ProductCategory getById(int id);
    List<ProductCategory> listAll();
    boolean updateCategory(ProductCategory category);
    boolean changeStatus(int id, String status);
}