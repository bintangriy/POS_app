# Developer Environment Setup Guide - POS_app

Dokumen ini memandu langkah-langkah penyiapan lingkungan kerja (*developer environment*) untuk pengembang baru yang akan mengompilasi, menjalankan, atau mengembangkan aplikasi **POS_app**.

---

## 1. Prasyarat Perangkat Lunak (Prerequisites)

1. **Java Development Kit (JDK):**
   - Wajib menggunakan **OpenJDK 21 (LTS)** atau Oracle JDK 21.
   - Verifikasi instalasi:
     ```bash
     java -version
     javac -version
     ```
2. **Apache Maven:**
   - Versi 3.8.0 atau lebih tinggi.
   - Verifikasi instalasi:
     ```bash
     mvn -version
     ```
3. **MySQL Server:**
   - MySQL versi 8.0 atau 8.4.
   - Pastikan service MySQL sedang berjalan di port `3306`.
4. **Git:**
   - Git CLI terpasang dan terkonfigurasi.

---

## 2. Langkah-Langkah Penyiapan Proyek (Step-by-Step Setup)

### Langkah 1: Clone Repository
```bash
git clone <url-repository-github>
cd POS_app
```

### Langkah 2: Setup Database MySQL
Buka MySQL CLI atau tool GUI (DBeaver / MySQL Workbench) dan jalankan script SQL secara berurutan:
```bash
# 1. Buat skema tabel dan relasi
mysql -u root -p < database/schema.sql

# 2. Masukkan data awal (kategori, produk awal, akun admin & kasir)
mysql -u root -p < database/seed.sql
```

### Langkah 3: Setup Konfigurasi Aplikasi (Java I/O)
Aplikasi membaca kredensial database dari file `config/app.properties`. Buat file tersebut dari template yang tersedia:
```bash
cp config/app.properties.example config/app.properties
```
Buka file `config/app.properties` menggunakan text editor Anda dan sesuaikan username serta password MySQL lokal:
```properties
db.host=localhost
db.port=3306
db.name=pos_app_db
db.user=root
db.password=password_mysql_anda
```

> [!CAUTION]
> Jangan pernah meng-commit file `config/app.properties` ke Git remote! File tersebut berisi password database lokal Anda.

### Langkah 4: Kompilasi Proyek dengan Maven
```bash
mvn clean compile
```

### Langkah 5: Menjalankan Pengujian Otomatis
```bash
mvn test
```
Pastikan seluruh test JUnit menghasilkan status hijau (*BUILD SUCCESS*).

### Langkah 6: Menjalankan Aplikasi Desktop JavaFX
```bash
mvn javafx:run
```

---

## 3. Akun Bawaan untuk Pengujian

Gunakan akun berikut pada layar login aplikasi:
- **Akun Admin:**
  - Username: `admin`
  - Password: `admin123`
- **Akun Kasir:**
  - Username: `kasir1`
  - Password: `kasir123`

