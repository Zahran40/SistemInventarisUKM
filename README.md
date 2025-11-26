# Sistem Inventaris UKM

Sistem manajemen inventaris untuk Unit Kegiatan Mahasiswa (UKM) dengan fitur login role-based (Admin & Peminjam).

## 🚀 Fitur

### Authentication
- ✅ Login dengan Email atau NIM
- ✅ Role-based access (Admin & Peminjam)
- ✅ Session management
- ✅ Logout dari semua halaman

### Admin
- Dashboard Admin
- Tambah Barang
- Edit Barang (dengan popup dialog)
- Hapus Barang
- Log Peminjaman
- Request Peminjaman
- Request Pengembalian

### Peminjam
- Dashboard Peminjam (Catalog)
- Riwayat Peminjaman
- Profil Peminjam
- Detail Barang
- Halaman Pengembalian

## 📋 Prasyarat

1. **JDK 17** atau lebih tinggi
2. **MySQL 8.0** atau lebih tinggi
3. **NetBeans IDE** (opsional, untuk GUI editing)

## ⚙️ Setup untuk Teman yang Clone Repository

### 1. Clone Repository
```bash
git clone https://github.com/Zahran40/SistemInventarisUKM.git
cd SistemInventarisUKM
```

### 2. Setup Database MySQL

#### a. Buat Database
```sql
CREATE DATABASE sistem_inventaris_ukm;
```

#### b. Import Data (pilih salah satu)

**Opsi 1: Database dengan data dummy lengkap**
```bash
mysql -u root -p sistem_inventaris_ukm < "sistem_inventaris_ukm (2).sql"
```

**Opsi 2: Hanya struktur + data minimal**
```bash
mysql -u root -p sistem_inventaris_ukm < data_dummy.sql
```

#### c. Update Konfigurasi Database

Edit file `src/main/java/Utils/DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/sistem_inventaris_ukm";
private static final String USER = "root";  // Ganti dengan username MySQL Anda
private static final String PASSWORD = "";  // Ganti dengan password MySQL Anda
```

### 3. Verifikasi Dependencies

File JAR MySQL Connector sudah disertakan di folder `lib/`:
- ✅ `lib/mysql-connector-j-8.0.33.jar`

Jika hilang, download dari: https://dev.mysql.com/downloads/connector/j/

### 4. Compile Project

**Windows (PowerShell/CMD):**
```bash
# Compile semua file
.\build.bat
```

Atau manual:
```bash
javac -encoding UTF-8 -d target\classes -cp "lib\*" -sourcepath src\main\java src\main\java\Register\*.java src\main\java\Admin\*.java src\main\java\Peminjam\*.java src\main\java\Utils\*.java src\main\java\com\mycompany\sisteminventarisukm\*.java
```

### 5. Run Aplikasi

**Windows (PowerShell/CMD):**
```bash
# Run aplikasi
.\run.bat
```

Atau manual:
```bash
java -cp "target\classes;lib\*" Register.LoginPage
```

## 👤 Login Credentials

### Admin
- **Email/NIM:** ADM001
- **Password:** password123

### Peminjam
- **Email/NIM:** 2101001 atau peminjam1@email.com
- **Password:** password123

## 🗂️ Struktur Database

### Tabel Users
- `user_id` (Primary Key)
- `nim` (Unique)
- `nama`
- `email` (Unique)
- `password`
- `role` (admin/peminjam)
- `no_hp`

### Tabel Barang
- `barang_id` (Primary Key)
- `nama_barang`
- `kategori`
- `stok`
- `status` (tersedia/dipinjam)

### Tabel Peminjaman
- `peminjaman_id` (Primary Key)
- `user_id` (Foreign Key)
- `barang_id` (Foreign Key)
- `tanggal_pinjam`
- `tanggal_kembali`
- `status` (pending/approved/returned)

## 🛠️ Troubleshooting

### Database Connection Error
**Error:** `Communications link failure`

**Solusi:**
1. Pastikan MySQL service running
2. Cek username/password di `DatabaseConnection.java`
3. Pastikan database sudah dibuat

### ClassNotFoundException: com.mysql.cj.jdbc.Driver
**Solusi:**
- Pastikan file `lib/mysql-connector-j-8.0.33.jar` ada
- Cek classpath saat compile dan run sudah include `lib\*`

### NullPointerException saat Login
**Solusi:**
- Pastikan data dummy sudah di-import ke database
- Cek koneksi database berhasil

### Aplikasi Tidak Fullscreen
**Solusi:**
- Sudah otomatis fullscreen dengan `setExtendedState(MAXIMIZED_BOTH)`
- Jika masih tidak fullscreen, coba restart aplikasi

## 📝 Catatan Pengembangan

- **Maven:** Project menggunakan struktur Maven tapi compile manual (Maven CLI tidak wajib)
- **NetBeans Forms:** File `.form` untuk GUI designer (opsional)
- **Encoding:** Semua file Java menggunakan UTF-8
- **Session:** Menggunakan Singleton pattern untuk UserSession

## 🔄 Update dari GitHub

Jika ada update dari repository:
```bash
git pull origin main
# atau
git pull origin abbil
```

Lalu compile ulang dengan `.\build.bat`

## 👥 Kontributor

- **Developer:** Tim Sistem Inventaris UKM
- **Repository:** https://github.com/Zahran40/SistemInventarisUKM

## 📄 Lisensi

Project ini dibuat untuk keperluan pembelajaran Praktikum PBOL.
