# Database Documentation & Data Dictionary - POS_app

Dokumen ini mendokumentasikan spesifikasi teknis basis data MySQL untuk aplikasi **POS_app**, mencakup kamus data, tipe data, *constraints*, *indexes*, dan aturan persistensi JDBC.

---

## 1. Ikhtisar Basis Data

- **Database Engine:** MySQL 8.x
- **Storage Engine:** InnoDB (Wajib mendukung transaksi ACID dan Foreign Key constraints)
- **Character Set:** `utf8mb4`
- **Collation:** `utf8mb4_unicode_ci`
- **Konvensi Tipe Data Finansial:** Seluruh nilai mata uang (harga beli, harga jual, subtotal, diskon, grand total, nominal pembayaran, kembalian) disimpan menggunakan tipe data **`DECIMAL(12, 2)`** untuk menjamin presisi matematis dan menghindari *floating-point rounding errors*.

---

## 2. Kamus Data (Data Dictionary)

### 2.1 Tabel `users`
Menyimpan akun pengguna untuk otentikasi kasir dan admin.

| Nama Kolom | Tipe Data | Nullable | Default | Keterangan |
| :--- | :--- | :--- | :--- | :--- |
| `id` | INT | NO | AUTO_INCREMENT | Primary Key |
| `username` | VARCHAR(50) | NO | - | Unik, nama pengguna untuk login |
| `password_hash` | VARCHAR(255) | NO | - | Hash password terenkripsi BCrypt |
| `full_name` | VARCHAR(100) | NO | - | Nama lengkap petugas |
| `role` | ENUM('ADMIN', 'CASHIER') | NO | - | Peran otorisasi menu |
| `terminal_id` | VARCHAR(50) | YES | NULL | Nomor terminal kasir (khusus kasir) |
| `is_active` | BOOLEAN | NO | TRUE | Status keaktifan akun pengguna |
| `created_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Waktu pendaftaran akun |
| `updated_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP ON UPDATE | Waktu perubahan terakhir |

### 2.2 Tabel `categories`
Menyimpan pengelompokan jenis produk.

| Nama Kolom | Tipe Data | Nullable | Default | Keterangan |
| :--- | :--- | :--- | :--- | :--- |
| `id` | INT | NO | AUTO_INCREMENT | Primary Key |
| `name` | VARCHAR(100) | NO | - | Unik, nama kategori produk |
| `description` | TEXT | YES | NULL | Deskripsi penjelasan kategori |
| `created_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Waktu pencatatan |

### 2.3 Tabel `products`
Menyimpan katalog master barang ritel.

| Nama Kolom | Tipe Data | Nullable | Default | Keterangan |
| :--- | :--- | :--- | :--- | :--- |
| `id` | INT | NO | AUTO_INCREMENT | Primary Key |
| `barcode` | VARCHAR(50) | NO | - | Unik, nomor barcode produk fisik |
| `name` | VARCHAR(150) | NO | - | Nama produk lengkap |
| `category_id` | INT | NO | - | Foreign Key ke `categories(id)` |
| `buy_price` | DECIMAL(12, 2) | NO | - | Harga modal beli (HPP), $\ge 0$ |
| `sell_price` | DECIMAL(12, 2) | NO | - | Harga jual ke pelanggan, $\ge$ `buy_price` |
| `stock` | INT | NO | 0 | Sisa stok fisik saat ini, constraint $\ge 0$ |
| `min_stock_alert` | INT | NO | 5 | Ambang batas peringatan stok menipis |
| `created_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Waktu pembuatan data |
| `updated_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP ON UPDATE | Waktu update terakhir |

### 2.4 Tabel `transactions`
Menyimpan header transaksi penjualan kasir.

| Nama Kolom | Tipe Data | Nullable | Default | Keterangan |
| :--- | :--- | :--- | :--- | :--- |
| `id` | INT | NO | AUTO_INCREMENT | Primary Key |
| `invoice_number` | VARCHAR(50) | NO | - | Unik, nomor nota transaksi (misal: `INV-20261122-001`) |
| `user_id` | INT | NO | - | Foreign Key ke `users(id)` (kasir bertugas) |
| `transaction_date` | DATETIME | NO | CURRENT_TIMESTAMP | Waktu nota diproses |
| `subtotal` | DECIMAL(12, 2) | NO | - | Total kotor belanjaan sebelum diskon/pajak |
| `discount_amount` | DECIMAL(12, 2) | NO | 0.00 | Total potongan diskon nota |
| `tax_amount` | DECIMAL(12, 2) | NO | 0.00 | Nilai pajak jika berlaku |
| `grand_total` | DECIMAL(12, 2) | NO | - | Total akhir yang wajib dibayar |
| `payment_type` | ENUM('CASH', 'QRIS')| NO | - | Metode pelunasan |
| `amount_paid` | DECIMAL(12, 2) | NO | - | Nominal uang yang diterima kasir |
| `change_amount` | DECIMAL(12, 2) | NO | 0.00 | Uang kembalian yang diserahkan |
| `reference_number`| VARCHAR(100) | YES | NULL | Nomor referensi/settlement QRIS |
| `status` | ENUM('PENDING', 'COMPLETED', 'CANCELLED') | NO | 'COMPLETED' | Status transaksi |
| `cancelled_by` | INT | YES | NULL | Foreign Key ke `users(id)` yang membatalkan |
| `cancelled_at` | DATETIME | YES | NULL | Waktu transaksi dibatalkan |
| `cancellation_reason` | VARCHAR(255) | YES | NULL | Catatan alasan pembatalan |
| `created_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Waktu pembuatan baris |

### 2.5 Tabel `transaction_details` (`transaction_items`)
Menyimpan rincian produk yang dibeli per transaksi dengan snapshot harga.

| Nama Kolom | Tipe Data | Nullable | Default | Keterangan |
| :--- | :--- | :--- | :--- | :--- |
| `id` | INT | NO | AUTO_INCREMENT | Primary Key |
| `transaction_id` | INT | NO | - | Foreign Key ke `transactions(id)` |
| `product_id` | INT | NO | - | Foreign Key ke `products(id)` |
| `quantity` | INT | NO | - | Jumlah unit barang yang dibeli ($> 0$) |
| `unit_price` | DECIMAL(12, 2) | NO | - | Snapshot harga jual per unit saat transaksi |
| `buy_price_snapshot`| DECIMAL(12, 2) | NO | - | Snapshot harga modal per unit untuk hitung laba kotor |
| `discount_amount`| DECIMAL(12, 2) | NO | 0.00 | Potongan harga per item |
| `line_total` | DECIMAL(12, 2) | NO | - | `(quantity * unit_price) - discount_amount` |

### 2.6 Tabel `stock_movements`
Menyimpan riwayat lengkap perubahan persediaan (*audit trail*).

| Nama Kolom | Tipe Data | Nullable | Default | Keterangan |
| :--- | :--- | :--- | :--- | :--- |
| `id` | INT | NO | AUTO_INCREMENT | Primary Key |
| `product_id` | INT | NO | - | Foreign Key ke `products(id)` |
| `user_id` | INT | NO | - | Foreign Key ke `users(id)` petugas mutasi |
| `movement_type` | ENUM('STOCK_IN', 'ADJUSTMENT', 'SALE', 'CANCELLATION_RETURN') | NO | - | Jenis mutasi stok |
| `quantity` | INT | NO | - | Jumlah unit yang dimutasi |
| `stock_before` | INT | NO | - | Saldo stok sebelum mutasi |
| `stock_after` | INT | NO | - | Saldo stok sesudah mutasi |
| `remarks` | VARCHAR(255) | YES | NULL | Catatan keterangan mutasi |
| `created_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Waktu pencatatan mutasi |

---

## 3. Indeks & Optimalisasi Kinerja Query

- `idx_product_barcode`: Indeks unik pada `products(barcode)` untuk pencarian barcode scanner instan ($O(1)$).
- `idx_trans_invoice`: Indeks unik pada `transactions(invoice_number)`.
- `idx_trans_date`: Indeks pada `transactions(transaction_date)` untuk mempercepat filter rentang tanggal laporan.
- `idx_details_trans_id`: Indeks foreign key pada `transaction_details(transaction_id)`.
- `idx_movements_prod_id`: Indeks pada `stock_movements(product_id)` untuk pembuatan kartu stok barang.

