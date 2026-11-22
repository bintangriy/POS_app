# Database Business Rules & Integrity Constraints - POS_app

Dokumen ini merangkum seluruh aturan bisnis (*business rules*), batasan integritas data (*integrity constraints*), dan mekanisme transaksi atomik pada aplikasi **POS_app**.

---

## 1. Aturan Validasi Master Data

### 1.1 Produk & Kategori
1. **Barcode Unik:** Kolom `barcode` pada tabel `products` bersifat unik (`UNIQUE`). Dua produk berbeda dilarang memiliki nomor barcode yang sama.
2. **Integritas Harga Beli vs Harga Jual:**
   - Aturan Bisnis: Harga jual (`sell_price`) **harus lebih besar atau sama dengan** harga beli modal (`buy_price`).
   - Penegakan: Validasi ganda pada constructor kelas `Product` di Java dan constraint `CHECK (sell_price >= buy_price)` pada MySQL.
3. **Pencegahan Nilai Negatif:**
   - `buy_price >= 0`
   - `sell_price >= 0`
   - `min_stock_alert >= 0`

---

## 2. Aturan Inventaris & Pencegahan Stok Negatif

1. **Pencegahan Stok Negatif (Absolute Invariant):**
   - Saldo stok barang (`stock`) tidak boleh bernilai negatif dalam kondisi apapun ($stock \ge 0$).
   - Penegakan:
     - Lapisan Aplikasi: Method `Product.reduceStock(quantity)` melempar `IllegalStateException` jika $stock < quantity$.
     - Lapisan Basis Data: Tabel `products` dilengkapi constraint MySQL `CONSTRAINT chk_product_stock CHECK (stock >= 0)`.
2. **Audit Trail Mutasi Wajib:**
   - Setiap perubahan nilai kolom `products.stock` (baik bertambah maupun berkurang) **wajib** menghasilkan satu baris pencatatan pada tabel `stock_movements`.
   - Jenis mutasi yang valid:
     - `STOCK_IN`: Barang masuk dari supplier.
     - `ADJUSTMENT`: Koreksi opname fisik (rusak, hilang, selisih hitung).
     - `SALE`: Pemotongan otomatis akibat transaksi kasir lunas.
     - `CANCELLATION_RETURN`: Pengembalian stok otomatis akibat transaksi dibatalkan oleh Admin.

---

## 3. Aturan Transaksi Penjualan & Pembayaran

1. **Snapshot Harga (Price Snapshotting):**
   - Tabel `transaction_details` wajib mencatat snapshot harga jual (`unit_price`) dan harga beli modal (`buy_price_snapshot`) pada detik transaksi terjadi.
   - Perubahan harga produk di kemudian hari pada tabel `products` tidak boleh mengubah nilai transaksi atau laba kotor transaksi lama yang telah selesai.
2. **Kalkulasi Garis Item (`line_total`):**
   - $line\_total = (quantity \times unit\_price) - discount\_amount$
   - $quantity > 0$
3. **Aturan Pembayaran Tunai (Cash Payment):**
   - Nominal uang tunai yang diserahkan pembeli (`amount_paid`) wajib $\ge grand\_total$.
   - Uang kembalian dihitung: $change\_amount = amount\_paid - grand\_total$.
4. **Aturan Pembayaran QRIS (Electronic Payment):**
   - Nilai `amount_paid` harus presisi sama dengan `grand_total`.
   - $change\_amount = 0.00$.
   - Kolom `reference_number` wajib terisi sebagai bukti settlement.

---

## 4. Aturan Pembatalan Transaksi oleh Admin (Transaction Cancellation)

1. **Otorisasi Khusus:** Hanya pengguna dengan peran `Role.ADMIN` yang berhak melakukan pembatalan transaksi. Kasir dilarang membatalkan transaksi yang sudah lunas.
2. **Status Transaksi:** Hanya transaksi dengan status `COMPLETED` yang dapat dibatalkan. Transaksi yang sudah `CANCELLED` tidak dapat dibatalkan ulang.
3. **Restorasi Stok Otomatis:**
   - Ketika transaksi dibatalkan, sistem wajib secara otomatis menambahkan kembali kuantitas setiap item belanja ke saldo stok produk terkait:  
     $stock_{baru} = stock_{lama} + quantity$
   - Sistem wajib mencatat mutasi berjenis `CANCELLATION_RETURN` pada `stock_movements`.
4. **Catatan Alasan:** Pembatalan transaksi wajib menyertakan teks alasan pada kolom `cancellation_reason` dan mencatat ID admin penanggung jawab pada `cancelled_by`.

---

## 5. Atomisitas Transaksi Database (ACID Control)

Seluruh operasi penulisan multitable (Checkout kasir dan Pembatalan nota) wajib dibungkus dalam blok transaksi JDBC:
- `connection.setAutoCommit(false);`
- Eksekusi serangkaian query menggunakan `PreparedStatement`.
- `connection.commit();` jika seluruh query berhasil tanpa pengecualian.
- `connection.rollback();` di dalam blok `catch (SQLException e)` jika ada satu query yang gagal.

