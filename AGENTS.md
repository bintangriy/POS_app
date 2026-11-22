# Antigravity AI Agent Team Instructions - POS_app

Dokumen ini adalah instruksi operasional permanen bagi seluruh AI Coding Agent yang bekerja pada repositori **POS_app**. Seluruh agen wajib mematuhi aturan berikut.

---

## 1. Aturan Wajib Teknologi & Stack

1. **Wajib Menggunakan Java:** Java versi 21 LTS adalah bahasa utama untuk seluruh logika bisnis, transaksi kasir, inventaris, perhitungan pembayaran, dan validasi data.
2. **Wajib Menerapkan Konsep OOP:** Menerapkan 4 pilar utama OOP (Encapsulation, Inheritance, Polymorphism, Abstraction) dan memprioritaskan *Composition over Inheritance*.
3. **Wajib Mengikuti `OOP_DESIGN.md` yang Telah Disesuaikan:** Seluruh hierarki kelas domain dan kontrak antarmuka DAO wajib dipatuhi.
4. **Wajib Menggunakan JavaFX:** JavaFX 21 untuk GUI desktop (FXML untuk struktur dan CSS untuk styling).
5. **Wajib Menggunakan Java I/O & CSV:** Penyimpanan data wajib menggunakan berkas CSV lokal via package `java.io` (`File`, `FileReader`, `BufferedReader`, `FileWriter`, `BufferedWriter`, `FileInputStream`, `IOException`).
6. **Dilarang Menggunakan Database Server:** Dilarang menggunakan SQL, MySQL, JDBC, SQLite, H2, PostgreSQL, MongoDB, atau ORM database apapun.
7. **Wajib Menggunakan Maven:** Manajemen dependensi dan build dikelola melalui `pom.xml`.
8. **Wajib Menggunakan JUnit 5:** Pengujian unit wajib dijalankan melalui Maven (`mvn test`).
9. **Git & GitHub:** Dilarang keras melakukan push (`git push`), merge branch utama, atau modifikasi remote repository tanpa izin eksplisit pengguna.

---

## 2. Standar Java I/O & Integritas Data

1. **Parser CSV yang Aman:** Dilarang menggunakan `String.split(",")` secara naif. Gunakan `CsvParser.parseLine()` yang mematuhi RFC 4180.
2. **Try-With-Resources:** Seluruh pembacaan dan penulisan berkas wajib menggunakan blok `try-with-resources`.
3. **Atomic File Write:** Pembaruan berkas penuh harus menggunakan pola file sementara `.tmp` sebelum menggantikan berkas utama.
4. **Mencegah Stok Negatif:** Validasi dilakukan berlapis pada domain model (`Product.reduceStock()`) dan service layer.
5. **Password Hashing:** Password pengguna wajib dienkripsi menggunakan BCrypt (tidak boleh disimpan plaintext).

---

## 3. Tiga Quality Gates Wajib

- **Quality Gate 1:** Perencanaan disetujui pengguna sebelum koding dimulai.
- **Quality Gate 2:** Seluruh unit test lolos (`mvn test`), validasi visual JavaFX bersih tanpa memory leak/unhandled exception.
- **Quality Gate 3:** Tampilkan `git status` dan minta persetujuan eksplisit pengguna sebelum melakukan `git push` atau merge remote.
