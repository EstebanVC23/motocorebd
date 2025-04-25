package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IProductDao;
import com.motocoredb.models.Product;
import java.util.List;

public class ProductService {
    private final IProductDao productDao;
    
    public ProductService(IProductDao productDao) {
        this.productDao = productDao;
    }
    
    public boolean createProduct(Product product) {
        return productDao.createProduct(product);
    }
    
    public List<Product> getAllProducts() {
        return productDao.listAll();
    }
    
    public Product getProductById(int id) {
        return productDao.getById(id);
    }
    
    public boolean updateProduct(Product product) {
        return productDao.updateProduct(product);
    }
    
    public boolean updateStock(int productId, int quantity) {
        return productDao.updateStock(productId, quantity);
    }
}