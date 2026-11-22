package com.posapp.service;

import com.posapp.dao.CategoryDao;
import com.posapp.dao.ProductDao;
import com.posapp.dao.csv.CategoryDaoCsv;
import com.posapp.dao.csv.ProductDaoCsv;
import com.posapp.model.Category;
import com.posapp.model.Product;

import java.util.List;

public class ProductService {
    private final ProductDao productDao;
    private final CategoryDao categoryDao;

    public ProductService(ProductDao productDao, CategoryDao categoryDao) {
        this.productDao = productDao;
        this.categoryDao = categoryDao;
    }

    public ProductService() {
        this(new ProductDaoCsv(), new CategoryDaoCsv());
    }

    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    public Product getProductById(int id) {
        return productDao.findById(id);
    }

    public Product getProductByBarcode(String barcode) {
        return productDao.findByBarcode(barcode);
    }

    public List<Product> searchProducts(String keyword) {
        return productDao.searchByNameOrBarcode(keyword);
    }

    public List<Product> getProductsByCategory(int categoryId) {
        return productDao.findByCategoryId(categoryId);
    }

    public boolean createProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Produk tidak boleh null.");
        Product existing = productDao.findByBarcode(product.getBarcode());
        if (existing != null) {
            throw new IllegalArgumentException("Barcode '" + product.getBarcode() + "' sudah terdaftar.");
        }
        return productDao.insert(product);
    }

    public boolean updateProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Produk tidak boleh null.");
        Product existing = productDao.findByBarcode(product.getBarcode());
        if (existing != null && existing.getId() != product.getId()) {
            throw new IllegalArgumentException("Barcode '" + product.getBarcode() + "' sudah digunakan oleh produk lain.");
        }
        return productDao.update(product);
    }

    public boolean deleteProduct(int id) {
        return productDao.delete(id);
    }

    // Category Management
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    public Category getCategoryById(int id) {
        return categoryDao.findById(id);
    }

    public boolean createCategory(Category category) {
        if (category == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama kategori wajib diisi.");
        }
        Category existing = categoryDao.findByName(category.getName());
        if (existing != null) {
            throw new IllegalArgumentException("Kategori '" + category.getName() + "' sudah ada.");
        }
        return categoryDao.insert(category);
    }

    public boolean updateCategory(Category category) {
        return categoryDao.update(category);
    }

    public boolean deleteCategory(int id) {
        List<Product> products = productDao.findByCategoryId(id);
        if (!products.isEmpty()) {
            throw new IllegalStateException("Kategori tidak dapat dihapus karena masih memuat " + products.size() + " produk.");
        }
        return categoryDao.delete(id);
    }
}

