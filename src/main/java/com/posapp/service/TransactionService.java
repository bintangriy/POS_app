package com.posapp.service;

import com.posapp.dao.InventoryDao;
import com.posapp.dao.ProductDao;
import com.posapp.dao.TransactionDao;
import com.posapp.dao.csv.InventoryDaoCsv;
import com.posapp.dao.csv.ProductDaoCsv;
import com.posapp.dao.csv.TransactionDaoCsv;
import com.posapp.model.Product;
import com.posapp.model.StockMovement;
import com.posapp.model.Transaction;
import com.posapp.model.TransactionItem;
import com.posapp.model.User;
import com.posapp.model.enums.MovementType;
import com.posapp.model.enums.TransactionStatus;

import java.time.LocalDate;
import java.util.List;

public class TransactionService {
    private final TransactionDao transactionDao;
    private final ProductDao productDao;
    private final InventoryDao inventoryDao;

    public TransactionService(TransactionDao transactionDao, ProductDao productDao, InventoryDao inventoryDao) {
        this.transactionDao = transactionDao;
        this.productDao = productDao;
        this.inventoryDao = inventoryDao;
    }

    public TransactionService() {
        this(new TransactionDaoCsv(), new ProductDaoCsv(), new InventoryDaoCsv());
    }

    public List<Transaction> getAllTransactions() {
        return transactionDao.findAll();
    }

    public Transaction getTransactionById(int id) {
        return transactionDao.findById(id);
    }

    public Transaction getTransactionByInvoice(String invoiceNumber) {
        return transactionDao.findByInvoiceNumber(invoiceNumber);
    }

    public List<Transaction> getTransactionsByDateRange(LocalDate from, LocalDate to) {
        return transactionDao.findByDateRange(from, to);
    }

    /**
     * Membatalkan transaksi (khusus Admin) dan secara otomatis merestorasi stok seluruh item belanja.
     */
    public synchronized boolean cancelTransaction(int transactionId, User admin, String reason) {
        if (admin == null || !admin.canAccessAdminMenu()) {
            throw new IllegalArgumentException("Hanya Administrator yang memiliki wewenang membatalkan transaksi.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Alasan pembatalan wajib diisi.");
        }

        Transaction transaction = transactionDao.findById(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaksi tidak ditemukan.");
        }
        if (transaction.getStatus() == TransactionStatus.CANCELLED) {
            throw new IllegalStateException("Transaksi ini sudah dibatalkan sebelumnya.");
        }

        // 1. Eksekusi domain cancel
        transaction.cancel(admin, reason);

        // 2. Update status transaksi pada file transactions.csv
        boolean updated = transactionDao.updateStatus(transactionId, transaction);
        if (!updated) {
            throw new RuntimeException("Gagal memperbarui status transaksi.");
        }

        // 3. Kembalikan stok barang yang dibeli (Stock Restoration)
        for (TransactionItem item : transaction.getItems()) {
            Product product = productDao.findById(item.getProduct().getId());
            if (product != null) {
                int before = product.getStock();
                int restoredStock = before + item.getQuantity();

                // Kembalikan saldo stok
                productDao.updateStock(product.getId(), restoredStock);

                // Catat mutasi pengembalian pembatalan ke stock_movements.csv
                StockMovement movement = new StockMovement(
                    product, admin, MovementType.CANCELLATION_RETURN,
                    item.getQuantity(), before, restoredStock,
                    "Restorasi Stok dari Pembatalan Nota " + transaction.getInvoiceNumber() + ": " + reason
                );
                inventoryDao.insertMovement(movement);
            }
        }

        return true;
    }
}

