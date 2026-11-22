# Acceptance Criteria & Sign-Off - POS_app

Kriteria penerimaan formal untuk aplikasi desktop **POS_app** berbasis **Java I/O dan file CSV**:

- [x] Aplikasi dikembangkan murni menggunakan bahasa **Java 21**, **JavaFX 21**, dan **Maven**.
- [x] Persistensi data menggunakan berkas CSV lokal pada direktori `data/` tanpa menggunakan MySQL, SQL, JDBC, atau server database apapun.
- [x] Seluruh operasi file memanfaatkan kelas dari package `java.io` (`File`, `FileReader`, `BufferedReader`, `FileWriter`, `BufferedWriter`, `FileInputStream`, `IOException`) dengan pola *try-with-resources*.
- [x] Memiliki parser CSV mandiri yang aman terhadap karakter koma dan tanda kutip ganda (tidak menggunakan `String.split(",")`).
- [x] Seluruh 7 modul utama berfungsi penuh:
  - Authentication (Login/Logout dengan hash BCrypt, RBAC Admin vs Kasir).
  - Dashboard (Omset hari ini, jumlah transaksi, total produk, peringatan stok menipis).
  - Product & Category Management (CRUD produk dan kategori via CSV).
  - Point of Sale (Scan barcode, keranjang dinamis, kalkulasi kembalian, cetak struk).
  - Inventory Management (Restock, opname, log mutasi audit).
  - Transaction History (Riwayat penjualan, filter tanggal, pembatalan nota oleh Admin disertai restorasi stok otomatis).
  - Reports (Rekapitulasi penjualan harian, bulanan, produk terlaris, valuasi persediaan).
- [x] Seluruh unit test otomatis JUnit 5 lulus 100% (*exit code 0*).
