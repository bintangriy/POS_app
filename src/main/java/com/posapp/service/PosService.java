package com.posapp.service;

import com.posapp.dao.InventoryDao;
import com.posapp.dao.ProductDao;
import com.posapp.dao.TransactionDao;
import com.posapp.dao.csv.InventoryDaoCsv;
import com.posapp.dao.csv.ProductDaoCsv;
import com.posapp.dao.csv.TransactionDaoCsv;
import com.posapp.model.*;
import com.posapp.model.enums.MovementType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Service orkestrasi Point of Sale (POS) dan transaksi kasir.
 * Menegakkan multi-file consistency berbasis file CSV (Pre-validation, Stock check, Audit trail).
 */
public class PosService {
    private final ProductDao productDao;
    private final TransactionDao transactionDao;
    private final InventoryDao inventoryDao;

    public PosService(ProductDao productDao, TransactionDao transactionDao, InventoryDao inventoryDao) {
        this.productDao = productDao;
        this.transactionDao = transactionDao;
        this.inventoryDao = inventoryDao;
    }

    public PosService() {
        this(new ProductDaoCsv(), new TransactionDaoCsv(), new InventoryDaoCsv());
    }

    public Transaction createNewTransaction(User cashier) {
        String invoice = "INV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        return new Transaction(invoice, cashier);
    }

    public void addProductToCart(Transaction transaction, Product product, int quantity) {
        if (product == null) throw new IllegalArgumentException("Produk tidak ditemukan.");
        if (quantity <= 0) throw new IllegalArgumentException("Kuantitas harus lebih dari 0.");

        // Validasi stok fisik di master data
        Product currentMaster = productDao.findById(product.getId());
        if (currentMaster == null) throw new IllegalStateException("Produk tidak ada di database.");

        int currentCartQty = 0;
        for (TransactionItem item : transaction.getItems()) {
            if (item.getProduct().getId() == product.getId()) {
                currentCartQty = item.getQuantity();
                break;
            }
        }

        if (currentCartQty + quantity > currentMaster.getStock()) {
            throw new IllegalStateException("Sisa stok '" + product.getName() + "' hanya " + currentMaster.getStock() + 
                                            " unit (di keranjang: " + currentCartQty + ").");
        }

        transaction.addItem(product, quantity);
    }

    /**
     * Menyelesaikan checkout pembayaran dan menyimpan transaksi secara konsisten ke seluruh file CSV.
     */
    public synchronized boolean processCheckout(Transaction transaction, Payment payment) {
        if (transaction == null || transaction.getItems().isEmpty()) {
            throw new IllegalStateException("Keranjang belanja masih kosong.");
        }
        if (payment == null) {
            throw new IllegalArgumentException("Metode pembayaran harus dipilih.");
        }

        // 1. Eksekusi polimorfik pemrosesan bayar
        boolean paid = payment.processPayment(transaction.getGrandTotal());
        if (!paid) {
            throw new IllegalStateException("Pembayaran gagal diproses.");
        }
        transaction.setPayment(payment);

        // 2. Pre-Validation In-Memory: Pastikan semua stok mencukupi sebelum mengubah file apapun
        Map<Integer, Integer> stockAfterMap = new HashMap<>();
        for (TransactionItem item : transaction.getItems()) {
            Product p = productDao.findById(item.getProduct().getId());
            if (p == null || p.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Stok tidak mencukupi untuk: " + 
                                                (p != null ? p.getName() : "Item #" + item.getProduct().getId()));
            }
            stockAfterMap.put(p.getId(), p.getStock() - item.getQuantity());
        }

        // 3. Simpan header dan rincian transaksi ke transactions.csv & transaction_details.csv
        transaction.completeTransaction();
        boolean saved = transactionDao.saveWithItems(transaction);
        if (!saved) {
            throw new RuntimeException("Gagal menulis data transaksi ke file CSV.");
        }

        // 4. Potong stok produk di products.csv dan catat log mutasi ke stock_movements.csv
        for (TransactionItem item : transaction.getItems()) {
            int prodId = item.getProduct().getId();
            Product p = productDao.findById(prodId);
            int before = p.getStock();
            int after = stockAfterMap.get(prodId);

            // Update stok
            productDao.updateStock(prodId, after);

            // Log mutasi
            StockMovement sm = new StockMovement(
                p, transaction.getCashier(), MovementType.SALE,
                item.getQuantity(), before, after,
                "Penjualan Nota " + transaction.getInvoiceNumber()
            );
            inventoryDao.insertMovement(sm);
        }

        return true;
    }
}

