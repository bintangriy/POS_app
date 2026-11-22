package com.posapp.service;

import com.posapp.dao.InventoryDao;
import com.posapp.dao.ProductDao;
import com.posapp.dao.TransactionDao;
import com.posapp.dao.UserDao;
import com.posapp.dao.csv.*;
import com.posapp.model.*;
import com.posapp.model.enums.MovementType;
import com.posapp.model.enums.TransactionStatus;
import com.posapp.util.CsvFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    private ProductDao productDao;
    private TransactionDao transactionDao;
    private InventoryDao inventoryDao;
    private PosService posService;
    private TransactionService transactionService;

    private User admin;
    private User cashier;
    private Product product;

    @BeforeEach
    void setUp(@TempDir File tempDir) {
        CsvFileManager fileManager = new CsvFileManager(tempDir.getAbsolutePath());
        UserDao userDao = new UserDaoCsv(fileManager);
        var categoryDao = new CategoryDaoCsv(fileManager);
        productDao = new ProductDaoCsv(fileManager, categoryDao);
        transactionDao = new TransactionDaoCsv(fileManager, userDao, productDao);
        inventoryDao = new InventoryDaoCsv(fileManager, productDao, userDao);

        posService = new PosService(productDao, transactionDao, inventoryDao);
        transactionService = new TransactionService(transactionDao, productDao, inventoryDao);

        admin = new Admin(1, "admin", "hash", "Admin Toko");
        cashier = new Cashier(2, "kasir1", "hash", "Kasir Satu", "POS-1");
        userDao.insert(admin);
        userDao.insert(cashier);

        Category cat = new Category(1, "Makanan", "");
        categoryDao.insert(cat);

        product = new Product(1, "899", "Biskuit", cat, 5000, 7000, 30, 5);
        productDao.insert(product);
    }

    @Test
    @DisplayName("Admin membatalkan transaksi: Status berubah menjadi CANCELLED dan stok direstorasi")
    void testCancelTransactionWithStockRestoration() {
        // 1. Jalankan transaksi normal (beli 10) -> stok sisa 20
        Transaction t = posService.createNewTransaction(cashier);
        posService.addProductToCart(t, product, 10);
        posService.processCheckout(t, new CashPayment(100000));

        assertEquals(20, productDao.findById(product.getId()).getStock());

        // 2. Admin membatalkan transaksi
        boolean cancelled = transactionService.cancelTransaction(t.getId(), admin, "Barang retur pembeli");
        assertTrue(cancelled);

        // 3. Verifikasi status transaksi
        Transaction loaded = transactionDao.findById(t.getId());
        assertEquals(TransactionStatus.CANCELLED, loaded.getStatus());
        assertEquals("Barang retur pembeli", loaded.getCancellationReason());

        // 4. Verifikasi stok dikembalikan: 20 + 10 = 30
        assertEquals(30, productDao.findById(product.getId()).getStock());

        // 5. Verifikasi tercatat mutasi pengembalian
        var movements = inventoryDao.findAllMovements();
        assertEquals(2, movements.size()); // 1 SALE + 1 CANCELLATION_RETURN
        assertEquals(MovementType.CANCELLATION_RETURN, movements.get(1).getMovementType());
        assertEquals(10, movements.get(1).getQuantity());
        assertEquals(20, movements.get(1).getStockBefore());
        assertEquals(30, movements.get(1).getStockAfter());
    }

    @Test
    @DisplayName("Kasir dilarang membatalkan transaksi lunas")
    void testCashierCannotCancel() {
        Transaction t = posService.createNewTransaction(cashier);
        posService.addProductToCart(t, product, 2);
        posService.processCheckout(t, new CashPayment(20000));

        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.cancelTransaction(t.getId(), cashier, "Kasir coba batal")
        );
    }
}

