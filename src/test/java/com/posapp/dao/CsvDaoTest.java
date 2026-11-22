package com.posapp.dao;

import com.posapp.dao.csv.*;
import com.posapp.model.*;
import com.posapp.model.enums.MovementType;
import com.posapp.util.CsvFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvDaoTest {

    private CsvFileManager fileManager;
    private UserDao userDao;
    private CategoryDao categoryDao;
    private ProductDao productDao;
    private TransactionDao transactionDao;
    private InventoryDao inventoryDao;

    @BeforeEach
    void setUp(@TempDir File tempDir) {
        fileManager = new CsvFileManager(tempDir.getAbsolutePath());
        userDao = new UserDaoCsv(fileManager);
        categoryDao = new CategoryDaoCsv(fileManager);
        productDao = new ProductDaoCsv(fileManager, categoryDao);
        transactionDao = new TransactionDaoCsv(fileManager, userDao, productDao);
        inventoryDao = new InventoryDaoCsv(fileManager, productDao, userDao);
    }

    @Test
    @DisplayName("CRUD Kategori dan Produk via CSV")
    void testCategoryAndProductCrud() {
        Category cat = new Category(0, "Elektronik", "Peralatan elektronik");
        assertTrue(categoryDao.insert(cat));
        assertNotNull(categoryDao.findById(cat.getId()));

        Product prod = new Product(0, "999888", "Baterai AA", cat, 5000, 7500, 20, 5);
        assertTrue(productDao.insert(prod));

        Product found = productDao.findByBarcode("999888");
        assertNotNull(found);
        assertEquals("Baterai AA", found.getName());

        // Update stok
        productDao.updateStock(prod.getId(), 35);
        assertEquals(35, productDao.findById(prod.getId()).getStock());

        // Delete
        assertTrue(productDao.delete(prod.getId()));
        assertNull(productDao.findById(prod.getId()));
    }

    @Test
    @DisplayName("Menyimpan dan membaca transaksi dengan detail rincian belanja via CSV")
    void testTransactionSaveAndFind() {
        User admin = new Admin(1, "admin", "hash", "Admin Toko");
        userDao.insert(admin);

        Category cat = new Category(1, "Minuman", "");
        categoryDao.insert(cat);

        Product p = new Product(1, "8991", "Kopi Susu", cat, 3000, 5000, 50, 10);
        productDao.insert(p);

        Transaction t = new Transaction("INV-TEST-001", admin);
        t.addItem(p, 3); // 3 * 5000 = 15000
        t.setPayment(new CashPayment(20000));
        t.completeTransaction();

        assertTrue(transactionDao.saveWithItems(t));

        List<Transaction> list = transactionDao.findAll();
        assertEquals(1, list.size());
        Transaction loaded = list.get(0);
        assertEquals("INV-TEST-001", loaded.getInvoiceNumber());
        assertEquals(1, loaded.getItems().size());
        assertEquals(3, loaded.getItems().get(0).getQuantity());
        assertEquals(15000.0, loaded.getGrandTotal());
    }

    @Test
    @DisplayName("Pencatatan mutasi inventaris via CSV")
    void testInventoryMovement() {
        User user = new Admin(1, "admin", "hash", "Admin");
        Category cat = new Category(1, "Makanan", "");
        Product p = new Product(1, "123", "Snack", cat, 1000, 2000, 10, 2);

        StockMovement sm = new StockMovement(p, user, MovementType.STOCK_IN, 10, 0, 10, "Saldo Awal");
        assertTrue(inventoryDao.insertMovement(sm));

        List<StockMovement> list = inventoryDao.findAllMovements();
        assertEquals(1, list.size());
        assertEquals(MovementType.STOCK_IN, list.get(0).getMovementType());
        assertEquals(10, list.get(0).getQuantity());
    }
}

