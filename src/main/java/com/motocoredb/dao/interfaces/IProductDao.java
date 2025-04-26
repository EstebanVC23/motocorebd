package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Product;
import java.util.List;

public interface IProductDao {
    boolean createProduct(Product product);
    Product getById(int id);
    List<Product> listAll();
    boolean updateProduct(Product product);
    boolean updateStock(int productId, int quantity);
    boolean changeStatus(int productId, String status);
}