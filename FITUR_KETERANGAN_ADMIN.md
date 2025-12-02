# Fitur Keterangan Admin untuk Pengembalian

## 📋 Deskripsi
Fitur ini memungkinkan admin untuk memberikan keterangan/pesan saat menyetujui atau menolak pengembalian barang. Keterangan ini akan ditampilkan kepada peminjam di halaman Riwayat.

## 🎯 Use Case
- **Penolakan**: Admin menolak pengembalian karena barang rusak → Admin mengirim pesan: *"Barang yang kamu kembalikan rusak, jumpai saya di UKM"*
- **Persetujuan (opsional)**: Admin menyetujui pengembalian → Admin bisa mengirim pesan: *"Pengembalian diterima dengan baik, terima kasih"*

---

## 🛠️ Perubahan yang Dilakukan

### 1. Database Schema
**File**: `alter_table_pengembalian.sql`

**Perubahan**:
```sql
ALTER TABLE `pengembalian` 
ADD COLUMN `keterangan_admin` TEXT NULL DEFAULT NULL 
AFTER `status`;
```

**Cara Menjalankan**:
```bash
mysql -u root -p sistem_inventaris_ukm < alter_table_pengembalian.sql
```

**Struktur Tabel Setelah Update**:
| Kolom | Tipe | Keterangan |
|-------|------|------------|
| id_pengembalian | INT | Primary Key |
| id_peminjaman | INT | Foreign Key |
| id_barang | INT | Foreign Key |
| id_user | INT | Foreign Key |
| jumlah | INT | Jumlah barang dikembalikan |
| tanggal_kembali | DATE | Tanggal pengembalian |
| status | ENUM | proses/disetujui/ditolak |
| **keterangan_admin** | **TEXT** | **Keterangan dari admin (BARU!)** |

---

### 2. Model Layer
**File**: `Model/RequestData.java`

**Perubahan**:
```java
// Tambah field baru
private String keteranganAdmin;

// Tambah getter & setter
public String getKeteranganAdmin() { return keteranganAdmin; }
public void setKeteranganAdmin(String keteranganAdmin) { 
    this.keteranganAdmin = keteranganAdmin; 
}
```

---

### 3. DAO Layer
**File**: `DAO/AdminDAO.java`

**Perubahan Method Signature**:
```java
// SEBELUM
public boolean prosesPengembalian(int idPengembalian, int idBarang, 
                                   int jumlah, String keputusan)

// SESUDAH (+ parameter keteranganAdmin)
public boolean prosesPengembalian(int idPengembalian, int idBarang, 
                                   int jumlah, String keputusan, 
                                   String keteranganAdmin)
```

**Perubahan SQL UPDATE**:
```java
// SEBELUM
String sqlUpdate = "UPDATE pengembalian SET status = ? WHERE id_pengembalian = ?";
ps.setString(1, statusAkhir);
ps.setInt(2, idPengembalian);

// SESUDAH
String sqlUpdate = "UPDATE pengembalian SET status = ?, keterangan_admin = ? 
                    WHERE id_pengembalian = ?";
ps.setString(1, statusAkhir);
ps.setString(2, keteranganAdmin);  // BARU!
ps.setInt(3, idPengembalian);
```

**File**: `DAO/PeminjamanDAO.java`

**Perubahan SQL SELECT di getRiwayatUser()**:
```java
// Tambah pg.keterangan_admin ke SELECT statement
String sql = "SELECT p.id_peminjaman, b.nama_barang, p.tanggal_pinjam, 
              p.tanggal_jatuh_tempo, p.status, p.jumlah, p.keterangan, 
              pg.status as status_pengembalian, pg.tanggal_kembali, 
              pg.keterangan_admin  -- BARU!
              FROM peminjaman p ...";

// Tambah mapping ke RequestData
rd.setKeteranganAdmin(rs.getString("keterangan_admin"));
```

---

### 4. UI Admin Layer
**File**: `Admin/RequestPengembalian.java`

**Perubahan di Event Handler btnOk**:
```java
btnOk.addActionListener(e -> {
    String aksi = cmb.getSelectedItem().toString();
    if (aksi.equals("Pilih...")) return;
    
    if (JOptionPane.showConfirmDialog(...) == JOptionPane.YES_OPTION) {
        String keterangan = "";
        
        // BARU: Jika admin menolak, minta keterangan wajib
        if (aksi.equals("Tolak")) {
            keterangan = JOptionPane.showInputDialog(this,
                "Masukkan keterangan penolakan:\n" +
                "(contoh: Barang rusak, jumpai saya di UKM)",
                "Keterangan Penolakan",
                JOptionPane.PLAIN_MESSAGE);
            
            // Validasi keterangan tidak boleh kosong
            if (keterangan == null || keterangan.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Keterangan wajib diisi untuk penolakan!", 
                    "Peringatan", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Update method call dengan parameter keterangan
        AdminDAO dao = new AdminDAO();
        boolean sukses = dao.prosesPengembalian(
            rd.getIdPengembalian(), 
            rd.getIdBarang(), 
            rd.getJumlah(), 
            aksi, 
            keterangan  // BARU!
        );
        
        if (sukses) {
            JOptionPane.showMessageDialog(this, "Berhasil diproses!");
            loadDaftarPengembalian();
        }
    }
});
```

**Flow Admin**:
1. Admin klik dropdown → Pilih "Tolak"
2. Admin klik tombol "Konfirmasi"
3. **Dialog input muncul**: "Masukkan keterangan penolakan"
4. Admin ketik: "Barang rusak, jumpai saya di UKM"
5. Klik OK → Keterangan disimpan ke database

---

### 5. UI Peminjam Layer
**File**: `Peminjam/RiwayatPeminjam.java`

**Perubahan di createRiwayatRow()**:
```java
// BARU: Tambahkan tombol info (ℹ) untuk pengembalian ditolak
if ("pengembalian_ditolak".equalsIgnoreCase(status) && 
    item.getKeteranganAdmin() != null && 
    !item.getKeteranganAdmin().trim().isEmpty()) {
    
    JButton btnInfo = new JButton("ℹ");
    btnInfo.setFont(new Font("Segoe UI", 1, 12));
    btnInfo.setForeground(Color.WHITE);
    btnInfo.setBackground(new Color(255, 102, 102));
    btnInfo.setBounds(622, 15, 30, 25);
    btnInfo.setToolTipText("Lihat keterangan admin");
    
    btnInfo.addActionListener(e -> {
        JOptionPane.showMessageDialog(this,
            "Alasan penolakan:\n" + item.getKeteranganAdmin(),
            "Keterangan Admin",
            JOptionPane.INFORMATION_MESSAGE);
    });
    
    panel.add(btnInfo);
}
```

**Flow Peminjam**:
1. Peminjam buka halaman Riwayat
2. Lihat item dengan status **PENGEMBALIAN DITOLAK** (merah)
3. Klik tombol **ℹ** (info) di sebelah status
4. **Dialog muncul** dengan pesan dari admin: *"Barang yang kamu kembalikan rusak, jumpai saya di UKM"*

---

## 🎨 Tampilan UI

### Admin - Request Pengembalian
```
┌─────────────────────────────────────────────────┐
│ Request Pengembalian                            │
├─────────────────────────────────────────────────┤
│ Nama Barang: Proyektor Epson                    │
│ Peminjam: Budi Prasetyo                         │
│ Jumlah: 1                                       │
│ Tanggal Kembali: 15 Des 2024                    │
│                                                 │
│ [Lihat Bukti]                                   │
│                                                 │
│ [Pilih... ▼]  [Konfirmasi]                      │
│  - Pilih...                                     │
│  - Setujui                                      │
│  - Tolak ← Dipilih                              │
└─────────────────────────────────────────────────┘

↓ Klik Konfirmasi

┌─────────────────────────────────────────────────┐
│ Keterangan Penolakan                       [X]  │
├─────────────────────────────────────────────────┤
│ Masukkan keterangan penolakan:                  │
│ (contoh: Barang rusak, jumpai saya di UKM)      │
│                                                 │
│ ┌───────────────────────────────────────────┐   │
│ │ Barang rusak, jumpai saya di UKM          │   │
│ └───────────────────────────────────────────┘   │
│                                                 │
│                        [OK]    [Cancel]         │
└─────────────────────────────────────────────────┘
```

### Peminjam - Riwayat Peminjaman
```
┌───────────────────────────────────────────────────────────────┐
│ Riwayat Peminjaman                                            │
├───────────────────────────────────────────────────────────────┤
│ Proyektor Epson                                               │
│ 15 Des 2024    20 Des 2024    PENGEMBALIAN DITOLAK  [ℹ]       │
│                                     ↑                          │
│                          Klik untuk lihat alasan               │
└───────────────────────────────────────────────────────────────┘

↓ Klik tombol ℹ

┌─────────────────────────────────────────────────┐
│ Keterangan Admin                           [X]  │
├─────────────────────────────────────────────────┤
│ Alasan penolakan:                               │
│ Barang rusak, jumpai saya di UKM                │
│                                                 │
│                        [OK]                     │
└─────────────────────────────────────────────────┘
```

---

## ✅ Cara Testing

### 1. Setup Database
```bash
# Jalankan ALTER TABLE script
mysql -u root -p sistem_inventaris_ukm < alter_table_pengembalian.sql

# Verifikasi struktur tabel
mysql -u root -p -e "DESCRIBE sistem_inventaris_ukm.pengembalian"
```

### 2. Clean & Build Project
```bash
# Di NetBeans atau terminal
mvn clean compile
```

### 3. Test Flow Admin
1. Login sebagai admin
2. Buka menu **Request Pengembalian**
3. Pilih request pengembalian yang sedang diproses
4. Klik dropdown → Pilih **Tolak**
5. Klik tombol **Konfirmasi**
6. **Dialog input muncul** → Ketik: "Barang rusak, jumpai saya di UKM"
7. Klik **OK**
8. Verifikasi: Pesan "Berhasil diproses!" muncul

### 4. Test Flow Peminjam
1. Login sebagai peminjam (yang pengembaliannya ditolak)
2. Buka menu **Riwayat**
3. Cari item dengan status **PENGEMBALIAN DITOLAK** (warna merah)
4. Klik tombol **ℹ** (info) di sebelah status
5. **Dialog muncul** dengan keterangan dari admin
6. Verifikasi: Pesan admin tampil dengan benar

### 5. Validasi Database
```sql
-- Cek data keterangan_admin tersimpan
SELECT id_pengembalian, status, keterangan_admin 
FROM pengembalian 
WHERE status = 'ditolak';

-- Expected output:
-- +------------------+--------+------------------------------------------+
-- | id_pengembalian  | status | keterangan_admin                         |
-- +------------------+--------+------------------------------------------+
-- | 5                | ditolak| Barang rusak, jumpai saya di UKM         |
-- +------------------+--------+------------------------------------------+
```

---

## 🔍 Validasi & Error Handling

### Admin Side
- ✅ Keterangan **WAJIB** diisi jika admin memilih "Tolak"
- ✅ Jika dialog di-cancel (null) atau kosong → Muncul peringatan
- ✅ Jika admin pilih "Setujui" → Keterangan opsional (bisa kosong)

### Peminjam Side
- ✅ Tombol ℹ **HANYA** muncul jika:
  - Status = "pengembalian_ditolak"
  - keterangan_admin **NOT NULL** dan **NOT EMPTY**
- ✅ Jika tidak ada keterangan → Tombol tidak tampil

---

## 📊 Struktur Kode

```
sisteminventarisukm/
│
├── alter_table_pengembalian.sql       ← Database schema update
│
├── src/main/java/
│   ├── Model/
│   │   └── RequestData.java           ← +keteranganAdmin field
│   │
│   ├── DAO/
│   │   ├── AdminDAO.java              ← +keteranganAdmin param
│   │   └── PeminjamanDAO.java         ← Query pg.keterangan_admin
│   │
│   ├── Admin/
│   │   └── RequestPengembalian.java   ← Input dialog untuk keterangan
│   │
│   └── Peminjam/
│       └── RiwayatPeminjam.java       ← Display keterangan dengan tombol ℹ
│
└── FITUR_KETERANGAN_ADMIN.md          ← Dokumentasi ini
```

---

## 🚀 Status Implementasi

| No | Task | Status |
|----|------|--------|
| 1 | Tambah kolom keterangan_admin ke tabel pengembalian | ✅ DONE |
| 2 | Update Model/RequestData.java | ✅ DONE |
| 3 | Update DAO/AdminDAO.java (method signature + SQL) | ✅ DONE |
| 4 | Update DAO/PeminjamanDAO.java (query + mapping) | ✅ DONE |
| 5 | Update Admin/RequestPengembalian.java (input dialog) | ✅ DONE |
| 6 | Update Peminjam/RiwayatPeminjam.java (display keterangan) | ✅ DONE |
| 7 | Testing end-to-end flow | ⏳ PENDING |

---

## 📝 Catatan Penting

1. **Database Migration**: Pastikan ALTER TABLE script sudah dijalankan sebelum testing!
2. **Validasi Input**: Keterangan wajib untuk "Tolak", opsional untuk "Setujui"
3. **Character Limit**: TEXT field bisa menampung hingga 65,535 karakter
4. **Null Handling**: Kode sudah handle null/empty string dengan baik
5. **UI Positioning**: Tombol ℹ di posisi 622px (sebelah status label)

---

## 🎓 Contoh Keterangan Admin

### Penolakan
- "Barang yang kamu kembalikan rusak, jumpai saya di UKM"
- "Kondisi barang tidak sesuai, silakan hubungi admin"
- "Barang hilang beberapa komponen, segera hubungi kami"
- "Pengembalian terlambat dan barang rusak, ada denda tambahan"

### Persetujuan (Opsional)
- "Pengembalian diterima dengan baik, terima kasih"
- "Barang dalam kondisi baik, terima kasih sudah menjaga"
- "OK, barang sudah diterima"

---

## 📞 Support

Jika ada masalah saat implementasi:
1. Cek console untuk error log
2. Verify database schema dengan `DESCRIBE pengembalian`
3. Test dengan data dummy terlebih dahulu
4. Clean & rebuild project jika ada compilation error

---

**Fitur ini sudah siap digunakan!** 🎉

Silakan jalankan `alter_table_pengembalian.sql` di database, lalu clean & build project untuk testing.
