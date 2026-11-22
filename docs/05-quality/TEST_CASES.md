# Test Cases Specification - POS_app

Dokumen ini mendefinisikan skenario uji unit (*test cases*) untuk aplikasi **POS_app** berbasis Java I/O dan penyimpanan CSV.

---

## 1. Test Cases: Java I/O & CSV Engine

| Test ID | Skenario Uji | Kondisi / Input | Hasil yang Diharapkan |
| :--- | :--- | :--- | :--- |
| **TC-CSV-01** | Parsing baris standar | `"1,8991,Indomie,3000"` | Menghasilkan list 4 elemen tepat. |
| **TC-CSV-02** | Parsing nilai dengan koma dalam tanda kutip | `"1,\"Beras, Ramos\",50000"` | Elemen kedua: `"Beras, Ramos"` tanpa terpotong. |
| **TC-CSV-03** | Parsing nilai dengan tanda kutip ganda | `"1,\"Sabun \"\"Lifebuoy\"\"\",4500"` | Elemen kedua: `"Sabun \"Lifebuoy\""`. |
| **TC-IO-01** | Membaca konfigurasi `AppConfig` via `FileInputStream` | File properties valid | Mengembalikan nilai properti tepat. |
| **TC-IO-02** | File konfigurasi tidak ditemukan | Path file fiktif | Melempar `RuntimeException` informatif. |

---

## 2. Test Cases: Domain Model & Aturan Bisnis

| Test ID | Skenario Uji | Kondisi / Input | Hasil yang Diharapkan |
| :--- | :--- | :--- | :--- |
| **TC-MOD-01** | Invarian harga produk | Harga jual < Harga beli | Melempar `IllegalArgumentException`. |
| **TC-MOD-02** | Mencegah stok negatif | Mengurangi stok melebihi saldo | Melempar `IllegalStateException`, stok tidak berubah. |
| **TC-MOD-03** | Pembayaran Tunai uang pas / lebih | Bayar >= grand total | Sukses, menghitung kembalian tepat. |
| **TC-MOD-04** | Pembayaran Tunai uang kurang | Bayar < grand total | Melempar `IllegalArgumentException`. |
| **TC-MOD-05** | Pembayaran QRIS presisi | Bayar == grand total | Status settled = true. |
| **TC-UI-01** | Penambahan belanja via katalog `+` | Klik tombol `➕` pada baris produk katalog | Produk masuk ke keranjang belanja dengan kuantitas 1 tanpa scan barcode. |
| **TC-UI-02** | Penyesuaian inline Qty di keranjang | Klik tombol `+` / `-` pada tabel keranjang | Kuantitas bertambah / berkurang, subtotal dan grand total ter-update otomatis. |
| **TC-UI-03** | Auto-generate barcode produk | Klik tombol `➕ Auto Barcode` pada form produk | Terisi kode barcode unik 13 digit (awalan `899`) yang belum pernah terdaftar. |

---

## 3. Test Cases: Layanan Bisnis & Multi-File Consistency

| Test ID | Skenario Uji | Kondisi / Input | Hasil yang Diharapkan |
| :--- | :--- | :--- | :--- |
| **TC-SVC-01** | Checkout kasir sukses | Beli 5 unit dari stok 20 | Nota tersimpan di CSV, sisa stok menjadi 15, tercatat log mutasi `SALE`. |
| **TC-SVC-02** | Checkout gagal karena stok kurang | Minta beli melebihi stok | Ditolak sebelum berkas CSV tersentuh. |
| **TC-SVC-03** | Pembatalan transaksi oleh Admin | Transaksi status `COMPLETED` | Status menjadi `CANCELLED`, seluruh stok item belanja direstorasi, tercatat log `CANCELLATION_RETURN`. |
| **TC-SVC-04** | Kasir mencoba membatalkan transaksi | Kasir role | Ditolak dengan `IllegalArgumentException`. |
| **TC-SVC-05** | Laporan penjualan harian & laba kotor | Transaksi pada hari aktif | Total omset dan laba kotor (omset - snapshot HPP) terhitung akurat. |
