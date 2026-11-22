package com.posapp.service;

import com.posapp.dao.InventoryDao;
import com.posapp.dao.ProductDao;
import com.posapp.dao.TransactionDao;
import com.posapp.dao.UserDao;
import com.posapp.dao.csv.*;
import com.posapp.model.*;
import com.posapp.model.enums.MovementType;
import com.posapp.util.CsvFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class PosServiceTest {

    private ProductDao productDao;
    private TransactionDao transactionDao;
    private InventoryDao inventoryDao;
    private PosService posService;
    private Product testProduct;
    private User cashier;

    @BeforeEach
    void setUp(@TempDir File tempDir) {
        CsvFileManager fileManager = new CsvFileManager(tempDir.getAbsolutePath());
        UserDao userDao = new UserDaoCsv(fileManager);
        var categoryDao = new CategoryDaoCsv(fileManager);
        productDao = new ProductDaoCsv(fileManager, categoryDao);
        transactionDao = new TransactionDaoCsv(fileManager, userDao, productDao);
        inventoryDao = new InventoryDaoCsv(fileManager, productDao, userDao);

        posService = new PosService(productDao, transactionDao, inventoryDao);

        cashier = new Cashier(1, "kasir1", "hash", "Siti", "TERM-1");
        userDao.insert(cashier);

        Category cat = new Category(1, "Sembako", "");
        categoryDao.insert(cat);

        testProduct = new Product(1, "899123", "Minyak Goreng", cat, 15000, 18000, 20, 5);
        productDao.insert(testProduct);
    }

    @Test
    @DisplayName("Checkout kasir: Memotong stok dan mencatat log mutasi secara konsisten")
    void testCheckoutSuccess() {
        Transaction t = posService.createNewTransaction(cashier);
        posService.addProductToCart(t, testProduct, 5); // beli 5

        CashPayment payment = new CashPayment(100000); // 5 * 18000 = 90.000
        boolean success = posService.processCheckout(t, payment);

        assertTrue(success);
        assertEquals(10000.0, payment.getChange());

        // Verifikasi stok terpotong: 20 - 5 = 15
        Product updatedProduct = productDao.findById(testProduct.getId());
        assertEquals(15, updatedProduct.getStock());

        // Verifikasi log mutasi tercatat
        var movements = inventoryDao.findAllMovements();
        assertEquals(1, movements.size());
        assertEquals(MovementType.SALE, movements.get(0).getMovementType());
        assertEquals(5, movements.get(0).getQuantity());
        assertEquals(20, movements.get(0).getStockBefore());
        assertEquals(15, movements.get(0).getStockAfter());
    }

    @Test
    @DisplayName("Validasi kasir: Menolak penambahan produk melebihi sisa stok")
    void testAddToCartStockExceeded() {
        Transaction t = posService.createNewTransaction(cashier);
        // stok hanya 20, minta 25
        assertThrows(IllegalStateException.class, () -> posService.addProductToCart(t, testProduct, 25));
    }
}

