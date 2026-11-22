# Product Requirements Document (PRD) - POS_app

## 1. Identitas & Visi Produk
**POS_app** adalah aplikasi desktop kasir (*Point of Sale*) dan manajemen persediaan barang (*Inventory Management*) yang dirancang khusus untuk Usaha Mikro, Kecil, dan Menengah (UMKM) sektor ritel.

Aplikasi ini dikembangkan sebagai tugas akhir mata kuliah **Java Object-Oriented Programming (OOP)** dengan mengutamakan:
- Arsitektur berorientasi objek murni tanpa *god class*.
- Mekanisme penyimpanan data mandiri berbasis **Java I/O dan file CSV** (tanpa ketergantungan pada MySQL, SQL, JDBC, atau database server luar).
- Antarmuka desktop JavaFX yang cepat, responsif, dan mematuhi kebijakan *Anti-AI-Slop*.

---

## 2. Target Pengguna & Persona

| Peran | Pengguna | Tanggung Jawab & Kebutuhan Utama |
| :--- | :--- | :--- |
| **Cashier (Kasir)** | Staf Operasional Kasir | - Memproses transaksi belanja pelanggan secepat mungkin (scan barcode, kalkulasi total, terima bayar).<br/>- Menghitung uang kembalian secara otomatis.<br/>- Meninjau transaksi pada shift aktifnya. |
| **Admin (Pemilik/Manajer)** | Pemilik Toko / Manajer | - Memantau ringkasan metrik harian pada Dashboard utama.<br/>- Mengelola katalog produk, harga beli, harga jual, dan kategori barang.<br/>- Mengontrol persediaan barang (stok masuk, opname, alert stok menipis).<br/>- Melakukan pembatalan transaksi dengan pengembalian stok otomatis jika terjadi komplain.<br/>- Menganalisis laporan penjualan harian, bulanan, produk terlaris, dan laba kotor. |

---

## 3. Fitur Utama Produk (Functional Requirements)

### FR-01: Autentikasi Pengguna & Role-Based Access Control (RBAC)
- Login berbasis username dan password terenkripsi hash BCrypt.
- Penyimpanan data akun pada berkas `data/users.csv`.
- Pembedaan peran:
  - `Cashier`: Mengakses menu Dashboard kasir ringkas, Transaksi Kasir (POS), dan Riwayat Transaksi Shift.
  - `Admin`: Mengakses seluruh menu manajerial (Dashboard lengkap, Produk, Kategori, Inventaris, Riwayat Transaksi + Pembatalan, Laporan Penjualan & Laba).
- Logout aman dengan pembersihan sesi pengguna di memori.

### FR-02: Dashboard Utama
- **Ringkasan Penjualan Hari Ini:** Menampilkan total nominal omset penjualan yang berhasil diproses hari ini.
- **Jumlah Transaksi:** Total frekuensi transaksi nota penjualan yang berstatus `COMPLETED` pada hari aktif.
- **Jumlah Produk:** Total keseluruhan SKU produk yang terdaftar aktif dalam katalog toko.
- **Produk dengan Stok Menipis:** Indikator jumlah produk yang stok fisiknya $\le$ batas minimum (`min_stock_alert`), lengkap dengan tabel produk yang butuh restock.

### FR-03: Product & Category Management
- CRUD master produk: Barcode unik, Nama Produk, Kategori, Harga Beli (HPP), Harga Jual, Stok Saat Ini, Batas Minimum Stok Alert.
- **Auto Generate Barcode:** Tombol cepat `[ ➕ Auto Barcode ]` untuk membuat kode barcode unik standar retail (awalan `899...`) secara otomatis jika produk UMKM tidak memiliki barcode pabrik.
- CRUD master kategori barang.
- Disimpan pada berkas `data/products.csv` dan `data/categories.csv`.
- Validasi bisnis: Harga jual $\ge$ harga beli, harga dan stok awal $\ge 0$.
- Pencarian cepat produk berdasarkan barcode atau teks nama produk.

### FR-04: Point of Sale (Kasir)
- **Katalog Produk Cepat:** Panel visual daftar produk dengan filter pencarian instan (nama/kategori) dan tombol **`[ ➕ ]`** per baris produk serta dukungan *double-click* untuk memasukkan produk ke keranjang belanja tanpa memerlukan pemindai barcode fisik.
- **Pencarian / Scan Barcode:** Tetap mendukung input barcode scanner USB atau ketik nama pada form pencarian.
- Keranjang belanja interaktif:
  - Tombol aksi inline **`[ + ]`** dan **`[ - ]`** langsung pada setiap baris item keranjang untuk penyesuaian jumlah seketika.
  - Penambahan kuantitas produk otomatis jika barcode di-scan ulang.
  - Penghapusan item dari keranjang dan tombol batal transaksi.
- Validasi stok realtime: Mencegah penambahan kuantitas belanja melebihi sisa stok fisik di database CSV.
- Perhitungan otomatis subtotal belanja, diskon per item/faktur, dan grand total.
- Dukungan metode pembayaran ganda:
  - **Tunai (Cash):** Input nominal uang dari pembeli, tombol cepat pecahan uang rupiah, validasi uang cukup, kalkulasi kembalian otomatis.
  - **QRIS / Digital:** Input/generate nomor referensi transaksi digital dan verifikasi pelunasan.
- Penyimpanan transaksi kasir secara konsisten ke berkas `data/transactions.csv` dan `data/transaction_details.csv`.
- Pemotongan otomatis stok barang yang terjual di `data/products.csv` dan pencatatan log ke `data/stock_movements.csv`.
- Tampilan dialog struk nota setelah transaksi berhasil.

### FR-05: Inventory Management
- **Stock-In (Barang Masuk):** Pencatatan penerimaan stok baru dari pemasok yang menambah saldo stok produk.
- **Stock Adjustment (Opname):** Penyesuaian koreksi stok fisik jika ada barang rusak atau hilang, disertai catatan alasan.
- **Riwayat Perubahan Stok (Audit Trail):** Berkas log `data/stock_movements.csv` yang mencatat setiap mutasi.
- **Peringatan Stok Menipis:** Penandaan visual otomatis pada produk yang berada pada atau di bawah batas `min_stock_alert`.

### FR-06: Transaction History & Pembatalan Transaksi
- Tabel riwayat transaksi dengan filter pencarian berdasarkan rentang tanggal dan nomor faktur.
- Tampilan rincian barang belanjaan per nota.
- **Fitur Khusus Admin - Pembatalan Transaksi:**
  - Hanya dapat dieksekusi oleh pengguna berstatus `Admin`.
  - Memerlukan input alasan pembatalan.
  - Mengubah status transaksi menjadi `CANCELLED`.
  - Secara otomatis **mengembalikan stok produk** (*stock restoration*) ke `data/products.csv` dan mencatat entri `CANCELLATION_RETURN` pada `data/stock_movements.csv`.

### FR-07: Reports (Pelaporan Finansial & Inventaris)
- **Laporan Penjualan Harian:** Rekapitulasi transaksi, total omset, modal HPP, dan laba kotor hari ini.
- **Laporan Penjualan Bulanan:** Tren penjualan per bulan untuk evaluasi performa toko.
- **Produk Terlaris (Top-Selling Products):** Urutan produk dengan kuantitas penjualan tertinggi dalam periode tertentu.
- **Laporan Persediaan (Inventory Report):** Nilai valuasi total persediaan toko saat ini (total aset barang berdasarkan harga beli).
- **Ringkasan Keuntungan Kotor (Gross Profit Summary):** Kalkulasi akurat laba kotor berdasarkan snapshot harga modal saat transaksi terjadi.

---

## 4. Penerapan Java I/O untuk Penyimpanan Data (CSV Storage)

Aplikasi memiliki penerapan Java I/O secara eksplisit sebagai mekanisme utama penyimpanan data:
- Menggunakan kelas-kelas inti: `java.io.File`, `java.io.FileReader`, `java.io.BufferedReader`, `java.io.FileWriter`, `java.io.BufferedWriter`, `java.io.FileInputStream`, dan `java.io.IOException`.
- Operasi berkas ditangani secara aman dengan pola *try-with-resources*.
- Pembaruan berkas menggunakan teknik *atomic temporary file swap* (`.tmp`) untuk mencegah berkas korup jika aplikasi terhenti tiba-tiba.
