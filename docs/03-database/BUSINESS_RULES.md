# Business Rules & Data Integrity Constraints - POS_app

Dokumen ini merangkum aturan bisnis (*business rules*) dan batasan integritas data pada aplikasi **POS_app** dengan media penyimpanan file CSV.

---

## 1. Aturan Validasi Master Data

1. **Barcode Unik:** Kolom `barcode` pada `data/products.csv` bersifat unik. Sistem menolak penambahan produk dengan barcode yang sudah ada.
2. **Integritas Harga Beli vs Harga Jual:**
   - Aturan Bisnis: Harga jual (`sell_price`) **harus lebih besar atau sama dengan** harga beli modal (`buy_price`).
   - Penegakan: Validasi pada constructor kelas `Product` di Java.
3. **Pencegahan Nilai Negatif:**
   - `buy_price >= 0`
   - `sell_price >= 0`
   - `min_stock_alert >= 0`

---

## 2. Aturan Inventaris & Pencegahan Stok Negatif

1. **Pencegahan Stok Negatif (Absolute Invariant):**
   - Saldo stok barang (`stock`) tidak boleh bernilai negatif dalam kondisi apapun ($stock \ge 0$).
   - Penegakan: Method `Product.reduceStock(quantity)` melempar `IllegalStateException` jika kuantitas melebihi stok yang tersedia.
2. **Audit Trail Mutasi Wajib:**
   - Setiap mutasi stok barang (baik bertambah maupun berkurang) wajib menghasilkan satu baris pencatatan pada `data/stock_movements.csv`.
   - Jenis mutasi yang valid:
     - `STOCK_IN`: Barang masuk dari supplier.
     - `ADJUSTMENT`: Koreksi opname fisik (rusak, hilang, selisih hitung).
     - `SALE`: Pemotongan otomatis akibat transaksi kasir lunas.
     - `CANCELLATION_RETURN`: Pengembalian stok otomatis akibat transaksi dibatalkan oleh Admin.

---

## 3. Aturan Transaksi Penjualan & Pembayaran

1. **Snapshot Harga (Price Snapshotting):**
   - Berkas `data/transaction_details.csv` wajib mencatat snapshot harga jual (`unit_price`) dan harga beli modal (`buy_price_snapshot`) saat transaksi terjadi.
   - Perubahan harga di kemudian hari pada `products.csv` tidak mempengaruhi nilai transaksi lama.
2. **Aturan Pembayaran Tunai (Cash):**
   - Nominal bayar $\ge$ grand total.
   - Kembalian: $change = amount\_paid - grand\_total$.
3. **Aturan Pembayaran QRIS (Digital):**
   - Nominal bayar harus sama persis dengan grand total. Kembalian = 0.
   - Nomor referensi settlement wajib dicatat.

---

## 4. Aturan Pembatalan Transaksi oleh Admin

1. **Otorisasi Khusus:** Hanya pengguna dengan peran `Role.ADMIN` yang berhak membatalkan transaksi.
2. **Status Transaksi:** Hanya transaksi berstatus `COMPLETED` yang dapat dibatalkan.
3. **Restorasi Stok Otomatis:**
   - Ketika transaksi dibatalkan, kuantitas setiap item belanja secara otomatis ditambahkan kembali ke stok produk terkait di `data/products.csv`.
   - Mencatat mutasi berjenis `CANCELLATION_RETURN` pada `data/stock_movements.csv`.
   - Mengubah status transaksi menjadi `CANCELLED` disertai alasan dan ID admin pembatal.
