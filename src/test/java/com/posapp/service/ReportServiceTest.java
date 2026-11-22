package com.posapp.service;

import com.posapp.dao.ProductDao;
import com.posapp.dao.TransactionDao;
import com.posapp.dao.UserDao;
import com.posapp.dao.csv.*;
import com.posapp.model.*;
import com.posapp.util.CsvFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {

    private ReportService reportService;
    private PosService posService;
    private Product p1;
    private Product p2;
    private User cashier;

    @BeforeEach
    void setUp(@TempDir File tempDir) {
        CsvFileManager fileManager = new CsvFileManager(tempDir.getAbsolutePath());
        UserDao userDao = new UserDaoCsv(fileManager);
        var categoryDao = new CategoryDaoCsv(fileManager);
        ProductDao productDao = new ProductDaoCsv(fileManager, categoryDao);
        TransactionDao transactionDao = new TransactionDaoCsv(fileManager, userDao, productDao);
        var inventoryDao = new InventoryDaoCsv(fileManager, productDao, userDao);

        posService = new PosService(productDao, transactionDao, inventoryDao);
        reportService = new ReportService(transactionDao, productDao);

        cashier = new Cashier(1, "kasir", "hash", "Kasir", "POS");
        userDao.insert(cashier);

        Category cat = new Category(1, "Umum", "");
        categoryDao.insert(cat);

        // p1: beli 1000, jual 1500 (margin 500)
        p1 = new Product(1, "111", "Barang A", cat, 1000, 1500, 100, 10);
        // p2: beli 2000, jual 3000 (margin 1000)
        p2 = new Product(2, "222", "Barang B", cat, 2000, 3000, 100, 10);
        productDao.insert(p1);
        productDao.insert(p2);
    }

    @Test
    @DisplayName("Kalkulasi laporan penjualan harian dan laba kotor akurat dari CSV")
    void testDailySalesReport() {
        // Transaksi 1: Beli 2 unit p1 (omset 3000, hpp 2000)
        Transaction t1 = posService.createNewTransaction(cashier);
        posService.addProductToCart(t1, p1, 2);
        posService.processCheckout(t1, new CashPayment(5000));

        // Transaksi 2: Beli 1 unit p2 (omset 3000, hpp 2000)
        Transaction t2 = posService.createNewTransaction(cashier);
        posService.addProductToCart(t2, p2, 1);
        posService.processCheckout(t2, new CashPayment(5000));

        Map<String, Object> rep = reportService.getDailySalesReport(LocalDate.now());

        assertEquals(2, rep.get("transactionCount"));
        assertEquals(6000.0, (Double) rep.get("totalOmset"));
        assertEquals(4000.0, (Double) rep.get("totalHpp"));
        assertEquals(2000.0, (Double) rep.get("grossProfit")); // 6000 - 4000 = 2000
    }

    @Test
    @DisplayName("Laporan valuasi aset persediaan toko")
    void testInventoryValuation() {
        Map<String, Object> val = reportService.getInventoryValuationReport();
        assertEquals(2, val.get("totalSku"));
        assertEquals(200, val.get("totalUnits")); // 100 + 100
        // total valuation = (100 * 1000) + (100 * 2000) = 300.000
        assertEquals(300000.0, (Double) val.get("totalValuation"));
    }
}

