package com.posapp.dao;

import com.posapp.model.Product;
import java.util.List;

public interface ProductDao {
    Product findById(int id);
    Product findByBarcode(String barcode);
    List<Product> findAll();
    List<Product> findByCategoryId(int categoryId);
    List<Product> searchByNameOrBarcode(String keyword);
    boolean insert(Product product);
    boolean update(Product product);
    boolean updateStock(int productId, int newStock);
    boolean delete(int id);
}

