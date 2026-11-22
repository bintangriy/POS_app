# Spesifikasi Penyimpanan Data Berbasis File CSV - POS_app

Dokumen ini mendokumentasikan spesifikasi teknis mekanisme penyimpanan data berbasis berkas lokal (*file-based storage*) menggunakan format **Comma-Separated Values (CSV)** dan **Java I/O murni** untuk aplikasi **POS_app**.

Aplikasi ini tidak lagi menggunakan sistem manajemen basis data relasional (RDBMS) seperti MySQL, melainkan menyimpan data secara mandiri pada direktori `data/`.

---

## 1. Ikhtisar Format Penyimpanan

- **Direktori Utama:** `data/`
- **Format File:** RFC 4180 Comma-Separated Values (`.csv`)
- **Encoding Karakter:** `UTF-8`
- **Pemisah Kolom:** Koma (`,`)
- **Penanganan Nilai Khusus:** Nilai teks yang mengandung tanda koma, tanda kutip ganda, atau baris baru dibungkus dengan tanda kutip ganda (`"..."`), dengan tanda kutip ganda di dalamnya di-escape (`""`).
- **Baris Header:** Wajib ada pada baris pertama di setiap file.
- **Teknologi Akses:** Java I/O (`File`, `FileReader`, `BufferedReader`, `FileWriter`, `BufferedWriter`, `IOException`) dengan *atomic file write / swap* (`.tmp`).

---

## 2. Struktur Berkas CSV

### 2.1 `data/users.csv`
Menyimpan akun pengguna untuk otentikasi kasir dan admin.

| Header Kolom | Tipe Nilai | Contoh Nilai | Keterangan |
| :--- | :--- | :--- | :--- |
| `id` | Integer | `1` | Pengenal unik pengguna |
| `username` | String | `admin` | Nama login (unik) |
| `password_hash` | String | `$2a$10$wE6...` | Hash password terenkripsi BCrypt |
| `full_name` | String | `Administrator Toko`| Nama lengkap staf |
| `role` | String | `ADMIN` / `CASHIER` | Peran hak akses |
| `terminal_id` | String | `POS-TERM-01` | ID mesin kasir (opsional) |
| `is_active` | Boolean | `true` | Status keaktifan akun |

### 2.2 `data/categories.csv`
Menyimpan pengelompokan jenis produk.

| Header Kolom | Tipe Nilai | Contoh Nilai | Keterangan |
| :--- | :--- | :--- | :--- |
| `id` | Integer | `1` | ID kategori |
| `name` | String | `Makanan & Minuman` | Nama kelompok kategori |
| `description` | String | `Snack dan biskuit` | Deskripsi kategori |

### 2.3 `data/products.csv`
Menyimpan katalog master barang ritel.

| Header Kolom | Tipe Nilai | Contoh Nilai | Keterangan |
| :--- | :--- | :--- | :--- |
| `id` | Integer | `1` | ID produk |
| `barcode` | String | `8992388111013` | Barcode fisik barang (unik) |
| `name` | String | `Indomie Mi Goreng 85g` | Nama produk |
| `category_id` | Integer | `1` | Referensi ke `categories.csv` |
| `buy_price` | Decimal String | `2800.00` | Harga modal beli (HPP) |
| `sell_price` | Decimal String | `3500.00` | Harga jual ke pelanggan ($\ge$ buy_price) |
| `stock` | Integer | `120` | Sisa saldo stok fisik ($\ge 0$) |
| `min_stock_alert` | Integer | `20` | Ambang batas peringatan stok menipis |

### 2.4 `data/transactions.csv`
Menyimpan header transaksi penjualan kasir.

| Header Kolom | Tipe Nilai | Contoh Nilai | Keterangan |
| :--- | :--- | :--- | :--- |
| `id` | Integer | `1` | ID transaksi |
| `invoice_number` | String | `INV-20261122-001` | Nomor nota penjualan |
| `user_id` | Integer | `2` | ID kasir penanggung jawab |
| `transaction_date` | ISO-8601 | `2026-11-22T09:30:00` | Waktu nota dicetak |
| `subtotal` | Decimal String | `50000.00` | Total kotor belanja |
| `discount_amount` | Decimal String | `0.00` | Diskon nota |
| `tax_amount` | Decimal String | `0.00` | Nilai pajak |
| `grand_total` | Decimal String | `50000.00` | Total wajib bayar |
| `payment_type` | String | `CASH` / `QRIS` | Metode pelunasan |
| `amount_paid` | Decimal String | `100000.00` | Nominal uang diserahkan |
| `change_amount` | Decimal String | `50000.00` | Uang kembalian |
| `reference_number`| String | `QRIS-9912` | Referensi transaksi digital |
| `status` | String | `COMPLETED` / `CANCELLED` | Status transaksi |
| `cancelled_by` | Integer | `1` | ID admin pembatal |
| `cancelled_at` | ISO-8601 | `2026-11-22T10:00:00` | Waktu dibatalkan |
| `cancellation_reason`| String | `Barang retur pembeli` | Alasan pembatalan |

### 2.5 `data/transaction_details.csv`
Menyimpan rincian item belanjaan per transaksi dengan snapshot harga.

| Header Kolom | Tipe Nilai | Contoh Nilai | Keterangan |
| :--- | :--- | :--- | :--- |
| `id` | Integer | `1` | ID detail item |
| `transaction_id` | Integer | `1` | Referensi ke `transactions.csv` |
| `product_id` | Integer | `1` | Referensi ke `products.csv` |
| `quantity` | Integer | `3` | Jumlah unit dibeli |
| `unit_price` | Decimal String | `3500.00` | Snapshot harga jual saat transaksi |
| `buy_price_snapshot`| Decimal String| `2800.00` | Snapshot harga modal untuk hitung laba |
| `discount_amount` | Decimal String | `0.00` | Diskon per item |
| `line_total` | Decimal String | `10500.00` | `(qty * unit_price) - discount` |

### 2.6 `data/stock_movements.csv`
Menyimpan log riwayat perubahan persediaan barang (*audit trail*).

| Header Kolom | Tipe Nilai | Contoh Nilai | Keterangan |
| :--- | :--- | :--- | :--- |
| `id` | Integer | `1` | ID mutasi |
| `product_id` | Integer | `1` | ID produk |
| `user_id` | Integer | `1` | ID staf pemroses |
| `movement_type` | String | `STOCK_IN` / `SALE` / `ADJUSTMENT` / `CANCELLATION_RETURN` | Jenis mutasi |
| `quantity` | Integer | `10` | Kuantitas mutasi |
| `stock_before` | Integer | `50` | Saldo sebelum mutasi |
| `stock_after` | Integer | `60` | Saldo setelah mutasi |
| `remarks` | String | `Penerimaan supplier`| Keterangan mutasi |
| `created_at` | ISO-8601 | `2026-11-22T08:15:00` | Waktu mutasi |

