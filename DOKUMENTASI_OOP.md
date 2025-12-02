# 📘 DOKUMENTASI FITUR & KONSEP OOP

## 📌 Panduan Screenshot untuk Temanmu
**Instruksi:** Ambil screenshot setiap tampilan GUI yang disebutkan di bawah ini dan sisipkan ke dokumentasi ini.

---

## 1️⃣ FITUR PROFIL PEMINJAM

### 📸 Screenshot yang Diperlukan:
- Screenshot tampilan halaman Profil Peminjam yang menampilkan: Nama, NIM, Email, Kontak, Total Peminjaman, Barang Dipinjam

### 💻 Implementasi OOP:

**File:** `Peminjam/ProfilPeminjam.java`

```java
public class ProfilPeminjam extends javax.swing.JFrame {
    
    // ENCAPSULATION: Data user disimpan dalam session
    private void MuatDataProfil() {
        UserSession session = UserSession.getInstance(); // SINGLETON PATTERN
        
        // Mengambil data dari session
        String nama = session.getNamaUser();
        String nim = session.getNim();
        String email = session.getEmail();
        String hp = session.getKontak();

        // Menampilkan ke GUI
        jLabel25.setText(nama);
        jLabel26.setText(nim);
        jLabel27.setText(email);
        jLabel28.setText(hp);
    }
    
    // ABSTRACTION: Load statistik lewat DAO
    private void loadStatistikPeminjaman() {
        int idUser = UserSession.getInstance().getUserId();
        PeminjamanDAO dao = new PeminjamanDAO(); // DEPENDENCY
        
        List<RequestData> listDipinjam = dao.getPeminjamanAktif(idUser);
        jLabel30.setText(listDipinjam.size() + " Barang");
        
        int totalHistory = dao.hitungTotalRiwayat(idUser);
        jLabel29.setText(totalHistory + " Kali");
    }
}
```

**Konsep OOP:**
- ✅ **Encapsulation:** Data user diambil dari `UserSession` (private data)
- ✅ **Singleton Pattern:** `UserSession.getInstance()` - hanya 1 instance
- ✅ **Abstraction:** Akses database via `PeminjamanDAO`
- ✅ **Inheritance:** Extends `javax.swing.JFrame`

---

## 2️⃣ FITUR DASHBOARD DENDA (Real-Time & Freeze)

### 📸 Screenshot yang Diperlukan:
- Screenshot Dashboard Denda dengan Total Denda Belum Bayar
- Screenshot daftar denda showing: Nama Barang, Hari Telat, Jumlah Denda, Status

### 💻 Implementasi OOP:

**File:** `Peminjam/DashboardDenda.java`

```java
public class DashboardDenda extends javax.swing.JFrame {
    
    private int userId;
    private DendaService dendaService; // COMPOSITION
    
    public DashboardDenda() {
        this.userId = UserSession.getInstance().getUserId();
        this.dendaService = new DendaService(); // DEPENDENCY INJECTION
        loadDendaData();
    }
    
    // POLYMORPHISM: Method untuk load data
    private void loadDendaData() {
        List<Denda> dendaList = dendaService.getDendaByUser(userId);
        
        panelDenda.removeAll();
        for (Denda d : dendaList) {
            JPanel card = createDendaCard(d); // ENCAPSULATION
            panelDenda.add(card);
        }
        
        updateTotalDenda(dendaList); // ABSTRACTION
    }
    
    private void updateTotalDenda(List<Denda> dendaList) {
        int total = 0;
        for (Denda d : dendaList) {
            if ("belum_bayar".equals(d.getStatus())) {
                total += d.getJumlahDenda();
            }
        }
        lblAmount.setText("Rp " + String.format("%,d", total));
    }
}
```

**Konsep OOP:**
- ✅ **Composition:** `DendaService` sebagai komponen
- ✅ **Encapsulation:** Method `createDendaCard()` private
- ✅ **Abstraction:** Business logic di `DendaService`

---

## 3️⃣ FITUR DASHBOARD PEMINJAM (Katalog)

### 📸 Screenshot yang Diperlukan:
- Screenshot katalog barang dengan kartu-kartu barang (grid layout)
- Screenshot dropdown filter kategori
- Screenshot search bar

### 💻 Implementasi OOP:

**File:** `Peminjam/DashboardPeminjam.java`

```java
public class DashboardPeminjam extends javax.swing.JFrame {
    
    // POLYMORPHISM: Method overloading
    private void loadDataBarang(String keyword, String kategori) {
        BarangDAO dao = new BarangDAO();
        List<Barang> data = dao.filterBarang(keyword, kategori); // ABSTRACTION
        
        panelDaftarBarang.removeAll();
        
        // LOOP & CREATE CARDS
        for (Barang b : data) {
            JPanel card = createCard(b); // ENCAPSULATION
            panelDaftarBarang.add(card);
        }
        
        panelDaftarBarang.revalidate();
        panelDaftarBarang.repaint();
    }
    
    // ENCAPSULATION: Private method untuk create card
    private JPanel createCard(Barang b) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        
        JLabel lblNama = new JLabel(b.getNama()); // GETTER dari Model
        JLabel lblStatus = new JLabel(b.getStatus());
        JButton btnDetail = new JButton("Detail");
        
        btnDetail.addActionListener(e -> {
            new DetailBarang(b.getId()).setVisible(true); // NAVIGATION
        });
        
        // ... setup layout
        return panel;
    }
}
```

**Konsep OOP:**
- ✅ **Abstraction:** DAO pattern untuk akses database
- ✅ **Encapsulation:** Private method `createCard()`
- ✅ **Object Creation:** Setiap barang jadi object `Barang`

---

## 4️⃣ FITUR RIWAYAT PEMINJAMAN (Ajukan Pengembalian & Ajukan Ulang)

### 📸 Screenshot yang Diperlukan:
- Screenshot riwayat showing tombol "Ajukan Pengembalian" (status disetujui)
- Screenshot riwayat showing tombol "Ajukan Ulang" (status pengembalian ditolak)

### 💻 Implementasi OOP:

**File:** `Peminjam/RiwayatPeminjam.java`

```java
private JPanel createRiwayatRow(RequestData rd) {
    JPanel panel = new JPanel();
    
    // CONDITIONAL LOGIC: Tampilkan tombol sesuai status
    String statusPengembalian = dao.getStatusPengembalian(rd.getId());
    
    if ("disetujui".equals(rd.getStatus()) && 
        (statusPengembalian == null || statusPengembalian.isEmpty())) {
        
        // Tombol AJUKAN PENGEMBALIAN
        JButton btnKembali = new JButton("Ajukan Pengembalian");
        btnKembali.addActionListener(e -> {
            new HalamanPengembalian(rd.getId()).setVisible(true); // NAVIGATION
        });
        panel.add(btnKembali);
    } 
    else if ("pengembalian_ditolak".equals(statusPengembalian)) {
        
        // Tombol AJUKAN ULANG
        JButton btnAjukanUlang = new JButton("Ajukan Ulang");
        btnAjukanUlang.addActionListener(e -> {
            new HalamanPengembalian(rd.getId()).setVisible(true);
        });
        panel.add(btnAjukanUlang);
    }
    
    return panel;
}
```

**Konsep OOP:**
- ✅ **Conditional Logic:** Dynamic button based on status
- ✅ **Navigation:** Object creation untuk pindah halaman
- ✅ **Event Handling:** ActionListener (Observer Pattern)

---

## 5️⃣ FITUR DASHBOARD ADMIN (Statistik)

### 📸 Screenshot yang Diperlukan:
- Screenshot Dashboard Admin dengan 4 statistik: Total Stok, Jenis Barang, Barang Dipinjam, Barang Tersedia

### 💻 Implementasi OOP:

**File:** `Admin/DashboardAdmin.java`

```java
public class DashboardAdmin extends javax.swing.JFrame {
    
    // ABSTRACTION: Load data via query
    private void loadDataStatistik() {
        Connection conn = DatabaseConnection.getConnection();
        
        try {
            // 1. Total Stok
            String sqlStok = "SELECT COALESCE(SUM(stok), 0) AS total_stok FROM barang";
            PreparedStatement pst = conn.prepareStatement(sqlStok);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                int totalStok = rs.getInt("total_stok");
                jLabel10.setText(totalStok + " Barang");
            }
            
            // 2. Total Jenis
            String sqlJenis = "SELECT COUNT(*) AS total_jenis FROM barang";
            // ... similar pattern
            
            // 3. Barang Dipinjam
            String sqlDipinjam = "SELECT COALESCE(SUM(p.jumlah), 0) FROM peminjaman...";
            // ... similar pattern
            
            // 4. Barang Tersedia = Total Stok - Dipinjam
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**Konsep OOP:**
- ✅ **Database Access:** Direct SQL (bisa pakai DAO juga)
- ✅ **Encapsulation:** Method `loadDataStatistik()` private
- ✅ **Error Handling:** Try-catch block

---

## 6️⃣ FITUR REQUEST PENGEMBALIAN ADMIN (Auto Denda & Denda Freeze)

### 📸 Screenshot yang Diperlukan:
- Screenshot list request pengembalian dengan tombol "Setujui" dan "Tolak"
- Screenshot dialog input keterangan admin saat menolak

### 💻 Implementasi OOP:

**File:** `Admin/RequestPengembalian.java` & `DAO/AdminDAO.java`

```java
// ===== RequestPengembalian.java =====
private void btnSetujuiClick(RequestData rd) {
    AdminDAO dao = new AdminDAO();
    boolean success = dao.prosesPengembalian(
        rd.getIdPengembalian(), 
        "disetujui", 
        "" // no keterangan
    );
    
    if (success) {
        JOptionPane.showMessageDialog(this, "Pengembalian disetujui!");
        loadDaftarPengembalian(); // REFRESH
    }
}

// ===== AdminDAO.java =====
public boolean prosesPengembalian(int idPengembalian, String status, String keterangan) {
    try {
        // 1. Update status pengembalian
        String sql = "UPDATE pengembalian SET status=?, keterangan_admin=? ...";
        
        // 2. Get data peminjaman
        String sqlGet = "SELECT * FROM pengembalian WHERE id_pengembalian=?";
        
        if ("disetujui".equals(status)) {
            // 3. Kembalikan stok
            updateStok(idBarang, jumlah, "+");
            
            // 4. Cek apakah terlambat
            if (tanggalKembali.after(deadline)) {
                long hari = hitungHariTerlambat(deadline, tanggalKembali);
                int denda = (int) hari * 5000;
                
                // 5. Insert denda otomatis
                insertDenda(idPeminjaman, idUser, hari, denda);
            }
        } 
        else if ("ditolak".equals(status)) {
            // DENDA FREEZE
            if (tanggalKembali.after(deadline)) {
                long hari = hitungHariTerlambat(deadline, CURDATE());
                int denda = (int) hari * 5000;
                
                // Insert denda dengan tanggal_ditolak
                String sqlDenda = "INSERT INTO denda (..., tanggal_ditolak) VALUES (...)";
            }
        }
        
        return true;
    } catch (Exception e) {
        return false;
    }
}
```

**Konsep OOP:**
- ✅ **Transaction Logic:** Multiple operations dalam 1 method
- ✅ **Conditional Logic:** Beda alur untuk setujui vs tolak
- ✅ **Business Logic:** Auto calculate denda
- ✅ **Data Persistence:** Insert ke tabel denda

---

## 📊 KONSEP OOP YANG DIGUNAKAN

### 🔹 1. Encapsulation
- Private methods: `createCard()`, `loadData()`, dll
- Private fields: `userId`, `dendaService`
- Getter/Setter di Model classes

### 🔹 2. Inheritance
- Semua form extends `javax.swing.JFrame`
- Model classes bisa extends base class

### 🔹 3. Polymorphism
- Method overloading (misal: `loadData()` dengan beda parameter)
- Interface implementation (ActionListener)

### 🔹 4. Abstraction
- DAO Pattern: `BarangDAO`, `PeminjamanDAO`, `AdminDAO`
- Service Layer: `DendaService`
- Database operations disembunyikan

### 🔹 5. Design Patterns
- **Singleton:** `UserSession.getInstance()`
- **DAO Pattern:** Semua akses database via DAO
- **MVC-like:** Model (POJO), View (JFrame), Controller (DAO/Service)

---

## 🎯 STRUKTUR PROJECT

```
src/main/java/
├── Admin/              → Admin GUI (JFrame)
├── Peminjam/           → Peminjam GUI (JFrame)
├── Register/           → Login/Register GUI
├── Model/              → POJO Classes (Barang, Denda, RequestData)
├── DAO/                → Data Access Objects
├── Service/            → Business Logic (DendaService)
├── Utils/              → Utility Classes (UserSession, SessionHelper)
└── Database/           → Database Connection
```

---

**Catatan untuk Temanmu:**
1. Screenshot setiap tampilan GUI sesuai instruksi di atas
2. Sisipkan screenshot di sebelah deskripsi GUI
3. Code OOP sudah dijelaskan dengan comment
4. Fokus pada konsep: Encapsulation, Inheritance, Polymorphism, Abstraction

**Selesai! 🎉**
