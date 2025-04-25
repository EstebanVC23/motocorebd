package com.motocoredb.services;

import com.motocoredb.dao.interfaces.ICategoryDao;
import com.motocoredb.models.ProductCategory;
import java.util.List;

public class CategoryService {
    private final ICategoryDao categoryDao;
    
    public CategoryService(ICategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }
    
    public boolean createCategory(ProductCategory category) {
        return categoryDao.createCategory(category);
    }
    
    public List<ProductCategory> getAllCategories() {
        return categoryDao.listAll();
    }
    
    public ProductCategory getCategoryById(int id) {
        return categoryDao.getById(id);
    }
    
    public boolean updateCategory(ProductCategory category) {
        return categoryDao.updateCategory(category);
    }
    
    public boolean deactivateCategory(int id) {
        return categoryDao.changeStatus(id, "Inactive");
    }
}