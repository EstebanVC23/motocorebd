package com.motocoredb.controllers;

import com.motocoredb.models.ProductCategory;
import com.motocoredb.services.CategoryService;
import java.util.List;

public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public boolean createCategory(ProductCategory category) {
        return categoryService.createCategory(category);
    }

    public List<ProductCategory> getAllCategories() {
        return categoryService.getAllCategories();
    }

    public ProductCategory getCategoryById(int id) {
        return categoryService.getCategoryById(id);
    }

    public boolean updateCategory(ProductCategory category) {
        return categoryService.updateCategory(category);
    }

    public boolean deactivateCategory(int id) {
        return categoryService.deactivateCategory(id);
    }
}