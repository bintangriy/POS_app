package com.posapp.service;

import com.posapp.dao.InventoryDao;
import com.posapp.dao.ProductDao;
import com.posapp.dao.csv.InventoryDaoCsv;
import com.posapp.dao.csv.ProductDaoCsv;
import com.posapp.model.Product;
import com.posapp.model.StockMovement;
import com.posapp.model.User;
import com.posapp.model.enums.MovementType;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryService {
    private final ProductDao productDao;
    private final InventoryDao inventoryDao;

    public InventoryService(ProductDao productDao, InventoryDao inventoryDao) {
        this.productDao = productDao;
        this.inventoryDao = inventoryDao;
    }

    public InventoryService() {
        this(new ProductDaoCsv(), new InventoryDaoCsv());
    }

    public synchronized boolean restockProduct(int productId, int quantity, User user, String remarks) {
        if (quantity <= 0) throw new IllegalArgumentException("Jumlah restock harus lebih dari 0.");
        Product product = productDao.findById(productId);
        if (product == null) throw new IllegalArgumentException("Produk tidak ditemukan.");

        int before = product.getStock();
        int after = before + quantity;

        productDao.updateStock(productId, after);

        StockMovement movement = new StockMovement(
            product, user, MovementType.STOCK_IN, quantity, before, after,
            remarks != null ? remarks : "Restock barang masuk"
        );
        return inventoryDao.insertMovement(movement);
    }

    public synchronized boolean adjustStock(int productId, int newPhysicalStock, User user, String reason) {
        if (newPhysicalStock < 0) throw new IllegalArgumentException("Stok fisik tidak boleh bernilai negatif.");
        Product product = productDao.findById(productId);
        if (product == null) throw new IllegalArgumentException("Produk tidak ditemukan.");

        int before = product.getStock();
        int diff = newPhysicalStock - before;

        productDao.updateStock(productId, newPhysicalStock);

        StockMovement movement = new StockMovement(
            product, user, MovementType.ADJUSTMENT, Math.abs(diff), before, newPhysicalStock,
            reason != null ? reason : "Penyesuaian stok opname fisik"
        );
        return inventoryDao.insertMovement(movement);
    }

    public List<Product> getLowStockProducts() {
        return productDao.findAll().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    public List<StockMovement> getAllMovements() {
        return inventoryDao.findAllMovements();
    }

    public List<StockMovement> getMovementsByProductId(int productId) {
        return inventoryDao.findMovementsByProductId(productId);
    }

    public List<StockMovement> getMovementsByDateRange(LocalDate from, LocalDate to) {
        return inventoryDao.findMovementsByDateRange(from, to);
    }
}

