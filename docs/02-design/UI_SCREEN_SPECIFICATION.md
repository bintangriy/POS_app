# UI Screen Specification - POS_app

Dokumen ini mendefinisikan rincian spesifikasi layar, komponen visual, interaksi event handling, dan pemetaan file FXML untuk seluruh modul aplikasi desktop **POS_app**.

---

## 1. Daftar Tampilan & Pemetaan File FXML

| Layar / View | File FXML | Controller Terkait | Pengguna |
| :--- | :--- | :--- | :--- |
| **Login Screen** | `views/login.fxml` | `LoginController` | Semua (Admin & Kasir) |
| **Main Layout (Shell Navigasi)**| `views/main_layout.fxml` | `MainDashboardController` | Semua |
| **Dashboard Utama** | `views/dashboard.fxml` | `DashboardController` | Admin & Kasir |
| **POS / Kasir** | `views/pos.fxml` | `PosController` | Kasir & Admin |
| **Katalog Produk & Kategori** | `views/products.fxml` | `ProductController` | Admin |
| **Dialog Tambah/Edit Produk** | `views/product_dialog.fxml` | `ProductDialogController` | Admin |
| **Manajemen Stok & Opname** | `views/inventory.fxml` | `InventoryController` | Admin |
| **Riwayat Transaksi** | `views/transaction_history.fxml` | `TransactionHistoryController`| Kasir & Admin |
| **Dialog Pembatalan Transaksi**| `views/cancel_dialog.fxml` | `CancelDialogController` | Khusus Admin |
| **Laporan & Rekapitulasi** | `views/reports.fxml` | `ReportController` | Admin |
| **Dialog Pratinjau Struk** | `views/receipt_dialog.fxml` | `ReceiptDialogController` | Kasir & Admin |

---

## 2. Spesifikasi Layar Rinci

### 2.1 Layar Login (`views/login.fxml`)
- **Tujuan:** Autentikasi pengguna sebelum masuk ke sistem aplikasi.
- **Komponen Visual:**
  - Card login centered dengan logo aplikasi.
  - `TextField txtUsername`: Input username pengguna.
  - `PasswordField txtPassword`: Input password.
  - `Label lblError`: Notifikasi pesan kesalahan jika login gagal (tersembunyi secara default).
  - `Button btnLogin`: Tombol login utama (Default Button, trigger via `Enter`).
- **Interaksi & Perilaku:**
  - Autofocus pada `txtUsername`.
  - Memverifikasi hash password BCrypt melalui `AuthService`.
  - Jika berhasil, stage diarahkan ke `views/main_layout.fxml`, lalu memuat menu sesuai hak akses peran (`Admin` vs `Cashier`).

### 2.2 Layar Dashboard Utama (`views/dashboard.fxml`)
- **Tujuan:** Menyajikan ringkasan metrik kesehatan operasional toko secara instan.
- **Komponen Visual:**
  - **Metric Card 1 - Omset Hari Ini:** `Label lblTodaySales` (Format Rupiah, misal: `Rp 2.450.000`).
  - **Metric Card 2 - Jumlah Transaksi:** `Label lblTodayTransactions` (misal: `38 Transaksi`).
  - **Metric Card 3 - Total Produk:** `Label lblTotalProducts` (misal: `142 SKU`).
  - **Metric Card 4 - Stok Kritis:** `Label lblLowStockCount` (Warna peringatan Amber/Merah, misal: `4 Produk`).
  - **Tabel Peringatan Stok Menipis:** `TableView tblLowStockProducts` dengan kolom:
    1. Barcode
    2. Nama Produk
    3. Kategori
    4. Sisa Stok (Badge Merah)
    5. Batas Minimum
    6. Tombol Cepat: `Restock Sekarang` (Mengarahkan ke menu inventaris).

### 2.3 Layar Transaksi Kasir (`views/pos.fxml`)
- **Tujuan:** Operasional transaksi kasir belanja harian dengan alur cepat, fleksibel untuk kasir dengan maupun tanpa pemindai barcode.
- **Komponen Visual (Layout 3 Kolom):**
  1. **Kolom Kiri: Panel Katalog Produk Cepat (`tblCatalog`):**
     - `TextField txtCatalogSearch`: Filter pencarian instan berdasarkan nama produk, barcode, atau kategori.
     - `TableView tblCatalog`: Tabel seluruh produk toko (Nama, Harga, Sisa Stok).
     - Tombol inline per baris: **`[ ➕ ]`** (Klik tombol `➕` atau klik ganda baris produk untuk langsung memasukkan barang ke keranjang belanja tanpa perlu memindai barcode).
  2. **Kolom Tengah: Keranjang Belanja (`tblCart`):**
     - `TextField txtSearch`: Input scanner barcode atau ketik manual (shortcut `F1`) + Tombol `Tambah`.
     - `TableView tblCart`: Tabel item belanjaan (Barcode, Nama Barang, Harga Satuan, Qty, Subtotal).
     - Kolom Aksi Keranjang: Tombol inline **`[ - ]`** dan **`[ + ]`** untuk menambah atau mengurangi kuantitas secara langsung per baris.
     - Tombol Aksi Bawah: `➕ (+1)`, `➖ (-1)`, `🗑 Hapus Item`, dan `Batal Transaksi`.
  3. **Kolom Kanan: Panel Pembayaran & Checkout:**
     - `Label lblGrandTotal`: Tampilan total tagihan berukuran besar.
     - `RadioButton rbCash` & `RadioButton rbQris`: Pilihan metode pembayaran.
     - `TextField txtAmountPaid`: Input uang tunai yang diserahkan pelanggan.
     - `FlowPane pnlQuickCash`: Tombol pecahan uang cepat (`Uang Pas`, `10k`, `20k`, `50k`, `100k`).
     - `Label lblChange`: Menampilkan jumlah uang kembalian secara realtime.
     - `Button btnProcessCheckout`: Tombol konfirmasi bayar & cetak nota.

### 2.4 Layar Manajemen Produk & Modal Dialog (`views/products.fxml` & `views/product_dialog.fxml`)
- **Tujuan:** Pengelolaan master katalog produk dan kategori barang.
- **Komponen Visual:**
  - `TextField txtSearchProduct`: Filter pencarian berdasarkan barcode atau nama produk.
  - `ComboBox cmbCategoryFilter`: Filter berdasarkan kategori barang.
  - `Button btnAddProduct`: Membuka modal tambah produk baru.
  - `TableView tblProducts`: Daftar produk dengan penyorotan warna khusus jika stok $\le$ batas minimum.
  - **Modal Dialog Produk (`product_dialog.fxml`):**
    - Input Barcode dilengkapi tombol **`[ ➕ Auto Barcode ]`** untuk men-generate kode barcode acak unik standar retail (`899...`) secara otomatis jika produk UMKM tidak memiliki barcode kemasan pabrik.
    - Input Nama Produk, Kategori, Harga Beli, Harga Jual, Stok Awal, dan Batas Minimum Stok Alert.

### 2.5 Layar Manajemen Stok (`views/inventory.fxml`)
- **Tujuan:** Operasional persediaan, pencatatan barang masuk, dan audit mutasi.
- **Komponen Visual:**
  - Tab 1: **Restock (Barang Masuk):** Form pemilihan produk, input jumlah barang masuk dari supplier, dan tombol simpan penambahan stok.
  - Tab 2: **Stock Adjustment (Opname):** Form koreksi stok fisik dengan dropdown alasan (Rusak, Kadaluarsa, Selisih Hitung).
  - Tab 3: **Log Mutasi Stok:** Tabel log `stock_movements` dengan filter tanggal.

### 2.6 Layar Riwayat Transaksi (`views/transaction_history.fxml`)
- **Tujuan:** Peninjauan kembali transaksi yang telah selesai dan fitur pembatalan nota oleh Admin.
- **Komponen Visual:**
  - `DatePicker dpFromDate` & `DatePicker dpToDate`: Filter rentang waktu.
  - `TextField txtSearchInvoice`: Filter berdasarkan nomor nota.
  - `TableView tblTransactions`: Menampilkan No Faktur, Waktu, Kasir, Tipe Bayar, Grand Total, Status (`COMPLETED` / `CANCELLED`).
  - `TableView tblDetailItems`: Menampilkan rincian barang dari transaksi yang dipilih pada tabel utama.
  - `Button btnReprint`: Mencetak ulang struk nota yang dipilih.
  - `Button btnCancelTransaction`: **(Khusus Admin)** Tombol pembatalan transaksi dengan pengembalian stok otomatis.

### 2.7 Dialog Pembatalan Transaksi (`views/cancel_dialog.fxml`)
- **Tujuan:** Memvalidasi aksi pembatalan transaksi oleh Admin.
- **Komponen Visual:**
  - Teks Peringatan: *"Pembatalan transaksi akan mengembalikan seluruh stok barang ke sistem inventaris secara permanen."*
  - `TextArea txtCancelReason`: Wajib diisi alasan pembatalan (misal: "Barang cacat diretur pelanggan").
  - `Button btnConfirmCancel`: Eksekusi pembatalan transaksi atomik.
  - `Button btnDismiss`: Batal menutup dialog.

### 2.8 Layar Laporan (`views/reports.fxml`)
- **Tujuan:** Rekapitulasi performa penjualan dan evaluasi laba toko.
- **Komponen Visual:**
  - Pilihan Laporan (Tab / Dropdown):
    1. **Penjualan Harian:** Rekap omset, HPP, laba kotor, dan total transaksi per hari.
    2. **Penjualan Bulanan:** Rekap omset dan tren per bulan.
    3. **Produk Terlaris:** Peringkat produk dengan kuantitas penjualan terbanyak.
    4. **Laporan Persediaan:** Total nilai valuasi stok toko.
    5. **Ringkasan Keuntungan Kotor:** Analisis margin keuntungan per periode.
