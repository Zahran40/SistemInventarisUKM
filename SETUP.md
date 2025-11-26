# Petunjuk Setup dan Running

## Langkah 1: Setup Database

1. **Jalankan MySQL**
   - Pastikan MySQL Server sudah berjalan di komputer Anda
   - Default port: 3306

2. **Import Database**
   ```sql
   -- Via MySQL Command Line atau phpMyAdmin
   CREATE DATABASE sistem_inventaris_ukm;
   USE sistem_inventaris_ukm;
   
   -- Import struktur tabel
   SOURCE sistem_inventaris_ukm (2).sql;
   
   -- Import data dummy  
   SOURCE data_dummy.sql;
   ```

3. **Verifikasi Data**
   ```sql
   USE sistem_inventaris_ukm;
   SELECT * FROM users;
   SELECT * FROM barang;
   SELECT * FROM kategori_barang;
   ```

## Langkah 2: Konfigurasi Aplikasi

1. **Edit Database Connection**
   - Buka: `src/main/java/Database/DatabaseConnection.java`
   - Ubah sesuai konfigurasi MySQL Anda:
   
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/sistem_inventaris_ukm";
   private static final String USERNAME = "root";  // Username MySQL Anda
   private static final String PASSWORD = "";      // Password MySQL Anda (kosongkan jika default)
   ```

## Langkah 3: Build dan Run

### Menggunakan NetBeans (Recommended)

1. **Buka Project**
   - File → Open Project
   - Pilih folder `sisteminventarisukm`

2. **Install Dependencies**
   - Right-click project → Build with Dependencies
   - Maven akan otomatis download MySQL Connector

3. **Run Aplikasi**
   - Right-click project → Run
   - Atau tekan F6
   - Halaman login akan muncul

### Menggunakan Command Line

1. **Install Dependencies**
   ```bash
   mvn clean install
   ```

2. **Compile**
   ```bash
   mvn compile
   ```

3. **Run**
   ```bash
   mvn exec:java -Dexec.mainClass="Register.LoginPage"
   ```

## Langkah 4: Login

### Login sebagai Admin
- **Username**: `ADM001` (atau `admin.olahraga@ukm.ac.id`)
- **Password**: `password123`
- Akan diarahkan ke Dashboard Admin

### Login sebagai Peminjam
- **Username**: `2101001` (atau `budi.prasetyo@student.ac.id`)
- **Password**: `password123`
- Akan diarahkan ke Dashboard Peminjam

## Troubleshooting

### Error: "Database connection failed"
**Penyebab**: MySQL tidak berjalan atau konfigurasi salah

**Solusi**:
1. Pastikan MySQL Server sudah running
2. Check username dan password di `DatabaseConnection.java`
3. Pastikan database `sistem_inventaris_ukm` sudah ada
4. Test koneksi manual:
   ```bash
   mysql -u root -p
   USE sistem_inventaris_ukm;
   ```

### Error: "JDBC Driver not found"
**Penyebab**: MySQL Connector belum terinstall

**Solusi**:
1. Jalankan `mvn clean install`
2. Check internet connection (Maven butuh download dependency)
3. Pastikan `pom.xml` ada dependency `mysql-connector-j`

### Error: "Main class not found"
**Penyebab**: Class path tidak benar

**Solusi**:
1. Build ulang: `mvn clean compile`
2. Di NetBeans: Right-click project → Clean and Build
3. Pastikan Main Class di project properties adalah `Register.LoginPage`

### Halaman Blank/Tidak Muncul
**Penyebab**: NetBeans Form tidak ter-load atau error di initComponents()

**Solusi**:
1. Clean and Build project
2. Check console untuk error message
3. Pastikan semua `.form` file ada di folder yang sama dengan `.java`

### Error: "Access Denied" saat login
**Penyebab**: Data dummy belum diimport atau salah username/password

**Solusi**:
1. Pastikan file `data_dummy.sql` sudah diimport
2. Check apakah data user ada:
   ```sql
   SELECT * FROM users;
   ```
3. Gunakan credentials yang benar (lihat di atas)

## Tips

### Untuk Development di NetBeans

1. **Edit GUI**: Klik kanan file `.java` → Open → Form Editor
2. **Lihat Code**: Klik tab "Source" di editor
3. **Test Run**: Tekan F6 untuk quick run
4. **Debug**: Tekan Ctrl+F5 untuk debug mode

### Untuk Testing

1. **Test Login**: Login dengan kedua role (admin & peminjam)
2. **Test Navigasi**: Klik semua tombol untuk memastikan navigasi bekerja
3. **Test Session**: Coba akses halaman tanpa login (harus redirect ke login)
4. **Test Logout**: Klik logout dan coba akses lagi

### Untuk Edit Database

**Via phpMyAdmin**:
- Akses http://localhost/phpmyadmin
- Pilih database `sistem_inventaris_ukm`
- Edit data sesuai kebutuhan

**Via MySQL Workbench**:
- Connect ke localhost
- Browse tables dan edit

**Via Command Line**:
```bash
mysql -u root -p
USE sistem_inventaris_ukm;
SELECT * FROM users;
-- Edit queries here
```

## Next Steps

Setelah aplikasi berhasil running:

1. ✅ Test semua fitur login dan navigasi
2. ⏭️ Implementasi CRUD barang dengan database
3. ⏭️ Implementasi fitur peminjaman
4. ⏭️ Implementasi fitur pengembalian
5. ⏭️ Tambahkan validasi form
6. ⏭️ Tambahkan fitur search dan filter

## Kontak

Jika ada pertanyaan atau issue, silakan:
- Check README.md untuk dokumentasi lengkap
- Review code di package `Utils` untuk helper functions
- Check console output untuk error details
