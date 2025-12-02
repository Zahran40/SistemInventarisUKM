# 📚 PANDUAN TESTING SISTEM INVENTARIS UKM

## 📋 Daftar Isi
1. [Setup Database](#-1-setup-database)
2. [Konfigurasi Aplikasi](#-2-konfigurasi-aplikasi)
3. [Compile & Run Aplikasi](#-3-compile--run-aplikasi)
4. [Fitur-Fitur yang Diimplementasikan](#-4-fitur-fitur-yang-diimplementasikan)
5. [Skenario Testing](#-5-skenario-testing)
6. [Login Credentials](#-6-login-credentials)
7. [Troubleshooting](#-7-troubleshooting)

---

## 🗄️ 1. Setup Database

### Langkah 1: Import Database
```sql
1. Buka MySQL Workbench atau phpMyAdmin
2. Jalankan file: sistem_inventaris_ukm (2).sql
   - File ini akan membuat database dan struktur tabel
3. Jalankan file: DATA_DUMMY_LENGKAP.sql
   - File ini akan mengisi data dummy untuk testing
```

### Langkah 2: Verifikasi Database
Pastikan tabel berikut sudah terisi:
- ✅ `users` → 6 users (1 admin + 5 peminjam)
- ✅ `barang` → 21 barang
- ✅ `kategori_barang` → 7 kategori
- ✅ `peminjaman` → 13 peminjaman (berbagai status)
- ✅ `pengembalian` → 6 pengembalian (berbagai status)
- ✅ `denda` → 2 denda (1 lunas, 1 belum bayar freeze)
- ✅ `log_aktivitas` → 19 log aktivitas

---

## ⚙️ 2. Konfigurasi Aplikasi

### File yang Perlu Dicek: `Database/DatabaseConnection.java`

Pastikan konfigurasi database sesuai:

```java
private static final String URL = "jdbc:mysql://localhost:3306/sistem_inventaris_ukm";
private static final String USER = "root";  // Sesuaikan dengan username MySQL Anda
private static final String PASSWORD = "";  // Sesuaikan dengan password MySQL Anda
```

**PENTING:** Jika MySQL Anda menggunakan password, ubah nilai `PASSWORD` sesuai konfigurasi Anda.

---

## 🚀 3. Compile & Run Aplikasi

### Opsi 1: Menggunakan NetBeans
```
1. Buka project di NetBeans
2. Klik kanan pada project → Clean and Build (Shift + F11)
3. Klik Run Project (F6)
```

### Opsi 2: Menggunakan Maven (Command Line)
```bash
# Di direktori project
mvn clean compile
mvn exec:java -Dexec.mainClass="com.mycompany.sisteminventarisukm.Sisteminventarisukm"
```

### Jika Ada Error "DashboardDenda cannot be resolved"
```
Solusi:
1. Buka NetBeans
2. Klik: Run → Clean and Build Project (Shift + F11)
3. Tunggu sampai build selesai
4. Run Project (F6)
```

---

## ✨ 4. Fitur-Fitur yang Diimplementasikan

### 🔐 A. SISTEM LOGIN
- **Login Admin:** username = `admin`, password = `admin123`
- **Login Peminjam:** username = NIM (contoh: `2101001`), password = `password123`

---

### 👤 B. FITUR PEMINJAM

#### 1. Dashboard Peminjam
**Lokasi:** `DashboardPeminjam.java`

**Fitur:**
- Melihat katalog barang yang tersedia
- Filter barang berdasarkan kategori
- Search barang berdasarkan nama
- Melihat detail barang (stok, deskripsi, gambar)
- Tombol navigasi ke: Riwayat, Profil, Denda Saya

**Testing:**
```
Login sebagai: Budi Santoso (NIM: 2101001)
- Lihat katalog barang
- Klik tombol "Detail" pada barang → Masuk ke DetailBarang
- Filter kategori: pilih "Bola" → hanya barang kategori bola yang muncul
```

---

#### 2. Detail Barang & Peminjaman
**Lokasi:** `DetailBarang.java`

**Fitur:**
- Melihat detail lengkap barang (nama, kategori, stok, deskripsi, gambar)
- Mengajukan peminjaman barang
- Upload bukti validasi (foto KTM/identitas)
- Validasi stok tersedia
- Tombol navigasi ke: Dashboard, Riwayat, Profil, Denda Saya

**Testing:**
```
Login sebagai: Dewi Lestari (NIM: 2101004)
1. Pilih barang "Bola Basket"
2. Isi form peminjaman:
   - Jumlah: 1
   - Tanggal Pinjam: 2025-01-20
   - Tanggal Kembali: 2025-01-27
3. Upload bukti validasi (pilih gambar apapun)
4. Klik "Ajukan Peminjaman"
5. Cek di Riwayat Peminjam → Status "proses" (menunggu admin)
```

---

#### 3. Riwayat Peminjaman
**Lokasi:** `RiwayatPeminjam.java`

**Fitur:**
- Melihat semua riwayat peminjaman user
- Status peminjaman: `proses`, `disetujui`, `ditolak`
- Tombol **"Ajukan Pengembalian"** (hanya muncul jika status peminjaman `disetujui`)
- Tombol **"Ajukan Ulang"** (muncul jika pengembalian `ditolak`)
- Informasi tanggal pinjam, tanggal kembali, jumlah, status

**Testing Skenario 1: Ajukan Pengembalian**
```
Login sebagai: Budi Santoso (NIM: 2101001)
1. Masuk ke "Riwayat Peminjam"
2. Lihat peminjaman "Net Voli" (Status: disetujui)
3. Klik tombol "Ajukan Pengembalian"
4. Upload bukti pengembalian (foto barang yang dikembalikan)
5. Klik "Ajukan"
6. Status pengembalian berubah menjadi "proses" (menunggu admin approve)
```

**Testing Skenario 2: Ajukan Ulang (Setelah Ditolak)**
```
Login sebagai: Budi Santoso (NIM: 2101001)
1. Masuk ke "Riwayat Peminjam"
2. Lihat peminjaman "Bola Voli" (Status pengembalian: ditolak)
   - Alasan ditolak: "Bola voli kempes, harus dipompa ulang terlebih dahulu"
3. Perbaiki kondisi barang (dalam kasus ini: pompa bola voli)
4. Klik tombol "Ajukan Ulang"
5. Upload bukti pengembalian baru
6. Klik "Ajukan"
7. Status pengembalian berubah menjadi "proses" (menunggu admin approve lagi)
```

---

#### 4. Dashboard Denda (Real-Time & Freeze)
**Lokasi:** `DashboardDenda.java`

**Fitur:**
- Menampilkan **Total Denda Belum Bayar** (dihitung real-time)
- Daftar semua denda user
- Informasi: Nama Barang, Hari Telat, Jumlah Denda, Status, Tanggal Bayar
- **DENDA REAL-TIME:** Jika barang belum dikembalikan dan terlambat, denda bertambah setiap hari
- **DENDA FREEZE:** Jika pengembalian ditolak, denda berhenti (freeze) di tanggal penolakan

**Testing Skenario 1: Denda Real-Time**
```
Login sebagai: Dewi Lestari (NIM: 2101004)
1. Masuk ke "Denda Saya"
2. Lihat peminjaman "Matras Yoga"
   - Deadline: 2025-01-15
   - Hari ini: 2025-01-16 (contoh)
   - Hari terlambat: 1 hari
   - Denda: Rp 5.000 (1 hari × Rp 5.000)
3. Besok (2025-01-17), denda akan bertambah menjadi Rp 10.000 (2 hari × Rp 5.000)
4. Denda terus bertambah SETIAP HARI sampai barang dikembalikan
```

**Testing Skenario 2: Denda Freeze**
```
Login sebagai: Budi Santoso (NIM: 2101001)
1. Masuk ke "Denda Saya"
2. Lihat denda "Bola Voli"
   - Deadline: 2024-12-22
   - Tanggal Ditolak: 2025-01-05
   - Hari terlambat: 14 hari (dari 2024-12-22 s/d 2025-01-05)
   - Denda: Rp 70.000 (14 hari × Rp 5.000)
   - Status: Belum Bayar
3. Denda TIDAK BERTAMBAH lagi meski hari ini sudah 2025-01-16
4. Denda "freeze" di Rp 70.000 karena pengembalian ditolak pada 2025-01-05
5. User bisa ajukan ulang pengembalian setelah perbaiki kondisi barang
```

**Cara Kerja Denda Freeze:**
```
Rumus Denda Freeze:
- Jika pengembalian ditolak:
  hari_terlambat = DATEDIFF(tanggal_ditolak, deadline_peminjaman)
  jumlah_denda = hari_terlambat × Rp 5.000
  
- Denda disimpan ke tabel `denda` dengan:
  - status = 'belum_bayar'
  - hari_terlambat = nilai fix (tidak berubah)
  - jumlah_denda = nilai fix (tidak berubah)
  
- Jika user ajukan ulang dan disetujui admin:
  - Denda tetap ada (harus dibayar)
  - User bisa bayar denda setelah barang dikembalikan dengan baik
```

---

#### 5. Profil Peminjam
**Lokasi:** `ProfilPeminjam.java`

**Fitur:**
- Melihat informasi pribadi: Nama, NIM, Kontak, Email
- Edit profil (update kontak & email)
- Logout

**Testing:**
```
Login sebagai: Siti Nurhaliza (NIM: 2101002)
1. Masuk ke "Profil"
2. Klik "Edit Profil"
3. Ubah Kontak: 081999888777
4. Ubah Email: siti.baru@student.ac.id
5. Klik "Simpan"
6. Lihat perubahan data di profil
```

---

### 🔧 C. FITUR ADMIN

#### 1. Dashboard Admin
**Lokasi:** `DashboardAdmin.java`

**Fitur:**
- **Statistik Dashboard:**
  - Total Stok Barang
  - Total Jenis Barang
  - Total Barang Dipinjam
  - Total Barang Tersedia
- Navigasi ke: Tambah Barang, Edit Barang, Hapus Barang, Request Peminjaman, Request Pengembalian, Log Peminjaman

**Testing:**
```
Login sebagai: admin (password: admin123)
1. Lihat dashboard admin
2. Cek statistik:
   - Total Stok: 100 (jumlah semua stok barang)
   - Jenis Barang: 21 (jumlah barang berbeda)
   - Dipinjam: 6 (barang yang sedang dipinjam)
   - Tersedia: 94 (stok - dipinjam)
```

---

#### 2. Request Peminjaman (Approve/Reject)
**Lokasi:** `RequestPeminjaman.java`

**Fitur:**
- Melihat semua peminjaman dengan status `proses`
- Informasi: Nama User, Barang, Jumlah, Tanggal Pinjam, Tanggal Kembali
- Tombol **"Setujui"** dan **"Tolak"**
- Stok barang berkurang otomatis saat peminjaman disetujui

**Testing:**
```
Login sebagai: admin
1. Masuk ke "Request Peminjaman"
2. Lihat peminjaman dari Budi Santoso (Bola Basket, 2 buah)
3. Klik tombol "Setujui"
4. Peminjaman status berubah menjadi "disetujui"
5. Stok Bola Basket berkurang 2 (dari 5 → 3)
6. User Budi bisa lihat peminjaman di Riwayat dengan status "disetujui"
```

---

#### 3. Request Pengembalian (Approve/Reject)
**Lokasi:** `RequestPengembalian.java`

**Fitur:**
- Melihat semua pengembalian dengan status `proses`
- Informasi: Nama User, Barang, Jumlah, Tanggal Pengembalian
- Lihat bukti pengembalian (foto yang diupload user)
- Tombol **"Setujui"** dan **"Tolak"**
- Input keterangan admin jika menolak
- **AUTO DENDA:**
  - Jika terlambat + disetujui → Denda otomatis masuk ke tabel `denda`
  - Jika ditolak → Denda FREEZE di tanggal penolakan
- Stok barang bertambah otomatis saat pengembalian disetujui

**Testing Skenario 1: Approve Pengembalian Tepat Waktu**
```
Login sebagai: admin
1. Masuk ke "Request Pengembalian"
2. Lihat pengembalian dari Budi Santoso (Bola Basket)
   - Tanggal Pinjam: 2025-01-15
   - Deadline: 2025-01-20
   - Tanggal Kembali: 2025-01-18 (tepat waktu)
3. Klik tombol "Setujui"
4. Status pengembalian → "disetujui"
5. Stok Bola Basket bertambah 2 (kembali 5)
6. TIDAK ADA DENDA (karena tepat waktu)
```

**Testing Skenario 2: Approve Pengembalian Terlambat (Auto Denda)**
```
Login sebagai: admin
1. Masuk ke "Request Pengembalian"
2. Lihat pengembalian dari Rudi Hermawan (Peluit)
   - Deadline: 2025-01-12
   - Tanggal Kembali: 2025-01-20 (terlambat 8 hari)
3. Klik tombol "Setujui"
4. Status pengembalian → "disetujui"
5. Stok Peluit bertambah 1
6. **DENDA OTOMATIS DIBUAT:**
   - Hari terlambat: 8 hari
   - Jumlah denda: Rp 40.000 (8 × Rp 5.000)
   - Status: belum_bayar
7. User Rudi bisa lihat denda di "Denda Saya"
```

**Testing Skenario 3: Reject Pengembalian (Denda Freeze)**
```
Login sebagai: admin
1. Masuk ke "Request Pengembalian"
2. Lihat pengembalian dari Budi Santoso (Bola Voli)
   - Deadline: 2024-12-22
   - Tanggal Kembali: 2025-01-10 (terlambat 19 hari)
3. Lihat bukti pengembalian (klik tombol "Lihat Bukti")
4. Ternyata bola voli kondisi kempes (tidak layak)
5. Klik tombol "Tolak"
6. Input keterangan: "Bola voli kempes, harus dipompa ulang"
7. Status pengembalian → "ditolak"
8. **DENDA FREEZE DIBUAT:**
   - Tanggal ditolak: 2025-01-10
   - Hari terlambat: 19 hari (dari 2024-12-22 s/d 2025-01-10)
   - Jumlah denda: Rp 95.000 (19 × Rp 5.000)
   - Status: belum_bayar
9. Denda TIDAK BERTAMBAH lagi (freeze di Rp 95.000)
10. User Budi bisa lihat alasan penolakan di Riwayat
11. User Budi bisa klik tombol "Ajukan Ulang" setelah perbaiki bola
```

---

#### 4. Log Peminjaman
**Lokasi:** `LogPeminjaman.java`

**Fitur:**
- Melihat semua riwayat peminjaman di sistem
- Filter berdasarkan status: Semua, Proses, Disetujui, Ditolak
- Informasi: Nama User, Barang, Jumlah, Tanggal Pinjam, Tanggal Kembali, Status
- Export ke Excel/PDF (jika sudah diimplementasikan)

**Testing:**
```
Login sebagai: admin
1. Masuk ke "Log Peminjaman"
2. Lihat semua peminjaman (13 peminjaman)
3. Filter status "disetujui" → tampil 8 peminjaman
4. Filter status "proses" → tampil 3 peminjaman
5. Filter status "ditolak" → tampil 2 peminjaman
```

---

#### 5. Tambah Barang
**Lokasi:** `tambahbarang.java`

**Fitur:**
- Menambah barang baru ke sistem
- Input: Nama Barang, Kategori, Stok, Deskripsi
- Upload gambar barang (optional)
- Validasi input

**Testing:**
```
Login sebagai: admin
1. Masuk ke "Tambah Barang"
2. Isi form:
   - Nama Barang: Matras Senam
   - Kategori: Olahraga Lainnya
   - Stok: 10
   - Deskripsi: Matras untuk senam lantai, ukuran 180x60 cm
3. Upload gambar (pilih gambar apapun)
4. Klik "Simpan"
5. Barang baru muncul di katalog
```

---

#### 6. Edit Barang
**Lokasi:** `editbarang.java`, `formeditbarang.java`

**Fitur:**
- Mengedit informasi barang yang sudah ada
- Update: Nama, Kategori, Stok, Deskripsi, Gambar
- Validasi input

**Testing:**
```
Login sebagai: admin
1. Masuk ke "Edit Barang"
2. Pilih barang "Bola Basket"
3. Klik "Edit"
4. Ubah stok: 5 → 10
5. Ubah deskripsi: "Bola basket standar FIBA untuk latihan dan pertandingan"
6. Klik "Simpan"
7. Lihat perubahan di katalog
```

---

#### 7. Hapus Barang
**Lokasi:** `hapusbarang.java`

**Fitur:**
- Menghapus barang dari sistem
- Konfirmasi sebelum hapus
- Validasi: barang tidak bisa dihapus jika sedang dipinjam

**Testing:**
```
Login sebagai: admin
1. Masuk ke "Hapus Barang"
2. Pilih barang "Matras Senam" (yang baru ditambahkan)
3. Klik "Hapus"
4. Konfirmasi: "Yakin ingin menghapus barang ini?"
5. Klik "Ya"
6. Barang hilang dari katalog

Testing Validasi:
1. Coba hapus barang "Bola Basket" (sedang dipinjam)
2. Muncul error: "Barang tidak bisa dihapus karena sedang dipinjam"
```

---

## 🧪 5. Skenario Testing

### Skenario 1: Full Workflow Peminjaman (Happy Path)
```
1. Login sebagai Peminjam (Siti Nurhaliza - NIM: 2101002)
2. Pilih barang "Raket Badminton"
3. Klik "Detail" → Ajukan Peminjaman
   - Jumlah: 1
   - Tanggal Pinjam: 2025-01-20
   - Tanggal Kembali: 2025-01-27
4. Upload bukti validasi
5. Klik "Ajukan Peminjaman"
6. Logout

7. Login sebagai Admin (admin)
8. Masuk "Request Peminjaman"
9. Lihat peminjaman Siti (Raket Badminton)
10. Klik "Setujui"
11. Logout

12. Login sebagai Siti (2101002)
13. Masuk "Riwayat Peminjam"
14. Lihat status peminjaman → "disetujui"
15. (Simulasi waktu: tanggal 2025-01-27)
16. Klik "Ajukan Pengembalian"
17. Upload bukti pengembalian
18. Klik "Ajukan"
19. Logout

20. Login sebagai Admin
21. Masuk "Request Pengembalian"
22. Lihat pengembalian Siti
23. Klik "Setujui"
24. TIDAK ADA DENDA (karena tepat waktu)
25. Stok Raket Badminton bertambah 1

✅ SELESAI - Peminjaman berhasil tanpa denda
```

---

### Skenario 2: Peminjaman Terlambat (Ada Denda)
```
1. Login sebagai Peminjam (Andi Wijaya - NIM: 2101003)
2. Pilih barang "Bola Sepak"
3. Ajukan Peminjaman:
   - Jumlah: 1
   - Tanggal Pinjam: 2025-01-10
   - Tanggal Kembali: 2025-01-17
4. Upload bukti → Ajukan

5. Login sebagai Admin
6. Setujui peminjaman Andi
7. Logout

8. (Simulasi waktu: tanggal 2025-01-25 - terlambat 8 hari)
9. Login sebagai Andi
10. Ajukan Pengembalian
11. Upload bukti → Ajukan
12. Logout

13. Login sebagai Admin
14. Masuk "Request Pengembalian"
15. Lihat pengembalian Andi (terlambat 8 hari)
16. Klik "Setujui"
17. **DENDA OTOMATIS DIBUAT:**
    - Hari terlambat: 8 hari
    - Jumlah denda: Rp 40.000
    - Status: belum_bayar
18. Logout

19. Login sebagai Andi
20. Masuk "Denda Saya"
21. Lihat denda Rp 40.000 (status: belum bayar)

✅ SELESAI - Denda berhasil dibuat otomatis
```

---

### Skenario 3: Pengembalian Ditolak → Denda Freeze → Ajukan Ulang
```
1. Login sebagai Peminjam (Dewi Lestari - NIM: 2101004)
2. Pilih barang "Cone Latihan"
3. Ajukan Peminjaman:
   - Jumlah: 2
   - Tanggal Pinjam: 2025-01-10
   - Tanggal Kembali: 2025-01-17
4. Upload bukti → Ajukan

5. Login sebagai Admin
6. Setujui peminjaman Dewi
7. Logout

8. (Simulasi waktu: tanggal 2025-01-25 - terlambat 8 hari)
9. Login sebagai Dewi
10. Ajukan Pengembalian (cone dalam kondisi rusak)
11. Upload bukti → Ajukan
12. Logout

13. Login sebagai Admin
14. Masuk "Request Pengembalian"
15. Lihat bukti pengembalian Dewi
16. Cone terlihat rusak/pecah
17. Klik "Tolak"
18. Input keterangan: "Cone rusak/pecah, harus diganti terlebih dahulu"
19. **DENDA FREEZE DIBUAT:**
    - Tanggal ditolak: 2025-01-25
    - Hari terlambat: 8 hari
    - Jumlah denda: Rp 40.000 (FREEZE)
    - Status: belum_bayar
20. Logout

21. Login sebagai Dewi
22. Masuk "Riwayat Peminjam"
23. Lihat peminjaman Cone → Status pengembalian: "ditolak"
24. Baca keterangan admin: "Cone rusak/pecah, harus diganti terlebih dahulu"
25. Masuk "Denda Saya"
26. Lihat denda Rp 40.000 (FREEZE - tidak bertambah)
27. (Simulasi: Dewi ganti cone yang rusak)
28. Kembali ke "Riwayat Peminjam"
29. Klik tombol **"Ajukan Ulang"**
30. Upload bukti pengembalian baru (cone sudah diganti)
31. Klik "Ajukan"
32. Logout

33. Login sebagai Admin
34. Masuk "Request Pengembalian"
35. Lihat pengajuan ulang Dewi
36. Lihat bukti baru (cone sudah diganti)
37. Klik "Setujui"
38. Stok Cone bertambah 2
39. Denda Rp 40.000 tetap ada (status: belum bayar)
40. Logout

41. Login sebagai Dewi
42. Masuk "Denda Saya"
43. Lihat denda Rp 40.000 (harus dibayar)
44. (Simulasi: Dewi bayar denda)
45. Denda berubah status → "lunas"

✅ SELESAI - Denda freeze berhasil, ajukan ulang berhasil
```

---

## 🔑 6. Login Credentials

### Admin
```
Username: admin
Password: admin123
```

### Peminjam (Pilih salah satu)
```
1. Budi Santoso
   Username: 2101001
   Password: password123
   
2. Siti Nurhaliza
   Username: 2101002
   Password: password123
   
3. Andi Wijaya
   Username: 2101003
   Password: password123
   
4. Dewi Lestari
   Username: 2101004
   Password: password123
   
5. Rudi Hermawan
   Username: 2101005
   Password: password123
```

---

## 🔧 7. Troubleshooting

### Error: "DashboardDenda cannot be resolved to a type"
**Solusi:**
```
1. Buka NetBeans
2. Klik: Run → Clean and Build Project (Shift + F11)
3. Tunggu sampai build selesai (lihat Output window)
4. Run Project (F6)
```

### Error: "Cannot connect to database"
**Solusi:**
```
1. Pastikan MySQL service sudah running
2. Cek konfigurasi di DatabaseConnection.java:
   - URL: jdbc:mysql://localhost:3306/sistem_inventaris_ukm
   - USER: root (atau sesuai username MySQL Anda)
   - PASSWORD: (sesuaikan dengan password MySQL Anda)
3. Pastikan database "sistem_inventaris_ukm" sudah ada
4. Jalankan ulang SQL file
```

### Error: "Table doesn't exist"
**Solusi:**
```
1. Buka MySQL Workbench/phpMyAdmin
2. Jalankan ulang file: sistem_inventaris_ukm (2).sql
3. Jalankan ulang file: DATA_DUMMY_LENGKAP.sql
4. Refresh aplikasi
```

### Stok Barang Tidak Update Setelah Peminjaman Disetujui
**Solusi:**
```
1. Cek file AdminDAO.java → method setujuiPeminjaman()
2. Pastikan ada query UPDATE barang SET stok = stok - ? WHERE id_barang = ?
3. Restart aplikasi
```

### Denda Tidak Muncul di Dashboard Denda
**Solusi:**
```
1. Cek apakah ada peminjaman yang terlambat dan belum dikembalikan
2. Cek tabel denda di database (pastikan ada record)
3. Cek file DendaService.java → method getDendaByUser()
4. Restart aplikasi
```

### Tombol "Ajukan Ulang" Tidak Muncul
**Solusi:**
```
1. Pastikan pengembalian benar-benar ditolak (status = 'ditolak')
2. Cek file RiwayatPeminjam.java → method createRiwayatRow()
3. Cek query: SELECT status FROM pengembalian WHERE id_peminjaman = ? ORDER BY id_pengembalian DESC LIMIT 1
4. Restart aplikasi
```

---

## 📊 8. Data Dummy Summary

### Users (6 users)
- 1 Admin: `admin` (password: admin123)
- 5 Peminjam: NIM 2101001-2101005 (password: password123)

### Peminjaman (13 total)
- **Status Proses:** 3 peminjaman (menunggu approval admin)
- **Status Disetujui - Belum Dikembalikan:** 4 peminjaman (sedang dipinjam)
- **Status Disetujui - Sudah Dikembalikan Tepat Waktu:** 2 peminjaman (no denda)
- **Status Disetujui - Dikembalikan Terlambat (Denda Lunas):** 1 peminjaman
- **Status Disetujui - Dikembalikan Terlambat (Denda Freeze):** 1 peminjaman
- **Status Ditolak:** 2 peminjaman

### Pengembalian (6 total)
- **Status Disetujui:** 3 pengembalian (no issues)
- **Status Ditolak:** 1 pengembalian (trigger denda freeze)
- **Status Proses - Ajukan Ulang:** 1 pengembalian (after ditolak)
- **Status Proses - Normal:** 2 pengembalian (menunggu approval)

### Denda (2 total)
- **Status Lunas:** 1 denda (Rp 30.000 - sudah dibayar)
- **Status Belum Bayar (FREEZE):** 1 denda (Rp 70.000 - freeze karena ditolak)
- **Real-Time Denda:** 2 peminjaman terlambat (belum ada record, dihitung real-time)

---

## ✅ Checklist Testing

### Fitur Peminjam
- [ ] Login sebagai peminjam
- [ ] Lihat katalog barang
- [ ] Filter barang berdasarkan kategori
- [ ] Search barang
- [ ] Lihat detail barang
- [ ] Ajukan peminjaman baru
- [ ] Upload bukti validasi
- [ ] Lihat riwayat peminjaman
- [ ] Ajukan pengembalian (status disetujui)
- [ ] Ajukan ulang pengembalian (status ditolak)
- [ ] Lihat denda real-time (barang terlambat)
- [ ] Lihat denda freeze (pengembalian ditolak)
- [ ] Edit profil
- [ ] Logout

### Fitur Admin
- [ ] Login sebagai admin
- [ ] Lihat statistik dashboard (total stok, jenis, dipinjam, tersedia)
- [ ] Lihat request peminjaman (status proses)
- [ ] Setujui peminjaman (stok berkurang otomatis)
- [ ] Tolak peminjaman
- [ ] Lihat request pengembalian (status proses)
- [ ] Setujui pengembalian tepat waktu (no denda)
- [ ] Setujui pengembalian terlambat (auto denda)
- [ ] Tolak pengembalian (denda freeze)
- [ ] Lihat log peminjaman
- [ ] Filter log peminjaman by status
- [ ] Tambah barang baru
- [ ] Edit barang existing
- [ ] Hapus barang
- [ ] Logout

---

## 📞 Kontak

Jika ada pertanyaan atau masalah saat testing, hubungi:
- **Developer:** [Nama Developer]
- **Email:** [Email Developer]

---

**Catatan:** File ini dibuat untuk membantu testing sistem inventaris UKM. Pastikan semua fitur ditest sesuai skenario yang diberikan.

**Selamat Testing! 🎉**
