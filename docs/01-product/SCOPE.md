# Project Scope & Boundaries - POS_app

Dokumen ini mendefinisikan batasan ruang lingkup proyek (*in-scope*) dan hal-hal yang berada di luar ruang lingkup (*out-of-scope*) untuk menjaga fokus pengembangan, mencegah *scope creep*, dan memastikan target pengiriman tercapai sesuai standar proyek tugas akhir Java OOP.

---

## 1. Fitur Dalam Lingkup (In-Scope)

### 1.1 Modul Fungsional Utama
1. **Autentikasi & Otorisasi Pengguna:**
   - Single-device desktop login dan logout.
   - Pembedaan peran: `Admin` (akses penuh) dan `Cashier` (akses kasir).
   - Enkripsi password menggunakan algoritma hashing BCrypt (`jBCrypt`).
2. **Dashboard:**
   - Ringkasan omset penjualan hari ini.
   - Jumlah total transaksi hari ini.
   - Jumlah total katalog produk.
   - Indikator produk dengan stok menipis (*low stock alert*).
3. **Manajemen Produk & Kategori:**
   - CRUD master produk: barcode unik, nama, kategori, harga beli, harga jual, stok, minimum stok.
   - CRUD kategori produk.
   - Pencarian cepat produk (barcode dan teks).
   - Validasi bisnis: harga jual $\ge$ harga beli, harga $\ge 0$, stok $\ge 0$.
4. **Point of Sale (Kasir):**
   - Scanning barcode produk atau pencarian manual.
   - Keranjang belanja dinamis (tambah, edit kuantitas, hapus item).
   - Validasi ketersediaan stok fisik secara realtime.
   - Perhitungan subtotal, diskon, dan grand total.
   - Pembayaran Tunai (validasi nominal dan hitung uang kembalian otomatis).
   - Pembayaran QRIS / Digital (pencatatan nomor referensi pembayaran).
   - Penyimpanan transaksi kasir secara **atomik** dengan kontrol JDBC (`Connection.setAutoCommit(false)`, `commit()`, `rollback()`).
   - Pemotongan stok produk otomatis saat transaksi tersimpan.
5. **Manajemen Persediaan (Inventory):**
   - Pencatatan stok masuk (*Stock-in* / Restock).
   - Penyesuaian koreksi stok fisik (*Stock Adjustment*) dengan catatan alasan.
   - Log audit mutasi persediaan barang (`stock_movements`).
   - Peringatan dini stok menipis.
6. **Riwayat Transaksi (Transaction History):**
   - Pencarian riwayat transaksi berdasarkan nomor nota/faktur dan filter rentang tanggal.
   - Rincian detail item barang per transaksi.
   - **Fitur Khusus Admin:** Pembatalan transaksi (*Transaction Cancellation*) disertai pengembalian stok barang otomatis (*stock restoration*).
7. **Pelaporan (Reports):**
   - Laporan penjualan harian.
   - Laporan penjualan bulanan.
   - Laporan produk terlaris (*Top-selling products*).
   - Laporan persediaan stok barang.
   - Ringkasan estimasi laba kotor (*Gross Profit*).

### 1.2 Penerapan Java I/O (Fitur Tunggal & Eksplisit)
- **Fokus Tunggal:** Membaca konfigurasi aplikasi dan koneksi database MySQL dari file eksternal:
  `config/app.properties`
- **Kelas Wajib yang Diterapkan:**
  - `java.io.File`
  - `java.io.FileInputStream`
  - `java.io.IOException`
  - `java.util.Properties`
- **Tujuan:** Mencegah hardcoded database credentials dan memisahkan konfigurasi lingkungan dari kode program Java.

---

## 2. Fitur di Luar Lingkup (Out-of-Scope)

Untuk menjaga kesederhanaan, kestabilan, dan kepraktisan proyek tugas akhir mahasiswa, hal-hal berikut secara eksplisit **TIDAK TERMASUK** dalam ruang lingkup aplikasi:

1. **Fitur Java I/O Tambahan di Luar Pembacaan Konfigurasi:**
   - Tidak ada fitur ekspor file CSV sebagai fitur wajib.
   - Tidak ada penyimpanan file struk berekstensi TXT secara otomatis ke disk.
   - Tidak ada custom file-based logging framework.
   - Seluruh fokus implementasi Java I/O hanya pada pembacaan file `config/app.properties` menggunakan `FileInputStream`.
2. **Integrasi Cloud Payment Gateway Live (Webhook):**
   - Pembayaran QRIS pada aplikasi bersifat pencatatan dan verifikasi manual (kasir memverifikasi status pembayaran dari bukti transfer atau aplikasi perbankan), bukan integrasi API webhook live seperti Midtrans atau Xendit.
3. **Multi-Cabang & Sinkronisasi Cloud:**
   - Aplikasi dirancang untuk operasional satu toko (*single-store*) berbasis LAN atau localhost.
4. **Sistem Hutang/Piutang Pelanggan (Kasbon / Kredit):**
   - Transaksi kasir hanya menerima pembayaran lunas instan.
5. **Modul Penggajian (Payroll) & Absensi Karyawan:**
   - Tidak mencakup sistem absensi atau penggajian staf.

---

## 3. Matriks Prioritas Fitur (MoSCoW Matrix)

| Prioritas | Fitur / Komponen |
| :--- | :--- |
| **Must Have** | - Autentikasi Pengguna (Admin & Kasir) dengan BCrypt.<br/>- Dashboard Ringkasan Metrik Toko.<br/>- CRUD Produk & Kategori (validasi harga & stok non-negatif).<br/>- Modul Kasir POS (Scanner, Cart, Tunai & Kembalian, QRIS).<br/>- Transaksi Database Atomik JDBC (Commit & Rollback).<br/>- Pemotongan Stok Otomatis.<br/>- Pembacaan Konfigurasi Java I/O (`FileInputStream`, `File`, `Properties`).<br/>- Pembatalan Transaksi oleh Admin dengan Pengembalian Stok Otomatis. |
| **Should Have** | - Filter Tanggal pada Riwayat Transaksi.<br/>- Peringatan Stok Menipis (*Low Stock Alert*).<br/>- Audit Trail Mutasi Stok (`stock_movements`).<br/>- Laporan Penjualan Harian & Bulanan.<br/>- Laporan Produk Terlaris & Laba Kotor. |
| **Could Have** | - Shortcut Keyboard Kasir (`F1` cari, `Enter` bayar, `Esc` batal).<br/>- Pratinjau Struk Nota Monospace di Layar. |
| **Won't Have (v1)** | - Live Payment Gateway Webhooks.<br/>- Multi-Outlet Cloud Sync.<br/>- Aplikasi Mobile untuk Pembeli.<br/>- File export CSV atau file logging I/O manual. |
