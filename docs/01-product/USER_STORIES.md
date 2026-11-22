# User Stories - POS_app

Dokumen ini mendefinisikan *user stories* berdasarkan kebutuhan pengguna akhir (Kasir dan Admin) pada arsitektur berbasis **Java I/O dan file CSV**.

---

## 1. Modul Autentikasi & Akun

- **US-AUTH-01 (Login Pengguna dari CSV):**
  - *As a* Kasir atau Admin,
  - *I want to* login menggunakan username dan password saya yang tersimpan di `data/users.csv`,
  - *So that* saya dapat mengakses fitur aplikasi sesuai dengan peran saya.
  - **Acceptance Criteria:**
    - Password diverifikasi menggunakan hash BCrypt.
    - Menolak akses jika kredensial salah atau akun dinonaktifkan (`is_active = false`).

- **US-AUTH-02 (Logout Sesi):**
  - *As a* Pengguna aktif,
  - *I want to* logout dari aplikasi,
  - *So that* sesi saya ditutup dengan aman.

---

## 2. Modul Dashboard

- **US-DASH-01 (Ringkasan Metrik Toko Hari Ini):**
  - *As an* Admin,
  - *I want to* melihat ringkasan omset penjualan hari ini, jumlah transaksi, total produk, dan produk stok kritis di dashboard,
  - *So that* saya mendapatkan visibilitas cepat atas performa operasional toko.

---

## 3. Modul Product & Category Management

- **US-PROD-01 (Tambah Produk Baru ke CSV):**
  - *As an* Admin,
  - *I want to* mendaftarkan produk baru ke berkas `data/products.csv`,
  - *So that* barang dapat mulai dijual oleh kasir.
  - **Acceptance Criteria:** Barcode tidak boleh duplikat, harga jual $\ge$ harga beli, stok awal $\ge 0$.

- **US-PROD-02 (Manajemen Kategori):**
  - *As an* Admin,
  - *I want to* mengelola kategori barang pada `data/categories.csv`,
  - *So that* produk terkelompokkan dengan rapi.

---

## 4. Modul Point of Sale (Kasir)

- **US-POS-01 (Pencarian Produk & Scan Barcode):**
  - *As a* Kasir,
  - *I want to* memindai barcode atau mencari nama produk,
  - *So that* barang masuk ke keranjang belanja dengan validasi ketersediaan stok fisik.

- **US-POS-02 (Pembayaran Tunai & QRIS):**
  - *As a* Kasir,
  - *I want to* memproses pembayaran tunai dengan kalkulasi kembalian otomatis atau mencatat pembayaran QRIS,
  - *So that* transaksi selesai dan pelanggan menerima struk belanja.

- **US-POS-03 (Penyimpanan Transaksi Konsisten):**
  - *As a* Kasir,
  - *I want to* nota tersimpan ke `data/transactions.csv` dan `data/transaction_details.csv`, serta stok di `data/products.csv` otomatis terpotong,
  - *So that* data penjualan dan persediaan selalu akurat.

---

## 5. Modul Inventory Management

- **US-INV-01 (Restock & Opname Penyesuaian Stok):**
  - *As an* Admin,
  - *I want to* mencatat barang masuk atau mengoreksi stok opname,
  - *So that* perubahan stok fisik tercatat ke `data/stock_movements.csv`.

---

## 6. Modul Transaction History & Pembatalan Transaksi

- **US-HIST-01 (Riwayat Penjualan & Filter Tanggal):**
  - *As an* Admin atau Kasir,
  - *I want to* mencari transaksi lama berdasarkan nomor nota atau tanggal.

- **US-HIST-02 (Pembatalan Transaksi oleh Admin & Restorasi Stok):**
  - *As an* Admin,
  - *I want to* membatalkan nota transaksi yang keliru,
  - *So that* status transaksi berubah menjadi `CANCELLED` dan seluruh stok barang yang dibeli otomatis dikembalikan ke `data/products.csv`.

---

## 7. Modul Reports

- **US-REP-01 (Laporan Keuangan & Valuasi Persediaan):**
  - *As an* Admin,
  - *I want to* melihat laporan penjualan harian, bulanan, produk terlaris, dan valuasi aset persediaan yang dihitung langsung dari berkas CSV.
