# Product Requirements Document (PRD) - POS_app

## 1. Identitas & Visi Produk
**POS_app** adalah aplikasi desktop kasir (*Point of Sale*) dan manajemen persediaan barang (*Inventory Management*) yang dirancang khusus untuk kebutuhan Usaha Mikro, Kecil, dan Menengah (UMKM) sektor ritel.

Aplikasi ini dikembangkan sebagai tugas akhir mata kuliah **Java Object-Oriented Programming (OOP)** dengan mengutamakan:
- Arsitektur berorientasi objek murni tanpa *god class*.
- Keandalan transaksi keuangan dengan database MySQL dan JDBC.
- Penggunaan **Java I/O** native untuk manajemen konfigurasi file eksternal tanpa *hardcoded credentials*.
- Antarmuka desktop JavaFX yang cepat, keyboard-friendly, dan bebas dari ornamen generik (*Anti-AI-Slop*).

---

## 2. Target Pengguna & Persona

| Peran | Pengguna | Tanggung Jawab & Kebutuhan Utama |
| :--- | :--- | :--- |
| **Cashier (Kasir)** | Staf Operasional Kasir | - Melakukan transaksi kasir secepat mungkin (scan barcode, terima pembayaran tunai/QRIS).<br/>- Menghitung uang kembalian secara tepat tanpa kesalahan.<br/>- Meninjau transaksi pada shift aktifnya. |
| **Admin (Pemilik/Manajer)** | Pemilik Toko / Manajer | - Memantau ringkasan metrik harian pada Dashboard utama.<br/>- Mengelola katalog produk, harga beli modal, harga jual, dan kategori.<br/>- Mengontrol persediaan barang (stok masuk, opname, alert stok menipis).<br/>- Melakukan pembatalan transaksi dengan pengembalian stok otomatis jika terjadi komplain/kesalahan.<br/>- Menganalisis laporan penjualan harian, bulanan, dan keuntungan kotor. |

---

## 3. Fitur Utama Produk (Functional Requirements)

### FR-01: Autentikasi Pengguna & Role-Based Access Control (RBAC)
- Login berbasis username dan password terenkripsi hash BCrypt.
- Pembedaan peran:
  - `Cashier`: Mengakses menu Dashboard kasir ringkas, Transaksi Kasir (POS), dan Riwayat Transaksi Shift.
  - `Admin`: Mengakses seluruh menu manajerial (Dashboard lengkap, Produk, Kategori, Inventaris, Riwayat Transaksi + Pembatalan, Laporan Penjualan & Laba).
- Logout aman dengan pembersihan sesi pengguna di memori.

### FR-02: Dashboard Utama
- **Ringkasan Penjualan Hari Ini:** Menampilkan total nominal omset penjualan yang berhasil diproses hari ini.
- **Jumlah Transaksi:** Total frekuensi transaksi nota penjualan yang berstatus `COMPLETED` pada hari aktif.
- **Jumlah Produk:** Total keseluruhan SKU produk yang terdaftar aktif dalam katalog toko.
- **Produk dengan Stok Menipis:** Indikator jumlah produk yang stok fisiknya $\le$ batas minimum (`min_stock_alert`), lengkap dengan daftar cepat barang yang butuh segera di-restock.

### FR-03: Product & Category Management
- CRUD master produk: Barcode unik, Nama Produk, Kategori, Harga Beli (HPP), Harga Jual, Stok Saat Ini, Batas Minimum Stok Alert.
- CRUD master kategori barang.
- Validasi bisnis ketat:
  - Harga beli $\ge 0$.
  - Harga jual $\ge$ harga beli (mencegah kerugian harga modal).
  - Stok awal $\ge 0$.
- Pencarian cepat produk berdasarkan barcode atau teks nama produk.

### FR-04: Point of Sale (Kasir)
- Pencarian produk via barcode scanner USB atau ketik nama pada form pencarian.
- Keranjang belanja interaktif:
  - Penambahan kuantitas produk otomatis jika barcode di-scan ulang.
  - Pengubahan jumlah item secara langsung atau melalui tombol `+` / `-`.
  - Penghapusan item dari keranjang.
- Validasi stok realtime: Mencegah penambahan kuantitas belanja melebihi sisa stok fisik di database.
- Perhitungan otomatis subtotal belanja, diskon per item/faktur, dan grand total.
- Dukungan metode pembayaran ganda:
  - **Tunai (Cash):** Input nominal uang dari pembeli, tombol cepat pecahan uang rupiah, validasi uang cukup, kalkulasi kembalian otomatis.
  - **QRIS / Digital:** Input/generate nomor referensi transaksi digital dan verifikasi pelunasan.
- Penyimpanan transaksi kasir secara **atomik** menggunakan kontrol transaksi JDBC (`Connection.setAutoCommit(false)`, `commit()`, `rollback()`).
- Pemotongan otomatis stok barang yang terjual dalam unit transaksi yang sama.
- Tampilan dialog struk ringkas setelah transaksi berhasil.

### FR-05: Inventory Management
- **Stock-In (Barang Masuk):** Pencatatan penerimaan stok baru dari pemasok/supplier yang menambah saldo stok produk.
- **Stock Adjustment (Opname):** Penyesuaian koreksi stok fisik jika ada barang rusak, hilang, atau kadaluarsa, disertai catatan alasan.
- **Riwayat Perubahan Stok (Audit Trail):** Tabel log `stock_movements` yang mencatat setiap mutasi (jenis mutasi, kuantitas, stok sebelum, stok sesudah, waktu, dan staf penanggung jawab).
- **Peringatan Stok Menipis:** Penandaan visual otomatis pada produk yang berada pada atau di bawah batas `min_stock_alert`.

### FR-06: Transaction History & Pembatalan Transaksi
- Tabel riwayat transaksi dengan kolom Nomor Faktur, Tanggal/Waktu, Kasir, Metode Bayar, Grand Total, dan Status.
- Filter pencarian berdasarkan rentang tanggal (`Date From` s/d `Date To`) dan pencarian nomor faktur.
- Tampilan panel detail transaksi yang memuat daftar rincian barang belanjaan.
- **Fitur Khusus Admin - Pembatalan Transaksi:**
  - Hanya dapat dieksekusi oleh pengguna berstatus `Admin`.
  - Memerlukan input alasan pembatalan.
  - Mengubah status transaksi menjadi `CANCELLED`.
  - Secara otomatis **mengembalikan stok produk** (*stock restoration*) ke tabel `products` dan mencatat entri `CANCELLATION_RETURN` pada log `stock_movements`.
  - Seluruh alur pembatalan dieksekusi dalam satu transaksi atomik database MySQL.

### FR-07: Reports (Pelaporan Finansial & Inventaris)
- **Laporan Penjualan Harian:** Rekapitulasi transaksi, total omset, modal HPP, dan laba kotor hari ini.
- **Laporan Penjualan Bulanan:** Tren penjualan per bulan untuk evaluasi performa toko.
- **Produk Terlaris (Top-Selling Products):** Urutan produk dengan kuantitas penjualan tertinggi dalam periode tertentu.
- **Laporan Persediaan (Inventory Report):** Nilai valuasi total persediaan toko saat ini (total aset barang berdasarkan harga beli).
- **Ringkasan Keuntungan Kotor (Gross Profit Summary):** Kalkulasi akurat laba kotor berdasarkan snapshot harga modal saat transaksi terjadi.

---

## 4. Penerapan Java I/O untuk Konfigurasi Aplikasi

Aplikasi memiliki penerapan Java I/O secara eksplisit dengan satu fokus utama:

> **Membaca file konfigurasi eksternal `config/app.properties` untuk mengatur parameter koneksi database MySQL tanpa menyimpan kredensial di dalam source code.**

### Komponen Teknis Java I/O Wajib:
1. `java.io.File`: Untuk memeriksa keberadaan fisik file konfigurasi pada jalur path `config/app.properties` dan memastikan file dapat dibaca.
2. `java.io.FileInputStream`: Untuk membuka input stream pembacaan file konfigurasi langsung dari filesystem lokal.
3. `java.io.IOException`: Untuk menangkap dan menangani kesalahan I/O (file tidak ditemukan atau tidak dapat dibaca).
4. `java.util.Properties`: Untuk mem-parse pasangan *key-value* konfigurasi database.

### Alur Kerja & Penanganan Error:
- **File Konfigurasi Ditemukan:** Kredensial dibaca dan di-cache dalam memori untuk membentuk JDBC Connection URL (`jdbc:mysql://host:port/dbname`).
- **File Konfigurasi Tidak Ditemukan:** Sistem menampilkan pesan kesalahan yang ramah dan instruktif: *"File konfigurasi config/app.properties tidak ditemukan. Silakan salin dari config/app.properties.example dan lengkapi kredensial MySQL Anda."*
- **Nilai Tidak Valid / Hilang:** Jika properti wajib seperti `db.host` atau `db.name` kosong, aplikasi menghentikan proses startup dengan peringatan spesifik.

---

## 5. Kebutuhan Non-Fungsional (Non-Functional Requirements)

1. **Integritas Transaksi (Atomicity):** Seluruh proses checkout kasir dan pembatalan transaksi wajib menggunakan transaksi database atomik (ACID) untuk mencegah korupsi data.
2. **Keamanan:**
   - Password pengguna tidak boleh disimpan secara plaintext (wajib hash BCrypt).
   - Seluruh query database wajib menggunakan `PreparedStatement` untuk mencegah SQL Injection.
   - Kredensial database tidak boleh di-hardcode di kode program.
3. **Pencegahan Stok Negatif:** Validasi bertingkat pada domain model, service layer, dan constraint database (`CHECK (stock >= 0)`).
4. **Performa:** Waktu pemrosesan pencarian produk dan penambahan ke keranjang $< 100\text{ ms}$.
