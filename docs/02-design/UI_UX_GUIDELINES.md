# UI/UX Guidelines & Anti-AI-Slop Policy - POS_app

Dokumen ini mendefinisikan panduan antarmuka pengguna (UI) dan pengalaman pengguna (UX) untuk aplikasi desktop **POS_app**. Panduan ini menegakkan standar visual yang bersih, fungsional, profesional, serta mematuhi **Anti-AI-Slop Policy**.

---

## 1. Anti-AI-Slop Policy untuk Aplikasi Desktop

Banyak aplikasi yang digenerasi oleh AI menghasilkan antarmuka yang generik, dipenuhi ornamen tidak berguna, animasi lambat, dan kontras warna yang buruk. POS_app menerapkan aturan ketat:

1. **Utamakan Keterbacaan & Kecepatan:** Aplikasi kasir digunakan di lingkungan kerja nyata dengan ritme cepat. Font harus tajam, angka harga harus jelas dan terbaca dari jarak kerja kasir (minimal 60-80 cm dari layar).
2. **Eliminasi Animasi Lambat:** Tidak boleh ada transisi lambat (*laggy transition*) atau efek hover berlebihan yang memperlambat alur scan kasir.
3. **Shortcut Keyboard First:** Seluruh operasional penting di kasir (Focus search, Bayar, Cetak Nota, Batal) wajib memiliki pintasan keyboard (misal: `F1`, `F2`, `Enter`, `Esc`).
4. **Struktur Visual Hierarchy yang Jelas:** Hindari penumpukan modal/popup bertumpuk. Informasi penting (Grand Total, Uang Diterima, Kembalian) harus memiliki ukuran visual paling dominan.
5. **No Fake Elements:** Tidak ada grafik/ikon hiasan tanpa fungsi nyata. Setiap tombol dan teks memiliki tujuan kerja yang jelas.

---

## 2. Palet Warna & Desain Tokens

Sistem warna POS_app menggunakan palet modern bernuansa *Slate / Deep Indigo* yang ramah di mata untuk penggunaan berjam-jam:

| Token | Nilai Hex | Penggunaan |
| :--- | :--- | :--- |
| `--color-primary` | `#2563EB` (Royal Blue) | Tombol aksi utama (Checkout, Simpan, Tambah). |
| `--color-primary-hover` | `#1D4ED8` | State hover tombol primer. |
| `--color-secondary` | `#475569` (Slate) | Tombol sekunder, border, teks pendukung. |
| `--color-success` | `#16A34A` (Emerald) | Notifikasi lunas, indikator stok aman, keuntungan. |
| `--color-warning` | `#D97706` (Amber) | Peringatan stok menipis (*low stock alert*). |
| `--color-danger` | `#DC2626` (Crimson) | Tombol hapus item, pembatalan nota, indikator stok habis. |
| `--color-bg-main` | `#F8FAFC` (Off-White) | Background utama aplikasi. |
| `--color-bg-card` | `#FFFFFF` (Pure White) | Card container, tabel kasir, form input. |
| `--color-text-main` | `#0F172A` (Slate 900) | Teks judul dan data tabel utama. |
| `--color-text-muted` | `#64748B` (Slate 500) | Label sekunder, placeholder input, tanggal nota. |

---

## 3. Tipografi & Ukuran Teks

- **Font Family Utama:** Segoe UI, Inter, Roboto, atau font Sans-Serif sistem bawaan OS yang bersih dan jelas.
- **Ukuran Teks Standar:**
  - `Display / Grand Total:` 28px - 32px (Bold, Monospace/Tabular Numbers untuk angka rupiah).
  - `Header H1 (Nama Modul):` 20px - 22px (Semi-Bold).
  - `Header H2 (Judul Tabel/Kartu):` 15px - 16px (Semi-Bold).
  - `Body / Data Tabel:` 13px - 14px (Regular).
  - `Small / Badge / Shortcut Tip:` 11px - 12px (Medium).

---

## 4. Pola Tata Letak Layar (Layout Patterns)

### 4.1 Layar Utama Kasir (POS Screen) - Pola 2-Kolom Split
- **Kolom Kiri (65% Lebar Layar):**
  - Baris atas: Input barcode/nama produk dengan autofocus permanen.
  - Bagian tengah: Tabel keranjang belanja (`TableView`) dengan kolom: Barcode, Nama Produk, Harga Satuan, Kuantitas (+/-), Subtotal, dan tombol Aksi (Hapus).
  - Baris bawah: Informasi ringkasan kuantitas barang dan total item.
- **Kolom Kanan (35% Lebar Layar - Panel Pembayaran):**
  - Tampilan Grand Total berukuran besar dengan kontras tinggi.
  - Pilihan metode bayar (*Toggle Button*: Tunai vs QRIS).
  - Form input Uang Diterima (*Cash Received*) dengan tombol cepat pecahan uang (Rp 10.000, Rp 20.000, Rp 50.000, Rp 100.000, Pas).
  - Tampilan otomatis Uang Kembalian (*Change Amount*).
  - Tombol aksi utama: `Bayar & Cetak Nota [Enter]` (Hijau besar).

### 4.2 Layar Manajemen (Produk, Stok, Riwayat, Laporan)
- **Top Bar:** Breadcrumb navigasi, kolom pencarian cepat, tombol aksi utama (+ Tambah Produk / Restock), dan filter dropdown kategori.
- **Main Content:** Tabel data responsif dengan pagination dan indikator status badge visual.
- **Bottom Bar:** Informasi total entitas data dan status server.

---

## 5. Standar Notifikasi & Dialog

- **Konfirmasi Aksi Destruktif:** Tindakan penghapusan produk atau pembatalan transaksi wajib menampilkan dialog konfirmasi yang jelas (*"Apakah Anda yakin ingin membatalkan transaksi ini?"*).
- **Toast / Snackbars Ringkas:** Notifikasi berhasil simpan muncul di pojok kanan bawah selama 3 detik dan hilang otomatis tanpa menghalangi alur kasir.
- **Struk Penjualan:** Dialog struk menampilkan pratinjau teks struk monospaced yang siap dicetak ke printer thermal 58mm atau 80mm.

