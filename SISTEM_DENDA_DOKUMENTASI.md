# 🚀 SISTEM DENDA - IMPLEMENTASI LENGKAP

## 📋 RINGKASAN

Sistem denda yang baru saja diimplementasikan adalah **upgrade besar** dari sistem sebelumnya yang hanya menampilkan peringatan visual. Sekarang sistem denda sudah **terintegrasi penuh dengan database** dan mengikuti **prinsip OOP**.

---

## 🎯 FITUR UTAMA

### 1. **Database Denda** ✅
- Tabel baru: `denda` dengan kolom lengkap
- Tracking: id_peminjaman, id_user, jumlah_denda, hari_telat, status_bayar
- Foreign key ke tabel peminjaman dan users

### 2. **Perhitungan Otomatis** 💰
- **Rp 5.000 per hari** keterlambatan (dinamis, bukan fixed!)
- Auto-calculate saat admin approve pengembalian
- Simpan ke database otomatis

### 3. **Blocking System** 🚫
- User **TIDAK BISA** pinjam barang kalau ada denda belum bayar
- Validasi langsung di `DetailBarang.java` sebelum ajukan peminjaman
- Pesan warning dengan total denda

### 4. **Dashboard untuk Peminjam** 📊
- Class baru: `DashboardDenda.java`
- Tampilkan semua denda (belum bayar & sudah lunas)
- Summary total denda belum bayar

### 5. **Dashboard untuk Admin** 👨‍💼
- Class baru: `ManajemenDenda.java`
- Monitor semua denda belum bayar dari semua user
- Tombol "Konfirmasi Bayar" untuk update status
- Total semua denda belum bayar (statistik)

### 6. **Tampilan Riwayat Terupdate** 🔄
- `RiwayatPeminjam.java` sekarang baca denda dari database
- Bukan lagi hardcoded "Rp 50.000"
- Tampilkan jumlah denda dinamis sesuai hari keterlambatan

---

## 🏗️ ARSITEKTUR (OOP)

### **Model Layer**
```
📁 Model/Denda.java
├─ Private fields dengan getter/setter (Encapsulation)
├─ Helper methods: getJumlahDendaFormatted(), isLunas()
└─ Data tambahan untuk display: namaUser, namaBarang
```

### **Service Layer** (Business Logic)
```
📁 Service/DendaService.java
├─ hitungDenda(idPeminjaman) → Hitung hari telat × Rp 5.000
├─ simpanDenda(Denda) → Save ke database
├─ getTotalDendaBelumBayar(idUser) → Total denda user
├─ punyaDendaBelumBayar(idUser) → Boolean check
├─ getDendaByUser(idUser) → List denda user
├─ getAllDendaBelumBayar() → List semua denda (admin)
├─ bayarDenda(idDenda) → Update status jadi 'lunas'
└─ getTotalSemuaDendaBelumBayar() → Total semua user (statistik)
```

### **DAO Layer** (Diupdate)
```
📁 DAO/AdminDAO.java
└─ prosesPengembalian() → Tambah logic:
   1. Cek keterlambatan
   2. Hitung denda pakai DendaService
   3. Simpan denda ke database
   4. Update status pengembalian
   5. Kembalikan stok barang
```

### **View Layer**
```
📁 Peminjam/DetailBarang.java
└─ Validasi denda sebelum ajukan peminjaman

📁 Peminjam/RiwayatPeminjam.java
└─ Tampilkan denda dari database (bukan hardcoded)

📁 Peminjam/DashboardDenda.java (NEW!)
└─ Dashboard denda untuk user

📁 Admin/ManajemenDenda.java (NEW!)
└─ Dashboard manajemen denda untuk admin
```

---

## 🔄 FLOW LENGKAP SISTEM DENDA

### **Scenario: User Terlambat Mengembalikan**

1. **User pinjam barang** (tgl_jatuh_tempo = +7 hari)
2. **Admin approve** peminjaman
3. User **terlambat** mengembalikan (lewat jatuh tempo)
4. User **ajukan pengembalian**
5. **Admin approve pengembalian** 
   - ✅ `AdminDAO.prosesPengembalian()` dipanggil
   - ✅ `DendaService.hitungDenda()` hitung hari telat
   - ✅ Denda = hari_telat × Rp 5.000
   - ✅ `DendaService.simpanDenda()` save ke database
   - ✅ Stok barang dikembalikan
   
6. **User coba pinjam lagi**
   - ❌ `DetailBarang.java` cek denda belum bayar
   - ❌ BLOCKED! Muncul dialog warning
   - ❌ Tidak bisa submit peminjaman

7. **User bayar denda** (offline ke admin)
8. **Admin konfirmasi bayar** di `ManajemenDenda`
   - ✅ Status denda → 'lunas'
   - ✅ Tanggal bayar di-set

9. **User bisa pinjam lagi** ✅

---

## 📊 DATABASE MIGRATION

### **Run SQL ini di phpMyAdmin:**

```sql
USE sistem_inventaris_ukm;

CREATE TABLE IF NOT EXISTS `denda` (
  `id_denda` INT NOT NULL AUTO_INCREMENT,
  `id_peminjaman` INT NOT NULL,
  `id_user` INT NOT NULL,
  `jumlah_denda` INT NOT NULL COMMENT 'Total denda dalam Rupiah',
  `hari_telat` INT NOT NULL COMMENT 'Jumlah hari keterlambatan',
  `tanggal_hitung` DATE NOT NULL COMMENT 'Tanggal denda dihitung',
  `status_bayar` ENUM('belum_bayar','lunas') NOT NULL DEFAULT 'belum_bayar',
  `tanggal_bayar` DATE NULL,
  `keterangan` VARCHAR(255) NULL,
  PRIMARY KEY (`id_denda`),
  KEY `id_peminjaman` (`id_peminjaman`),
  KEY `id_user` (`id_user`),
  CONSTRAINT `denda_ibfk_1` FOREIGN KEY (`id_peminjaman`) REFERENCES `peminjaman` (`id_peminjaman`),
  CONSTRAINT `denda_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**File migration:** `migration_add_denda_system.sql`

---

## 🎨 CARA AKSES FITUR

### **Untuk PEMINJAM:**

1. Login sebagai Peminjam
2. Menu: **"Denda Saya"** (perlu ditambahkan button di DashboardPeminjam)
3. Atau langsung run: `new DashboardDenda().setVisible(true);`
4. Lihat semua denda (belum bayar & lunas)
5. Jika ada denda belum bayar, **tidak bisa pinjam barang**

### **Untuk ADMIN:**

1. Login sebagai Admin
2. Menu: **"Manajemen Denda"** (perlu ditambahkan button di DashboardAdmin)
3. Atau langsung run: `new ManajemenDenda().setVisible(true);`
4. Lihat semua denda belum bayar dari semua user
5. Klik **"Konfirmasi Bayar"** untuk update status setelah user bayar

---

## 🔧 NEXT STEPS

### **Yang Sudah Selesai:** ✅
- [x] Tabel database denda
- [x] Model Denda.java (OOP)
- [x] DendaService.java (Business Logic)
- [x] Perhitungan otomatis Rp 5.000/hari
- [x] Auto create denda saat approve pengembalian terlambat
- [x] Blocking system (tidak bisa pinjam jika ada denda)
- [x] Dashboard denda untuk peminjam
- [x] Dashboard manajemen denda untuk admin
- [x] Update RiwayatPeminjam dengan denda dari database

### **Yang Perlu Ditambahkan (Optional):**
- [ ] Tombol menu "Denda" di DashboardPeminjam
- [ ] Tombol menu "Manajemen Denda" di DashboardAdmin
- [ ] Notifikasi mendekati jatuh tempo (H-1, H-2)
- [ ] Email reminder otomatis
- [ ] Print struk pembayaran denda
- [ ] Export laporan denda (Excel/PDF)

---

## 💡 KELEBIHAN SISTEM BARU

| Aspek | Sistem Lama | Sistem Baru |
|-------|-------------|-------------|
| **Storage** | Tidak ada (UI only) | Database table `denda` |
| **Jumlah Denda** | Fixed Rp 50.000 | Dinamis Rp 5.000/hari |
| **Tracking** | Tidak ada history | Full history belum bayar & lunas |
| **Enforcement** | Tidak ada blocking | User DIBLOKIR jika ada denda |
| **Admin Control** | Tidak ada | Dashboard konfirmasi pembayaran |
| **OOP** | ❌ | ✅ Model-Service-DAO pattern |
| **Payment Proof** | ❌ | ✅ Tanggal bayar tercatat |

---

## 🎓 PRINSIP OOP YANG DIGUNAKAN

1. **Encapsulation** → Private fields di `Denda.java`
2. **Separation of Concerns** → Service layer terpisah dari DAO
3. **Single Responsibility** → DendaService fokus logic denda saja
4. **Dependency Injection** → Service dipanggil di DAO/View
5. **Code Reusability** → DendaService dipakai di DetailBarang, AdminDAO, RiwayatPeminjam

---

## 📌 IMPORTANT NOTES

⚠️ **Jalankan migration SQL dulu sebelum compile!**

⚠️ **Restart NetBeans setelah migration** (Clean and Build)

⚠️ **Test dengan scenario:**
1. User pinjam barang
2. Admin approve
3. Ubah tanggal sistem ke 10 hari kedepan (atau edit manual tanggal_jatuh_tempo di DB)
4. User ajukan pengembalian
5. Admin approve → Denda auto created!
6. User coba pinjam lagi → BLOCKED!
7. Admin konfirmasi bayar
8. User bisa pinjam lagi ✅

---

**Dibuat dengan ❤️ menggunakan konsep PBO**
