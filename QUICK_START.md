# Setup Tanpa Install Maven

## Cara Paling Mudah: Menggunakan NetBeans

Karena Maven belum terinstall di sistem Anda, gunakan NetBeans yang sudah include Maven built-in:

### Langkah-langkah:

#### 1. Import Database Dulu
Sebelum running aplikasi, pastikan database sudah ready:

**Via phpMyAdmin:**
1. Buka http://localhost/phpmyadmin
2. Klik "New" untuk buat database baru
3. Nama database: `sistem_inventaris_ukm`
4. Klik tab "Import"
5. Pilih file `sistem_inventaris_ukm (2).sql` → Import
6. Pilih file `data_dummy.sql` → Import
7. Done!

**Via MySQL Command Line:**
```bash
mysql -u root -p
# Masukkan password MySQL Anda

CREATE DATABASE sistem_inventaris_ukm;
exit;
```

Lalu import file SQL:
```bash
cd C:\JavaProjects\PraktikumPBOL\sisteminventarisukm
mysql -u root -p sistem_inventaris_ukm < "sistem_inventaris_ukm (2).sql"
mysql -u root -p sistem_inventaris_ukm < "data_dummy.sql"
```

#### 2. Sesuaikan Konfigurasi Database

Buka file: `src\main\java\Database\DatabaseConnection.java`

Edit baris berikut sesuai MySQL Anda:
```java
private static final String USERNAME = "root";     // Username MySQL Anda
private static final String PASSWORD = "";         // Password MySQL Anda (default kosong)
```

#### 3. Buka Project di NetBeans

1. Buka NetBeans
2. **File** → **Open Project**
3. Browse ke: `C:\JavaProjects\PraktikumPBOL\sisteminventarisukm`
4. Klik **Open Project**

#### 4. Install Dependencies (Otomatis)

NetBeans akan otomatis detect bahwa ini Maven project dan akan:
- Download MySQL JDBC Driver
- Setup semua dependencies

Tunggu sampai loading selesai (lihat progress bar di kanan bawah NetBeans).

#### 5. Build Project

Di NetBeans:
- **Right-click** pada nama project
- Pilih **Clean and Build**
- Tunggu sampai selesai (lihat Output window)

#### 6. Set Main Class (Penting!)

- **Right-click** pada nama project
- Pilih **Properties**
- Klik **Run** di menu kiri
- Di "Main Class", pastikan isinya: `Register.LoginPage`
- Klik **OK**

#### 7. Run Aplikasi

- Tekan **F6** atau
- **Right-click** project → **Run**

Halaman login akan muncul!

## Login Credentials

**Admin:**
- Username: `ADM001` atau `admin.olahraga@ukm.ac.id`
- Password: `password123`

**Peminjam:**
- Username: `2101001` atau `budi.prasetyo@student.ac.id`
- Password: `password123`

## Troubleshooting

### "Cannot connect to database"

**Solusi:**
1. Pastikan MySQL Server sedang running
2. Check service MySQL di Windows Services
3. Test koneksi manual:
   ```bash
   mysql -u root -p
   ```
4. Pastikan database `sistem_inventaris_ukm` sudah ada:
   ```sql
   SHOW DATABASES;
   ```

### "Build Failed" atau "Dependencies could not be resolved"

**Solusi:**
1. Check koneksi internet (NetBeans perlu download dependencies)
2. Di NetBeans: **Tools** → **Options** → **Java** → **Maven**
3. Pastikan ada checkmark di "Download Sources" dan "Download Javadoc"
4. Coba lagi: Right-click project → **Clean and Build**

### "Main class not found"

**Solusi:**
1. Right-click project → **Properties** → **Run**
2. Main Class: `Register.LoginPage`
3. Klik **OK**
4. Clean and Build lagi

### Halaman Login Tidak Muncul

**Solusi:**
1. Check Output window di NetBeans untuk error
2. Pastikan tidak ada error merah di build
3. Pastikan semua file `.form` ada (di samping file `.java`)

## Verifikasi Setup

Setelah database ready dan project dibuka di NetBeans:

1. ✅ Check di NetBeans **Projects** panel:
   - Ada folder `Dependencies`
   - Di dalamnya ada `mysql-connector-j-8.0.33.jar`

2. ✅ Check database:
   ```sql
   USE sistem_inventaris_ukm;
   SELECT COUNT(*) FROM users;        -- Harus ada 2 users
   SELECT COUNT(*) FROM barang;       -- Harus ada 20 barang
   SELECT COUNT(*) FROM kategori_barang; -- Harus ada 7 kategori
   ```

3. ✅ Test login:
   - Run aplikasi (F6)
   - Login dengan username `ADM001` password `password123`
   - Harus masuk ke Dashboard Admin

## Jika Ingin Install Maven Terpisah (Optional)

Kalau tetap mau install Maven untuk command line:

1. Download Maven dari: https://maven.apache.org/download.cgi
2. Extract ke folder (misal: `C:\Program Files\Apache\maven`)
3. Tambah ke PATH:
   - Windows Search → "Environment Variables"
   - System Properties → Environment Variables
   - Edit "Path" di System variables
   - Tambah: `C:\Program Files\Apache\maven\bin`
   - Klik OK

4. Verify:
   ```bash
   mvn --version
   ```

Tapi untuk project ini, **TIDAK PERLU** install Maven terpisah karena NetBeans sudah punya Maven built-in!

## Quick Start Summary

```
1. Import database (2 file SQL)
   ↓
2. Edit DatabaseConnection.java (username/password)
   ↓
3. Buka project di NetBeans
   ↓
4. Wait for dependencies download
   ↓
5. Clean and Build (Right-click project)
   ↓
6. Run (F6)
   ↓
7. Login!
```

Selesai! 🎉
