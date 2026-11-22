# User Stories - POS_app

Dokumen ini mendefinisikan *user stories* berdasarkan kebutuhan pengguna akhir (Kasir dan Admin) dengan format standar:  
*As a [role], I want to [action], so that [business value].*

---

## 1. Modul Autentikasi & Akun

- **US-AUTH-01 (Login Pengguna):**
  - *As a* Kasir atau Admin,
  - *I want to* login menggunakan username dan password saya,
  - *So that* saya dapat mengakses fitur aplikasi sesuai dengan peran dan wewenang saya.
  - **Acceptance Criteria:**
    - Password divalidasi menggunakan verifikasi hash BCrypt.
    - Menolak akses jika kredensial salah atau akun dinonaktifkan (`is_active = FALSE`).
    - Kasir diarahkan ke layar POS, Admin diarahkan ke Dashboard Manajemen.

- **US-AUTH-02 (Logout Sesi):**
  - *As a* Pengguna aktif,
  - *I want to* logout dari aplikasi,
  - *So that* sesi saya ditutup dengan aman dan staf lain dapat login.

---

## 2. Modul Dashboard

- **US-DASH-01 (Ringkasan Metrik Toko Hari Ini):**
  - *As an* Admin,
  - *I want to* melihat ringkasan omset penjualan hari ini, jumlah transaksi, total SKU produk, dan jumlah produk dengan stok kritis,
  - *So that* saya mendapatkan visibilitas cepat atas performa operasional toko tanpa harus membuka laporan terpisah.

- **US-DASH-02 (Peringatan Stok Menipis pada Dashboard):**
  - *As an* Admin atau Kasir,
  - *I want to* melihat daftar produk yang berada pada batas kritis stok menipis secara instan pada dashboard,
  - *So that* toko dapat segera melakukan pemesanan ulang ke supplier sebelum terjadi kekosongan barang.

---

## 3. Modul Product & Category Management

- **US-PROD-01 (Tambah Produk Baru):**
  - *As an* Admin,
  - *I want to* mendaftarkan produk baru lengkap dengan barcode, nama, kategori, harga modal beli, harga jual, dan batas minimum stok,
  - *So that* barang dapat mulai dijual oleh kasir.
  - **Acceptance Criteria:**
    - Barcode harus unik di seluruh sistem.
    - Harga jual harus lebih besar atau sama dengan harga beli.
    - Stok awal harus $\ge 0$.

- **US-PROD-02 (Ubah & Hapus Produk):**
  - *As an* Admin,
  - *I want to* memperbarui data produk atau menonaktifkan produk lama,
  - *So that* katalog barang selalu akurat.
  - **Acceptance Criteria:**
    - Produk yang sudah memiliki riwayat transaksi tidak boleh di-*hard delete* (mencegah pelanggaran integritas referensial foreign key).

- **US-PROD-03 (Manajemen Kategori Barang):**
  - *As an* Admin,
  - *I want to* membuat dan mengedit kategori produk,
  - *So that* produk dapat dikelompokkan dengan rapi.

---

## 4. Modul Point of Sale (Kasir)

- **US-POS-01 (Pencarian Produk & Scan Barcode):**
  - *As a* Kasir,
  - *I want to* memindai barcode atau mengetikkan nama barang pada kolom pencarian,
  - *So that* produk langsung masuk ke dalam keranjang belanja.
  - **Acceptance Criteria:**
    - Jika barang ditemukan dan stok tersedia, produk ditambahkan ke keranjang.
    - Jika barang sudah ada di keranjang, kuantitas bertambah 1.
    - Jika sisa stok tidak mencukupi, sistem menampilkan peringatan dan menolak penambahan.

- **US-POS-02 (Pengaturan Kuantitas & Hapus Item):**
  - *As a* Kasir,
  - *I want to* mengubah jumlah barang atau menghapus item yang dibatalkan pelanggan,
  - *So that* keranjang belanja selalu sesuai dengan fisik barang yang dibeli.

- **US-POS-03 (Pembayaran Tunai & Hitung Kembalian):**
  - *As a* Kasir,
  - *I want to* memilih pembayaran tunai dan menginput nominal uang dari pembeli,
  - *So that* uang kembalian dihitung secara otomatis dan akurat.
  - **Acceptance Criteria:**
    - Sistem memvalidasi nominal bayar $\ge$ grand total.
    - Kembalian dihitung: `uang_diterima - grand_total`.

- **US-POS-04 (Pembayaran QRIS / Digital):**
  - *As a* Kasir,
  - *I want to* memilih metode pembayaran QRIS dan mencatat referensi transaksi digital,
  - *So that* transaksi nontunai tercatat dengan tertib tanpa selisih uang fisik kasir.

- **US-POS-05 (Finalisasi Transaksi & Pemotongan Stok Atomik):**
  - *As a* Kasir,
  - *I want to* menyelesaikan transaksi dan mencetak struk nota,
  - *So that* transaksi tersimpan permanen dan stok produk otomatis terpotong di database secara aman.

---

## 5. Modul Inventory Management

- **US-INV-01 (Pencatatan Stok Masuk / Restock):**
  - *As an* Admin,
  - *I want to* mencatat penambahan stok barang yang baru datang dari supplier,
  - *So that* saldo stok produk bertambah dan tercatat dalam log audit mutasi.

- **US-INV-02 (Penyesuaian Stok Opname Fisik):**
  - *As an* Admin,
  - *I want to* mengoreksi jumlah stok barang yang rusak atau kadaluarsa disertai catatan alasan,
  - *So that* catatan sistem selalu sesuai dengan fisik gudang.

- **US-INV-03 (Audit Log Mutasi Persediaan):**
  - *As an* Admin,
  - *I want to* melihat riwayat lengkap seluruh mutasi stok barang (*stock movements*),
  - *So that* setiap perubahan stok dapat dilacak penanggung jawab dan penyebabnya.

---

## 6. Modul Transaction History & Pembatalan Transaksi

- **US-HIST-01 (Melihat Riwayat & Filter Tanggal):**
  - *As an* Admin atau Kasir,
  - *I want to* mencari transaksi lama berdasarkan nomor faktur dan rentang tanggal,
  - *So that* saya dapat melihat kembali rincian transaksi belanja pelanggan.

- **US-HIST-02 (Pembatalan Transaksi oleh Admin & Pengembalian Stok):**
  - *As an* Admin,
  - *I want to* membatalkan transaksi yang keliru atau barangnya diretur oleh pembeli,
  - *So that* status transaksi berubah menjadi `CANCELLED` dan seluruh stok barang yang dibeli otomatis dikembalikan (*restored*) ke saldo produk.
  - **Acceptance Criteria:**
    - Hanya Admin yang memiliki akses membatalkan transaksi.
    - Kasir biasa tidak diperkenankan membatalkan transaksi yang sudah lunas.
    - Pengembalian stok dan pembaruan status transaksi berjalan dalam 1 transaksi atomik database MySQL.
    - Mencatat entri mutasi `CANCELLATION_RETURN` pada log mutasi stok.

---

## 7. Modul Reports

- **US-REP-01 (Laporan Penjualan Harian & Bulanan):**
  - *As an* Admin,
  - *I want to* melihat rekapitulasi penjualan harian dan bulanan,
  - *So that* saya dapat mengevaluasi performa pendapatan toko secara berkala.

- **US-REP-02 (Laporan Produk Terlaris):**
  - *As an* Admin,
  - *I want to* mengetahui daftar produk dengan volume penjualan tertinggi,
  - *So that* saya dapat memprioritaskan pemesanan stok untuk barang-barang favorit pelanggan.

- **US-REP-03 (Laporan Persediaan & Estimasi Keuntungan Kotor):**
  - *As an* Admin,
  - *I want to* melihat valuasi total nilai persediaan barang dan ringkasan laba kotor,
  - *So that* saya memahami kesehatan finansial dan margin keuntungan toko.

---

## 8. Penerapan Java I/O (Sistem Konfigurasi)

- **US-IO-01 (Membaca Konfigurasi Eksternal):**
  - *As a* Pengembang atau Pengguna Aplikasi,
  - *I want to* aplikasi membaca kredensial database dari file `config/app.properties` menggunakan `FileInputStream`,
  - *So that* kredensial database tidak tertulis di kode sumber dan dapat disesuaikan di berbagai komputer tanpa kompilasi ulang.

