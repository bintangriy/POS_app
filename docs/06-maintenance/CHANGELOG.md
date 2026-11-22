# Changelog & Version History - POS_app

Seluruh perubahan penting pada spesifikasi, arsitektur, dan kode aplikasi **POS_app** dicatat dalam dokumen ini mengikuti prinsip [Keep a Changelog](https://keepachangelog.com/id/1.0.0/).

---

## [Unreleased] - Persiapan Implementasi & Review Arsitektur

### Ditambahkan (Added)
- **Dokumentasi Produk (`docs/01-product/`):**
  - `PRD.md`: Spesifikasi kebutuhan produk POS dan inventaris untuk UMKM ritel.
  - `USER_STORIES.md`: Daftar skenario pengguna (Kasir dan Admin) lengkap dengan kriteria penerimaan.
  - `SCOPE.md`: Penegasan ruang lingkup in-scope (fokus tunggal Java I/O pada konfigurasi file eksternal) dan out-of-scope.
- **Dokumentasi Desain & OOP (`docs/02-design/`):**
  - `OOP_DESIGN.md`: **(Disetujui)** Arsitektur 4 pilar OOP, UML Class Diagram, Sequence Diagram, dan eliminasi anti-pattern pewarisan.
  - `ARCHITECTURE.md`: Arsitektur berlapis (*Layered Architecture*), integrasi Java I/O `AppConfig`, dan kontrol transaksi atomik JDBC.
  - `UI_UX_GUIDELINES.md`: Kebijakan *Anti-AI-Slop*, palet warna Slate/Blue, dan tata letak keyboard-friendly.
  - `UI_SCREEN_SPECIFICATION.md`: Spesifikasi detail untuk 11 tampilan JavaFX FXML.
- **Dokumentasi Basis Data (`docs/03-database/`):**
  - `DATABASE.md`: Kamus data untuk tabel `users`, `categories`, `products`, `transactions`, `transaction_details`, `stock_movements`. Penggunaan tipe `DECIMAL(12, 2)` untuk moneter.
  - `ERD.md`: Diagram relasi entitas Mermaid dan aturan integritas referensial foreign key.
  - `BUSINESS_RULES.md`: Aturan pencegahan stok negatif, snapshot harga produk, dan restorasi stok saat pembatalan transaksi oleh Admin.
- **Dokumentasi Pengembangan (`docs/04-development/`):**
  - `PROJECT_STRUCTURE.md`: Panduan struktur direktori Maven dan pemisahan paket Java.
  - `IMPLEMENTATION_PLAN.md`: Roadmap 8 fase pengembangan bertahap.
  - `CODING_STANDARDS.md`: Standar penamaan, clean code, enkapsulasi, dan pencegahan SQL Injection.
  - `DEPENDENCY_GUIDE.md`: Rincian dependensi Java 21, JavaFX 21, MySQL Connector/J, jBCrypt, dan JUnit 5.
- **Dokumentasi Kualitas & Pengujian (`docs/05-quality/`):**
  - `TESTING.md`: Strategi piramida pengujian otomatis dengan JUnit 5.
  - `TEST_CASES.md`: Spesifikasi skenario uji untuk kalkulasi, transaksi atomik, dan Java I/O.
  - `ACCEPTANCE_CRITERIA.md`: Kriteria penerimaan formal sebelum rilis.
- **Dokumentasi Pemeliharaan (`docs/06-maintenance/`):**
  - `SETUP.md`: Panduan penyiapan lingkungan pengembang baru (MySQL, Java 21, Maven).
  - `TROUBLESHOOTING.md`: Solusi kendala koneksi MySQL, konfigurasi Java I/O hilang, dan JavaFX runtime.
  - `CHANGELOG.md`: Riwayat pencatatan milestone dan versi.
- **Script Database & Konfigurasi:**
  - `database/schema.sql`: DDL skema MySQL dengan constraints `CHECK (stock >= 0)` dan `CHECK (sell_price >= buy_price)`.
  - `database/seed.sql`: Data awal pengguna (Admin & Kasir dengan hash BCrypt), kategori, produk ritel, dan saldo stok awal.
  - `config/app.properties.example`: Template konfigurasi koneksi MySQL untuk Java I/O.
- **Root Instructions:**
  - `AGENTS.md`: Aturan operasional permanen bagi AI Coding Agent dan penegakan 3 Quality Gates.
  - `README.md`: Panduan umum repositori dan navigasi dokumen.
  - `pom.xml`: Konfigurasi build Maven untuk Java 21 LTS dan JavaFX 21.

---

## [0.1.0-alpha] - 2026-11-22
- Rilis inisial fondasi arsitektur perangkat lunak dan spesifikasi teknis lengkap.

