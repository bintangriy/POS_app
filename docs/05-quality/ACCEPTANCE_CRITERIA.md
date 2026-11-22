# Acceptance Criteria & Feature Sign-Off - POS_app

Dokumen ini mendefinisikan kriteria penerimaan formal (*Acceptance Criteria*) untuk setiap modul aplikasi **POS_app**. Seluruh kriteria wajib diverifikasi sebelum aplikasi dinyatakan selesai dan siap dipresentasikan.

---

## 1. Kriteria Modul Autentikasi & RBAC
- [ ] Pengguna dapat login menggunakan username dan password yang benar.
- [ ] Kata sandi diverifikasi menggunakan hash BCrypt (tidak ada password plaintext di database).
- [ ] Pengguna dengan peran `CASHIER` hanya dapat mengakses menu Kasir (POS) dan Riwayat Shift. Menu Manajemen Produk, Inventaris, Laporan, dan Pembatalan Transaksi disembunyikan atau dinonaktifkan.
- [ ] Pengguna dengan peran `ADMIN` dapat mengakses seluruh fitur aplikasi.
- [ ] Tombol logout menutup sesi aktif dan mengembalikan layar ke form login.

---

## 2. Kriteria Modul Dashboard
- [ ] Menampilkan ringkasan total omset penjualan hari ini dalam format mata uang Rupiah (`Rp`).
- [ ] Menampilkan jumlah total nota transaksi hari ini.
- [ ] Menampilkan total SKU produk yang aktif dalam katalog.
- [ ] Menampilkan jumlah produk yang memiliki stok $\le$ batas minimum (`min_stock_alert`).
- [ ] Tabel peringatan stok menipis menampilkan produk kritis secara akurat.

---

## 3. Kriteria Modul Manajemen Produk & Kategori
- [ ] Admin dapat menambah, mengubah, dan menghapus kategori produk.
- [ ] Admin dapat mendaftarkan produk baru dengan barcode unik.
- [ ] Sistem menolak jika barcode yang dimasukkan sudah digunakan oleh produk lain.
- [ ] Sistem menolak jika harga jual lebih rendah dari harga beli modal.
- [ ] Sistem menolak jika nilai harga atau stok awal bernilai negatif.
- [ ] Fitur pencarian menyaring tabel produk berdasarkan barcode atau teks nama produk secara instan.

---

## 4. Kriteria Modul Point of Sale (Kasir)
- [ ] Kasir dapat memindai barcode atau memilih barang dari pencarian ke dalam keranjang.
- [ ] Scan ulang barcode produk yang sama otomatis menambah kuantitas barang tanpa membuat baris baru.
- [ ] Sistem menolak penambahan barang ke keranjang jika kuantitas melebihi sisa stok fisik di database.
- [ ] Kalkulasi subtotal, diskon, dan grand total akurat secara matematis.
- [ ] Pembayaran tunai memvalidasi kecukupan uang dan menghitung kembalian otomatis.
- [ ] Pembayaran QRIS mencatat nomor referensi digital.
- [ ] Proses checkout kasir menyimpan data transaksi dan memotong stok produk secara atomik (`commit`/`rollback`).
- [ ] Dialog struk nota belanja muncul setelah transaksi selesai diproses.

---

## 5. Kriteria Modul Manajemen Stok (Inventory)
- [ ] Admin dapat mencatat stok masuk (*stock-in*) dari supplier yang otomatis menambah saldo produk.
- [ ] Admin dapat melakukan koreksi stok opname fisik dengan mencantumkan alasan.
- [ ] Setiap penambahan atau pengurangan stok tercatat secara permanen pada log `stock_movements`.
- [ ] Saldo stok produk tidak pernah bernilai negatif dalam kondisi apapun.

---

## 6. Kriteria Modul Riwayat Transaksi & Pembatalan oleh Admin
- [ ] Tabel riwayat transaksi dapat difilter berdasarkan nomor faktur dan rentang tanggal.
- [ ] Memilih baris transaksi menampilkan rincian barang belanjaan pada panel detail.
- [ ] Fitur pembatalan transaksi hanya dapat diakses oleh peran `ADMIN`.
- [ ] Pembatalan transaksi mengubah status menjadi `CANCELLED` dan secara otomatis mengembalikan stok barang ke saldo produk dalam 1 transaksi atomik database MySQL.
- [ ] Alasan pembatalan dan identitas admin pembatal tersimpan di database.

---

## 7. Kriteria Modul Pelaporan (Reports)
- [ ] Laporan penjualan harian menyajikan omset, total modal (HPP), dan laba kotor hari ini.
- [ ] Laporan penjualan bulanan menampilkan rekapitulasi tren penjualan per bulan.
- [ ] Laporan produk terlaris mengurutkan produk berdasarkan total kuantitas terjual.
- [ ] Laporan persediaan menghitung total valuasi nilai aset barang di toko.

---

## 8. Kriteria Khusus Java I/O Configuration
- [ ] File konfigurasi dibaca dari jalur fisik eksternal: `config/app.properties`.
- [ ] Kode implementasi `AppConfig.java` **secara eksplisit menggunakan `java.io.File`, `java.io.FileInputStream`, `java.io.IOException`, dan `java.util.Properties`**.
- [ ] Tidak ada kredensial database (host, user, password) yang di-hardcode di kode program Java.
- [ ] Jika file `config/app.properties` tidak ditemukan, sistem memberikan pesan kesalahan informatif dan mengarahkan pengguna menyalin dari `config/app.properties.example`.

