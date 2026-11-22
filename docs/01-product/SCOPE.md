# Project Scope & Boundaries - POS_app

Dokumen ini mendefinisikan batasan ruang lingkup proyek (*in-scope*) dan hal-hal yang berada di luar ruang lingkup (*out-of-scope*) untuk menjaga fokus pengembangan berbasis **Java I/O dan file CSV**.

---

## 1. Fitur Dalam Lingkup (In-Scope)

### 1.1 Modul Fungsional Utama
1. **Autentikasi & Otorisasi Pengguna:**
   - Single-device desktop login dan logout.
   - Pembedaan peran: `Admin` (akses penuh) dan `Cashier` (akses kasir).
   - Enkripsi password menggunakan algoritma hashing BCrypt (`jBCrypt`).
   - Data disimpan pada `data/users.csv`.
2. **Dashboard:**
   - Ringkasan omset penjualan hari ini.
   - Jumlah total transaksi hari ini.
   - Jumlah total katalog produk.
   - Indikator produk dengan stok menipis (*low stock alert*).
3. **Manajemen Produk & Kategori:**
   - CRUD master produk dan kategori disimpan pada `data/products.csv` dan `data/categories.csv`.
   - Fitur **Auto Barcode Generator** (`[ ➕ Auto Barcode ]`) untuk menghasilkan barcode numerik unik otomatis bagi produk UMKM tanpa kemasan pabrik.
   - Validasi bisnis: harga jual $\ge$ harga beli, harga $\ge 0$, stok $\ge 0$.
4. **Point of Sale (Kasir):**
   - **Katalog Produk Cepat:** Panel visual daftar produk dengan tombol `[ ➕ ]` per baris dan double-click untuk memasukkan produk ke keranjang tanpa perlu scanner barcode.
   - Scanning barcode produk atau pencarian manual teks.
   - Keranjang belanja dinamis dengan tombol inline `[ + ]` dan `[ - ]` per baris item untuk penyesuaian kuantitas cepat.
   - Validasi ketersediaan stok fisik secara realtime.
   - Pembayaran Tunai (validasi nominal dan hitung uang kembalian otomatis).
   - Pembayaran QRIS / Digital (pencatatan nomor referensi pembayaran).
   - Penyimpanan transaksi kasir ke `data/transactions.csv` dan `data/transaction_details.csv`.
   - Pemotongan stok produk otomatis dan pencatatan mutasi ke `data/stock_movements.csv`.
5. **Manajemen Persediaan (Inventory):**
   - Restock barang masuk dan penyesuaian stok opname fisik.
   - Log audit mutasi persediaan barang (`data/stock_movements.csv`).
6. **Riwayat Transaksi (Transaction History):**
   - Pencarian riwayat transaksi berdasarkan nomor nota/faktur dan filter rentang tanggal.
   - Pembatalan transaksi oleh Admin dengan restorasi stok otomatis.
7. **Pelaporan (Reports):**
   - Laporan penjualan harian dan bulanan.
   - Laporan produk terlaris dan valuasi aset persediaan toko.
   - Ringkasan laba kotor (*Gross Profit*).

### 1.2 Penerapan Java I/O (Penyimpanan Berkas CSV)
- Seluruh persistensi data menggunakan format file CSV lokal pada direktori `data/`.
- Membaca dan menulis menggunakan `java.io.File`, `java.io.FileReader`, `java.io.BufferedReader`, `java.io.FileWriter`, dan `java.io.BufferedWriter`.
- Membaca konfigurasi aplikasi dari `config/app.properties` via `java.io.FileInputStream`.

---

## 2. Fitur di Luar Lingkup (Out-of-Scope)

Hal-hal berikut secara eksplisit **TIDAK TERMASUK** dalam ruang lingkup aplikasi:
1. **Penggunaan Database Server:**
   - Tidak menggunakan MySQL, PostgreSQL, SQLite, H2, MongoDB, atau ORM framework apapun.
2. **Integrasi Cloud Payment Gateway Live (Webhook):**
   - Pembayaran QRIS bersifat pencatatan dan konfirmasi manual.
3. **Multi-Cabang & Sinkronisasi Cloud:**
   - Aplikasi beroperasi secara mandiri (*standalone desktop*) pada satu komputer.
4. **Sistem Kasbon / Kredit Pelanggan:**
   - Transaksi kasir hanya menerima pembayaran lunas instan.
