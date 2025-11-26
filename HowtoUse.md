# Sistem Inventaris UKM

Aplikasi desktop untuk mengelola inventaris barang UKM Olahraga berbasis Java Swing dengan database MySQL.

## Fitur

### Admin
- Dashboard dengan statistik barang
- Tambah, Edit, dan Hapus barang
- Melihat log peminjaman
- Mengelola request peminjaman
- Mengelola request pengembalian
- Logout

### Peminjam
- Dashboard catalog barang
- Melihat detail barang
- Melihat riwayat peminjaman
- Melihat profil
- Logout

## Setup Database

### 1. Install MySQL
Pastikan MySQL sudah terinstall dan berjalan di komputer Anda.

### 2. Import Database
1. Buka phpMyAdmin atau MySQL Workbench
2. Buat database baru bernama `sistem_inventaris_ukm`
3. Import file `sistem_inventaris_ukm (2).sql` untuk membuat struktur tabel
4. Import file `data_dummy.sql` untuk menambahkan data dummy

Atau via command line:
```bash
mysql -u root -p
CREATE DATABASE sistem_inventaris_ukm;
USE sistem_inventaris_ukm;
SOURCE sistem_inventaris_ukm (2).sql;
SOURCE data_dummy.sql;
```

### 3. Konfigurasi Koneksi Database
Edit file `src/main/java/Database/DatabaseConnection.java` sesuai dengan konfigurasi MySQL Anda:

```java
private static final String URL = "jdbc:mysql://localhost:3306/sistem_inventaris_ukm";
private static final String USERNAME = "root"; // Ganti dengan username MySQL Anda
private static final String PASSWORD = ""; // Ganti dengan password MySQL Anda
```

## Setup Project

### Prerequisites
- JDK 24 atau lebih tinggi
- Maven
- MySQL 8.0 atau lebih tinggi
- NetBeans IDE (recommended untuk edit GUI)

### Install Dependencies
Jalankan perintah berikut untuk menginstall dependencies termasuk MySQL JDBC Driver:

```bash
mvn clean install
```

### Build Project
```bash
mvn clean package
```

## Menjalankan Aplikasi

### Via NetBeans
1. Buka project di NetBeans
2. Right-click pada project → Properties → Run
3. Pastikan Main Class adalah `Register.LoginPage`
4. Klik Run Project (F6)

### Via Maven
```bash
mvn clean compile exec:java -Dexec.mainClass="Register.LoginPage"
```

### Via JAR
```bash
cd target
java -cp sisteminventarisukm-1.0-SNAPSHOT.jar Register.LoginPage
```

## Login Credentials

### Admin
- Username: `ADM001` atau `admin.olahraga@ukm.ac.id`
- Password: `password123`

### Peminjam
- Username: `2101001` atau `budi.prasetyo@student.ac.id`
- Password: `password123`

## Struktur Project

```
src/main/java/
├── Admin/                  # Halaman admin
│   ├── DashboardAdmin.java
│   ├── tambahbarang.java
│   ├── editbarang.java
│   ├── hapusbarang.java
│   ├── LogPeminjaman.java
│   ├── RequestPeminjaman.java
│   └── RequestPengembalian.java
├── Peminjam/              # Halaman peminjam
│   ├── DashboardPeminjam.java
│   ├── DetailBarang.java
│   ├── RiwayatPeminjam.java
│   ├── ProfilPeminjam.java
│   ├── DetailRiwayat.java
│   └── HalamanPengembalian.java
├── Register/              # Halaman login
│   └── LoginPage.java
├── Database/              # Koneksi database
│   └── DatabaseConnection.java
└── Utils/                 # Utility classes
    ├── UserSession.java
    └── SessionHelper.java
```

## Fitur Keamanan

- **Session Management**: Setiap halaman mengecek apakah user sudah login
- **Role-based Access**: Admin dan Peminjam memiliki akses berbeda
- **Auto Redirect**: Jika belum login atau role tidak sesuai, otomatis redirect ke halaman login
- **Logout**: Membersihkan session dan kembali ke halaman login

## Data Dummy

Data dummy yang sudah tersedia:
- **7 kategori** perlengkapan olahraga
- **20 barang** inventaris (bola, raket, matras, dll)
- **1 admin**: Ketua UKM Olahraga
- **1 peminjam**: Budi Prasetyo

Tabel `peminjaman`, `pengembalian`, `riwayat`, dan `log_aktivitas` masih kosong untuk testing.

## Development

### Edit GUI dengan NetBeans
1. Buka file `.form` di NetBeans
2. Gunakan Design view untuk edit tampilan
3. NetBeans akan generate code di file `.java` secara otomatis

### Menambahkan Halaman Baru
1. Buat JFrame baru di NetBeans
2. Tambahkan session check di constructor:
   ```java
   public NamaHalaman() {
       if (!SessionHelper.checkAdmin(this)) return; // untuk admin
       // atau
       if (!SessionHelper.checkPeminjam(this)) return; // untuk peminjam
       initComponents();
       setLocationRelativeTo(null);
   }
   ```
3. Tambahkan import yang diperlukan

## Troubleshooting

### Database Connection Failed
- Pastikan MySQL berjalan
- Check username dan password di `DatabaseConnection.java`
- Pastikan database `sistem_inventaris_ukm` sudah dibuat

### JDBC Driver Not Found
- Jalankan `mvn clean install` untuk download dependencies
- Check file `pom.xml` sudah ada dependency `mysql-connector-j`

### Halaman Tidak Muncul
- Pastikan sudah login terlebih dahulu
- Check console untuk error message
- Pastikan role user sesuai dengan halaman yang diakses

## TODO (Future Development)
- [ ] Implementasi CRUD barang dengan database
- [ ] Implementasi peminjaman barang
- [ ] Implementasi pengembalian barang
- [ ] Validasi form input
- [ ] Report/export data
- [ ] Notifikasi jatuh tempo
- [ ] History log aktivitas

## Catatan Penting

⚠️ **Aplikasi ini dibuat menggunakan NetBeans Form Editor**. Jika Anda mengedit file `.java` secara manual untuk bagian GUI (area antara `//GEN-BEGIN` dan `//GEN-END`), perubahan akan hilang saat membuka form di Design view NetBeans.

✅ **Yang bisa diedit manual**: 
- Method event handler (seperti `addbarangActionPerformed`)
- Constructor (di luar `initComponents()`)
- Method custom yang Anda buat sendiri

❌ **Yang TIDAK boleh diedit manual**:
- Code di dalam `initComponents()`
- Variable declaration yang di-generate NetBeans
