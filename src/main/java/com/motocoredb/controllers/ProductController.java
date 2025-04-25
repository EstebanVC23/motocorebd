package com.motocoredb.controllers;

import com.motocoredb.models.Product;
import com.motocoredb.services.ProductService;
import java.util.List;

public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public boolean createProduct(Product product) {
        return productService.createProduct(product);
    }

    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    public Product getProductById(int id) {
        return productService.getProductById(id);
    }

    public boolean updateProduct(Product product) {
        return productService.updateProduct(product);
    }

    public boolean updateStock(int productId, int quantity) {
        return productService.updateStock(productId, quantity);
    }
}