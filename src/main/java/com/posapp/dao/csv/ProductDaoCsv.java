package com.posapp.dao.csv;

import com.posapp.dao.CategoryDao;
import com.posapp.dao.ProductDao;
import com.posapp.model.Category;
import com.posapp.model.Product;
import com.posapp.util.CsvFileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDaoCsv implements ProductDao {
    private static final String FILE_NAME = "products.csv";
    private static final List<String> HEADER = Arrays.asList(
        "id", "barcode", "name", "category_id", "buy_price", "sell_price", "stock", "min_stock_alert"
    );

    private final CsvFileManager fileManager;
    private final CategoryDao categoryDao;

    public ProductDaoCsv(CsvFileManager fileManager, CategoryDao categoryDao) {
        this.fileManager = fileManager;
        this.categoryDao = categoryDao;
        try {
            this.fileManager.ensureFileWithHeader(FILE_NAME, HEADER);
        } catch (IOException e) {
            throw new RuntimeException("Gagal inisialisasi file " + FILE_NAME, e);
        }
    }

    public ProductDaoCsv() {
        this(new CsvFileManager(), new CategoryDaoCsv());
    }

    @Override
    public Product findById(int id) {
        return findAll().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Product findByBarcode(String barcode) {
        if (barcode == null) return null;
        return findAll().stream()
                .filter(p -> barcode.equalsIgnoreCase(p.getBarcode()))
                .findFirst().orElse(null);
    }

    @Override
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        try {
            List<List<String>> rows = fileManager.readAll(FILE_NAME);
            for (int i = 1; i < rows.size(); i++) {
                List<String> r = rows.get(i);
                if (r.size() >= 8) {
                    int id = Integer.parseInt(r.get(0));
                    String barcode = r.get(1);
                    String name = r.get(2);
                    int categoryId = Integer.parseInt(r.get(3));
                    double buyPrice = Double.parseDouble(r.get(4));
                    double sellPrice = Double.parseDouble(r.get(5));
                    int stock = Integer.parseInt(r.get(6));
                    int minStock = Integer.parseInt(r.get(7));

                    Category cat = categoryDao.findById(categoryId);
                    if (cat == null) {
                        cat = new Category(categoryId, "Kategori #" + categoryId, "");
                    }

                    list.add(new Product(id, barcode, name, cat, buyPrice, sellPrice, stock, minStock));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca produk: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Product> findByCategoryId(int categoryId) {
        return findAll().stream()
                .filter(p -> p.getCategory() != null && p.getCategory().getId() == categoryId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchByNameOrBarcode(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String lower = keyword.toLowerCase().trim();
        return findAll().stream()
                .filter(p -> p.getBarcode().toLowerCase().contains(lower) || 
                             p.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    @Override
    public boolean insert(Product product) {
        List<Product> all = findAll();
        int newId = all.stream().mapToInt(Product::getId).max().orElse(0) + 1;
        product.setId(newId);
        all.add(product);
        return saveAll(all);
    }

    @Override
    public boolean update(Product product) {
        List<Product> all = findAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == product.getId()) {
                all.set(i, product);
                found = true;
                break;
            }
        }
        return found && saveAll(all);
    }

    @Override
    public boolean updateStock(int productId, int newStock) {
        List<Product> all = findAll();
        for (Product p : all) {
            if (p.getId() == productId) {
                p.setStock(newStock);
                return saveAll(all);
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        List<Product> all = findAll();
        boolean removed = all.removeIf(p -> p.getId() == id);
        return removed && saveAll(all);
    }

    private boolean saveAll(List<Product> products) {
        List<List<String>> records = new ArrayList<>();
        for (Product p : products) {
            int catId = p.getCategory() != null ? p.getCategory().getId() : 0;
            records.add(Arrays.asList(
                String.valueOf(p.getId()),
                p.getBarcode(),
                p.getName(),
                String.valueOf(catId),
                String.format(java.util.Locale.US, "%.2f", p.getBuyPrice()),
                String.format(java.util.Locale.US, "%.2f", p.getSellPrice()),
                String.valueOf(p.getStock()),
                String.valueOf(p.getMinStockAlert())
            ));
        }
        try {
            fileManager.writeAll(FILE_NAME, HEADER, records);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan data produk: " + e.getMessage(), e);
        }
    }
}

