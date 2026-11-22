# Test Cases Specification - POS_app

Dokumen ini mendefinisikan rincian skenario pengujian unit (*test cases*) untuk modul domain, logika bisnis, transaksi atomik, dan pembacaan konfigurasi Java I/O.

---

## 1. Test Cases: Java I/O Configuration (`AppConfigTest`)

| Test ID | Skenario Uji | Input / Kondisi | Hasil yang Diharapkan |
| :--- | :--- | :--- | :--- |
| **TC-IO-01** | Membaca konfigurasi valid menggunakan `FileInputStream` | File `config/app.properties` berisi `db.host=localhost`, `db.name=pos_app_db` | Properti terbaca sempurna, `AppConfig.get("db.host")` mengembalikan `"localhost"`. |
| **TC-IO-02** | Penanganan file konfigurasi tidak ditemukan | File `config/app.properties` tidak ada di filesystem | Melempar `RuntimeException` informatif dengan petunjuk menyalin dari `app.properties.example`. |
| **TC-IO-03** | Penanganan properti wajib yang hilang | `db.host` dihilangkan dari file properti | Melempar `IllegalStateException` ("Konfigurasi wajib hilang: db.host"). |

---

## 2. Test Cases: Domain Model & Aturan Bisnis

### 2.1 Model Produk (`ProductTest`)
| Test ID | Skenario Uji | Input / Kondisi | Hasil yang Diharapkan |
| :--- | :--- | :--- | :--- |
| **TC-PROD-01** | Pembuatan produk valid | Beli = 10000, Jual = 15000, Stok = 20 | Objek `Product` berhasil dibuat. |
| **TC-PROD-02** | Validasi harga jual < harga beli | Beli = 20000, Jual = 15000 | Melempar `IllegalArgumentException`. |
| **TC-PROD-03** | Pengurangan stok normal | Stok awal = 10, reduceStock(3) | Sisa stok = 7. |
| **TC-PROD-04** | Pengurangan stok melebihi saldo fisik | Stok awal = 5, reduceStock(8) | Melempar `IllegalStateException` (stok tidak mencukupi, mencegah stok negatif). |
| **TC-PROD-05** | Deteksi peringatan stok kritis | Stok = 4, minStockAlert = 5 | Method `isLowStock()` mengembalikan `true`. |

### 2.2 Transaksi & Komposisi Keranjang (`TransactionTest`)
| Test ID | Skenario Uji | Input / Kondisi | Hasil yang Diharapkan |
| :--- | :--- | :--- | :--- |
| **TC-TRANS-01** | Tambah item baru ke keranjang | Produk A (Rp 5.000) Qty 2 | Keranjang berisi 1 item, Grand Total = Rp 10.000. |
| **TC-TRANS-02** | Tambah produk yang sama berulang kali | Scan Produk A Qty 2, lalu Produk A Qty 3 | Keranjang tetap 1 baris item dengan total Qty = 5, Grand Total = Rp 25.000 (tidak ada duplikasi baris). |
| **TC-TRANS-03** | Hapus item dari keranjang | Hapus Produk A dari keranjang | Keranjang kosong, Grand Total = Rp 0. |

### 2.3 Pembayaran Polimorfik (`PaymentTest`)
| Test ID | Skenario Uji | Input / Kondisi | Hasil yang Diharapkan |
| :--- | :--- | :--- | :--- |
| **TC-PAY-01** | Pembayaran Tunai uang cukup | Grand Total = Rp 75.000, Bayar = Rp 100.000 | `processPayment()` sukses, `getChange()` = Rp 25.000. |
| **TC-PAY-02** | Pembayaran Tunai uang kurang | Grand Total = Rp 50.000, Bayar = Rp 40.000 | Melempar `IllegalArgumentException` ("Uang tunai kurang"). |
| **TC-PAY-03** | Pembayaran QRIS nominal pas | Grand Total = Rp 60.000, Bayar = Rp 60.000, Ref = "QRIS123" | `processPayment()` sukses, status settled = true. |
| **TC-PAY-04** | Pembayaran QRIS nominal tidak sesuai | Grand Total = Rp 60.000, Bayar = Rp 70.000 | Melempar `IllegalArgumentException` (QRIS harus bernilai presisi). |

---

## 3. Test Cases: Layanan Bisnis & Keamanan

### 3.1 Layanan Autentikasi (`AuthServiceTest`)
| Test ID | Skenario Uji | Input / Kondisi | Hasil yang Diharapkan |
| :--- | :--- | :--- | :--- |
| **TC-AUTH-01** | Login dengan password valid | Username "admin", Password "admin123" | Mengembalikan objek `Admin` aktif, sesi terbentuk. |
| **TC-AUTH-02** | Login dengan password salah | Username "admin", Password "wrongpass" | Mengembalikan `null` atau melempar `AuthenticationException`. |
| **TC-AUTH-03** | Pembatasan menu berdasarkan peran | User = `Cashier`, panggil `canAccessAdminMenu()` | Mengembalikan `false`. |

### 3.2 Pembatalan Transaksi oleh Admin (`TransactionServiceTest`)
| Test ID | Skenario Uji | Input / Kondisi | Hasil yang Diharapkan |
| :--- | :--- | :--- | :--- |
| **TC-CANC-01** | Admin membatalkan transaksi | User = `Admin`, Transaksi status = `COMPLETED` | Transaksi berubah jadi `CANCELLED`, stok barang direstorasi, tercatat di `stock_movements`. |
| **TC-CANC-02** | Kasir mencoba membatalkan transaksi | User = `Cashier`, Transaksi status = `COMPLETED` | Ditolak dengan `AccessDeniedException` (hanya Admin yang berwenang). |
| **TC-CANC-03** | Membatalkan transaksi yang sudah pernah dibatalkan | Transaksi status = `CANCELLED` | Ditolak dengan `IllegalStateException`. |

