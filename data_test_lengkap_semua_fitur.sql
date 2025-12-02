-- ============================================================
-- DATA DUMMY LENGKAP UNTUK TESTING SEMUA FITUR
-- Database: sistem_inventaris_ukm
-- Tanggal: 1 Desember 2025
-- ============================================================
-- FITUR YANG DI-TEST:
-- 1. ✅ Login Admin & Peminjam
-- 2. ✅ CRUD Barang (Tambah, Edit, Hapus)
-- 3. ✅ Request Peminjaman & Approval/Reject
-- 4. ✅ Batas Maksimal 2 Peminjaman Aktif
-- 5. ✅ Blocking Peminjaman jika Ada Denda Belum Bayar
-- 6. ✅ Request Pengembalian dengan Keterangan Admin
-- 7. ✅ Perhitungan Denda Otomatis
-- 8. ✅ Dashboard Denda & Riwayat
-- ============================================================

USE sistem_inventaris_ukm;

-- ============================================================
-- 1. BERSIHKAN DATA LAMA (untuk fresh testing)
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE log_aktivitas;
TRUNCATE TABLE denda;
TRUNCATE TABLE pengembalian;
TRUNCATE TABLE peminjaman;
TRUNCATE TABLE riwayat;
TRUNCATE TABLE barang;
TRUNCATE TABLE kategori_barang;
DELETE FROM users WHERE id_user > 1; -- Jangan hapus admin default

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 2. USER TESTING (Admin + 3 Peminjam dengan Skenario Berbeda)
-- ============================================================

-- Admin (sudah ada di database, pastikan password: admin123)
UPDATE users SET password = 'admin123' WHERE role = 'admin' LIMIT 1;

-- Peminjam 1: Budi (punya 2 peminjaman aktif - untuk test batas maksimal)
INSERT INTO users (nim, nama, password, email, no_hp, alamat, role) VALUES
('2101001', 'Budi Santoso', 'password123', 'budi@student.ac.id', '081234567890', 'Jl. Sudirman No. 10', 'user');

-- Peminjam 2: Siti (punya denda belum bayar - untuk test blocking)
INSERT INTO users (nim, nama, password, email, no_hp, alamat, role) VALUES
('2101002', 'Siti Nurhaliza', 'password123', 'siti@student.ac.id', '081234567891', 'Jl. Gatot Subroto No. 20', 'user');

-- Peminjam 3: Ahmad (bersih, bisa pinjam normal)
INSERT INTO users (nim, nama, password, email, no_hp, alamat, role) VALUES
('2101003', 'Ahmad Wijaya', 'password123', 'ahmad@student.ac.id', '081234567892', 'Jl. Diponegoro No. 30', 'user');

-- Peminjam 4: Rina (punya pengembalian ditolak dengan keterangan admin)
INSERT INTO users (nim, nama, password, email, no_hp, alamat, role) VALUES
('2101004', 'Rina Kusuma', 'password123', 'rina@student.ac.id', '081234567893', 'Jl. Ahmad Yani No. 40', 'user');

-- ============================================================
-- 3. KATEGORI BARANG
-- ============================================================

INSERT INTO kategori_barang (nama_kategori, deskripsi) VALUES
('Elektronik', 'Peralatan elektronik seperti kamera, mic, laptop'),
('Olahraga', 'Perlengkapan olahraga seperti bola, raket, matras'),
('Alat Tulis', 'Perlengkapan tulis seperti spidol, kertas, gunting'),
('Kostum', 'Kostum untuk pertunjukan dan acara'),
('Alat Musik', 'Alat musik untuk latihan dan pertunjukan');

-- ============================================================
-- 4. BARANG (Stock Bervariasi untuk Testing)
-- ============================================================

INSERT INTO barang (nama_barang, id_kategori, jumlah_total, jumlah_tersedia, deskripsi, lokasi_penyimpanan) VALUES
-- Elektronik
('Kamera DSLR Canon', 1, 3, 1, 'Kamera profesional untuk dokumentasi', 'Lemari A - Rak 1'),
('Microphone Wireless', 1, 5, 3, 'Mic wireless untuk acara', 'Lemari A - Rak 2'),
('Proyektor Epson', 1, 2, 0, 'Proyektor untuk presentasi (HABIS - untuk test stock)', 'Lemari A - Rak 3'),
('Laptop Asus ROG', 1, 2, 2, 'Laptop gaming untuk editing', 'Lemari A - Rak 4'),

-- Olahraga
('Bola Futsal', 2, 10, 8, 'Bola futsal official', 'Gudang B - Rak 1'),
('Raket Badminton', 2, 6, 6, 'Raket badminton Yonex', 'Gudang B - Rak 2'),
('Matras Yoga', 2, 8, 8, 'Matras untuk latihan yoga', 'Gudang B - Rak 3'),

-- Alat Tulis
('Spidol Whiteboard', 3, 20, 15, 'Spidol berbagai warna', 'Lemari C - Laci 1'),
('Gunting Besar', 3, 10, 10, 'Gunting untuk kerajinan', 'Lemari C - Laci 2'),

-- Kostum
('Kostum Tari Tradisional', 4, 5, 4, 'Kostum tari daerah', 'Lemari D - Gantung 1'),
('Jas Formal Hitam', 4, 3, 3, 'Jas untuk acara formal', 'Lemari D - Gantung 2'),

-- Alat Musik
('Gitar Akustik Yamaha', 5, 4, 3, 'Gitar akustik untuk latihan', 'Ruang Musik - Stand 1'),
('Keyboard Casio', 5, 2, 2, 'Keyboard elektronik', 'Ruang Musik - Stand 2');

-- ============================================================
-- 5. PEMINJAMAN - SKENARIO BUDI (2 Peminjaman Aktif)
-- ============================================================
-- Budi punya 2 peminjaman yang sudah disetujui dan belum dikembalikan
-- Tujuan: Test batas maksimal 2 peminjaman aktif

SET @budi_id = (SELECT id_user FROM users WHERE nim = '2101001');
SET @kamera_id = (SELECT id_barang FROM barang WHERE nama_barang = 'Kamera DSLR Canon');
SET @mic_id = (SELECT id_barang FROM barang WHERE nama_barang = 'Microphone Wireless');

-- Peminjaman 1: Kamera (disetujui, belum kembali)
INSERT INTO peminjaman (id_user, id_barang, jumlah, tanggal_pinjam, tanggal_jatuh_tempo, keterangan, status, bukti_validasi) VALUES
(@budi_id, @kamera_id, 1, '2025-11-20', '2025-11-25', 'Untuk dokumentasi kegiatan UKM', 'disetujui', 'ktm_budi_001.jpg');
SET @pinjam_budi_1 = LAST_INSERT_ID();

-- Peminjaman 2: Microphone (disetujui, belum kembali)
INSERT INTO peminjaman (id_user, id_barang, jumlah, tanggal_pinjam, tanggal_jatuh_tempo, keterangan, status, bukti_validasi) VALUES
(@budi_id, @mic_id, 2, '2025-11-22', '2025-11-27', 'Untuk acara seminar UKM', 'disetujui', 'ktm_budi_002.jpg');
SET @pinjam_budi_2 = LAST_INSERT_ID();

-- Update stok barang (karena dipinjam)
UPDATE barang SET jumlah_tersedia = jumlah_tersedia - 1 WHERE id_barang = @kamera_id;
UPDATE barang SET jumlah_tersedia = jumlah_tersedia - 2 WHERE id_barang = @mic_id;

-- ============================================================
-- 6. PEMINJAMAN - SKENARIO SITI (Punya Denda Belum Bayar)
-- ============================================================
-- Siti punya peminjaman yang telat dikembalikan dan punya denda belum bayar
-- Tujuan: Test blocking peminjaman jika ada denda

SET @siti_id = (SELECT id_user FROM users WHERE nim = '2101002');
SET @gitar_id = (SELECT id_barang FROM barang WHERE nama_barang = 'Gitar Akustik Yamaha');

-- Peminjaman: Gitar (sudah dikembalikan tapi telat)
INSERT INTO peminjaman (id_user, id_barang, jumlah, tanggal_pinjam, tanggal_jatuh_tempo, keterangan, status, bukti_validasi) VALUES
(@siti_id, @gitar_id, 1, '2025-11-01', '2025-11-07', 'Untuk latihan musik', 'disetujui', 'ktm_siti_001.jpg');
SET @pinjam_siti_1 = LAST_INSERT_ID();

-- Pengembalian: Dikembalikan tanggal 13 Nov (telat 6 hari)
INSERT INTO pengembalian (id_peminjaman, id_barang, id_user, jumlah, tanggal_kembali, status, keterangan_admin) VALUES
(@pinjam_siti_1, @gitar_id, @siti_id, 1, '2025-11-13', 'disetujui', 'Pengembalian diterima, mohon lebih tepat waktu ya!');
SET @kembali_siti_1 = LAST_INSERT_ID();

-- Denda: 6 hari x Rp 5.000 = Rp 30.000 (BELUM BAYAR)
INSERT INTO denda (id_peminjaman, id_user, jumlah_denda, hari_telat, tanggal_hitung, status_bayar, keterangan) VALUES
(@pinjam_siti_1, @siti_id, 30000, 6, '2025-11-13', 'belum_bayar', 'Pengembalian telat 6 hari dari tanggal 7 Nov');

-- Update stok barang (dikembalikan)
UPDATE barang SET jumlah_tersedia = jumlah_tersedia + 1 WHERE id_barang = @gitar_id;

-- ============================================================
-- 7. PEMINJAMAN - SKENARIO AHMAD (Bersih, Normal)
-- ============================================================
-- Ahmad tidak punya masalah apapun, bisa pinjam dengan lancar
-- Tujuan: Test flow normal peminjaman

SET @ahmad_id = (SELECT id_user FROM users WHERE nim = '2101003');
SET @bola_id = (SELECT id_barang FROM barang WHERE nama_barang = 'Bola Futsal');

-- Peminjaman pending (menunggu approval admin)
INSERT INTO peminjaman (id_user, id_barang, jumlah, tanggal_pinjam, tanggal_jatuh_tempo, keterangan, status, bukti_validasi) VALUES
(@ahmad_id, @bola_id, 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Untuk turnamen futsal antar fakultas', 'pending', 'ktm_ahmad_001.jpg');

-- ============================================================
-- 8. PEMINJAMAN - SKENARIO RINA (Pengembalian Ditolak)
-- ============================================================
-- Rina mengembalikan barang tapi admin tolak dengan keterangan
-- Tujuan: Test fitur keterangan admin yang baru dibuat

SET @rina_id = (SELECT id_user FROM users WHERE nim = '2101004');
SET @kostum_id = (SELECT id_barang FROM barang WHERE nama_barang = 'Kostum Tari Tradisional');

-- Peminjaman 1: Kostum (disetujui, dalam proses pengembalian)
INSERT INTO peminjaman (id_user, id_barang, jumlah, tanggal_pinjam, tanggal_jatuh_tempo, keterangan, status, bukti_validasi) VALUES
(@rina_id, @kostum_id, 1, '2025-11-15', '2025-11-20', 'Untuk pentas seni kampus', 'disetujui', 'ktm_rina_001.jpg');
SET @pinjam_rina_1 = LAST_INSERT_ID();

-- Pengembalian: DITOLAK dengan keterangan admin
INSERT INTO pengembalian (id_peminjaman, id_barang, id_user, jumlah, tanggal_kembali, status, keterangan_admin) VALUES
(@pinjam_rina_1, @kostum_id, @rina_id, 1, '2025-11-21', 'ditolak', 
'Kostum yang kamu kembalikan kotor dan ada robekan di bagian lengan. Tolong bersihkan dan perbaiki dulu, lalu jumpai saya di ruang UKM untuk pengecekan ulang. Terima kasih!');

-- Update stok (belum dikembalikan karena ditolak)
UPDATE barang SET jumlah_tersedia = jumlah_tersedia - 1 WHERE id_barang = @kostum_id;

-- Peminjaman 2: Rina juga punya peminjaman yang sukses dikembalikan (untuk kontras)
SET @keyboard_id = (SELECT id_barang FROM barang WHERE nama_barang = 'Keyboard Casio');

INSERT INTO peminjaman (id_user, id_barang, jumlah, tanggal_pinjam, tanggal_jatuh_tempo, keterangan, status, bukti_validasi) VALUES
(@rina_id, @keyboard_id, 1, '2025-10-10', '2025-10-15', 'Untuk latihan paduan suara', 'disetujui', 'ktm_rina_002.jpg');
SET @pinjam_rina_2 = LAST_INSERT_ID();

-- Pengembalian: DISETUJUI dengan keterangan positif
INSERT INTO pengembalian (id_peminjaman, id_barang, id_user, jumlah, tanggal_kembali, status, keterangan_admin) VALUES
(@pinjam_rina_2, @keyboard_id, @rina_id, 1, '2025-10-14', 'disetujui', 
'Terima kasih sudah mengembalikan keyboard dalam kondisi bersih dan tepat waktu! Pertahankan ya 👍');

-- ============================================================
-- 9. PEMINJAMAN TAMBAHAN - Request Pengembalian Pending
-- ============================================================
-- Request pengembalian yang belum diproses admin (untuk test approval)

SET @laptop_id = (SELECT id_barang FROM barang WHERE nama_barang = 'Laptop Asus ROG');

INSERT INTO peminjaman (id_user, id_barang, jumlah, tanggal_pinjam, tanggal_jatuh_tempo, keterangan, status, bukti_validasi) VALUES
(@ahmad_id, @laptop_id, 1, '2025-11-25', '2025-11-30', 'Untuk editing video UKM', 'disetujui', 'ktm_ahmad_002.jpg');
SET @pinjam_ahmad_2 = LAST_INSERT_ID();

-- Request pengembalian (status: proses)
INSERT INTO pengembalian (id_peminjaman, id_barang, id_user, jumlah, tanggal_kembali, status, keterangan_admin) VALUES
(@pinjam_ahmad_2, @laptop_id, @ahmad_id, 1, CURDATE(), 'proses', NULL);

UPDATE barang SET jumlah_tersedia = jumlah_tersedia - 1 WHERE id_barang = @laptop_id;

-- ============================================================
-- 10. DENDA TAMBAHAN (Variasi Status)
-- ============================================================

-- Budi punya denda yang sudah lunas (untuk test riwayat pembayaran)
SET @raket_id = (SELECT id_barang FROM barang WHERE nama_barang = 'Raket Badminton');

INSERT INTO peminjaman (id_user, id_barang, jumlah, tanggal_pinjam, tanggal_jatuh_tempo, keterangan, status, bukti_validasi) VALUES
(@budi_id, @raket_id, 1, '2025-10-01', '2025-10-05', 'Untuk turnamen badminton', 'disetujui', 'ktm_budi_003.jpg');
SET @pinjam_budi_old = LAST_INSERT_ID();

INSERT INTO pengembalian (id_peminjaman, id_barang, id_user, jumlah, tanggal_kembali, status, keterangan_admin) VALUES
(@pinjam_budi_old, @raket_id, @budi_id, 1, '2025-10-08', 'disetujui', 'Dikembalikan dengan baik');

-- Denda 3 hari telat = Rp 15.000 (LUNAS)
INSERT INTO denda (id_peminjaman, id_user, jumlah_denda, hari_telat, tanggal_hitung, status_bayar, tanggal_bayar, keterangan) VALUES
(@pinjam_budi_old, @budi_id, 15000, 3, '2025-10-08', 'lunas', '2025-10-10', 'Denda telah dibayar lunas');

-- ============================================================
-- 11. LOG AKTIVITAS (Optional - untuk tracking)
-- ============================================================

INSERT INTO log_aktivitas (id_user, aktivitas, tanggal) VALUES
(@budi_id, 'Meminjam Kamera DSLR Canon (1 unit)', '2025-11-20'),
(@budi_id, 'Meminjam Microphone Wireless (2 unit)', '2025-11-22'),
(@siti_id, 'Mengembalikan Gitar Akustik (telat 6 hari)', '2025-11-13'),
(@rina_id, 'Pengembalian Kostum Tari ditolak oleh admin', '2025-11-21'),
(@rina_id, 'Pengembalian Keyboard Casio disetujui', '2025-10-14'),
(@ahmad_id, 'Mengajukan peminjaman Bola Futsal (pending)', CURDATE()),
(@ahmad_id, 'Mengajukan pengembalian Laptop Asus ROG', CURDATE()),
(@budi_id, 'Membayar denda Rp 15.000', '2025-10-10');

-- ============================================================
-- SUMMARY & TESTING GUIDE
-- ============================================================

SELECT '============================================' AS '';
SELECT 'DATA DUMMY BERHASIL DIMASUKKAN!' AS 'STATUS';
SELECT '============================================' AS '';
SELECT '' AS '';

SELECT '📋 AKUN UNTUK TESTING:' AS '';
SELECT '' AS '';
SELECT 'ADMIN:' AS '';
SELECT '  Username: (cek di database users role=admin)' AS '';
SELECT '  Password: admin123' AS '';
SELECT '' AS '';
SELECT 'PEMINJAM 1 - BUDI (2 Peminjaman Aktif):' AS '';
SELECT '  NIM: 2101001' AS '';
SELECT '  Password: password123' AS '';
SELECT '  Test: Coba pinjam barang lagi → Akan diblokir!' AS '';
SELECT '' AS '';
SELECT 'PEMINJAM 2 - SITI (Punya Denda Belum Bayar):' AS '';
SELECT '  NIM: 2101002' AS '';
SELECT '  Password: password123' AS '';
SELECT '  Denda: Rp 30.000 (belum bayar)' AS '';
SELECT '  Test: Coba pinjam barang → Akan diblokir! Lihat dashboard denda' AS '';
SELECT '' AS '';
SELECT 'PEMINJAM 3 - AHMAD (Normal, Bersih):' AS '';
SELECT '  NIM: 2101003' AS '';
SELECT '  Password: password123' AS '';
SELECT '  Test: Bisa pinjam dengan lancar ✅' AS '';
SELECT '' AS '';
SELECT 'PEMINJAM 4 - RINA (Pengembalian Ditolak):' AS '';
SELECT '  NIM: 2101004' AS '';
SELECT '  Password: password123' AS '';
SELECT '  Test: Lihat riwayat → Ada pengembalian ditolak dengan keterangan admin!' AS '';
SELECT '        Klik tombol ℹ untuk lihat pesan dari admin' AS '';
SELECT '' AS '';

SELECT '🎯 FITUR YANG BISA DI-TEST:' AS '';
SELECT '' AS '';
SELECT '1. ✅ Login Admin & Peminjam' AS '';
SELECT '2. ✅ Dashboard Admin - Request Peminjaman (approve/reject)' AS '';
SELECT '3. ✅ Dashboard Admin - Request Pengembalian (approve/reject + keterangan)' AS '';
SELECT '4. ✅ Blocking Peminjaman (Budi: sudah 2 aktif, Siti: ada denda)' AS '';
SELECT '5. ✅ Dashboard Denda (Siti lihat dendanya)' AS '';
SELECT '6. ✅ Riwayat Peminjam (Rina lihat keterangan admin)' AS '';
SELECT '7. ✅ Detail Barang (coba pinjam dengan berbagai kondisi)' AS '';
SELECT '8. ✅ CRUD Barang (tambah, edit, hapus)' AS '';
SELECT '9. ✅ Manajemen Kategori' AS '';
SELECT '10. ✅ Log Aktivitas' AS '';
SELECT '' AS '';

SELECT '📊 STATISTIK DATA:' AS '';
SELECT CONCAT('Total Users: ', COUNT(*)) AS '' FROM users;
SELECT CONCAT('Total Barang: ', COUNT(*)) AS '' FROM barang;
SELECT CONCAT('Total Kategori: ', COUNT(*)) AS '' FROM kategori_barang;
SELECT CONCAT('Total Peminjaman: ', COUNT(*)) AS '' FROM peminjaman;
SELECT CONCAT('Total Pengembalian: ', COUNT(*)) AS '' FROM pengembalian;
SELECT CONCAT('Total Denda: ', COUNT(*)) AS '' FROM denda;
SELECT CONCAT('Denda Belum Bayar: ', COUNT(*)) AS '' FROM denda WHERE status_bayar = 'belum_bayar';
SELECT '' AS '';

SELECT '🔍 QUICK CHECK:' AS '';
SELECT 'Peminjaman Aktif Budi:' AS 'Info', COUNT(*) AS 'Jumlah' 
FROM peminjaman p 
WHERE p.id_user = @budi_id 
  AND p.status = 'disetujui' 
  AND NOT EXISTS (
    SELECT 1 FROM pengembalian pg 
    WHERE pg.id_peminjaman = p.id_peminjaman 
      AND pg.status = 'disetujui'
  );

SELECT 'Denda Belum Bayar Siti:' AS 'Info', 
       CONCAT('Rp ', FORMAT(SUM(jumlah_denda), 0)) AS 'Total'
FROM denda 
WHERE id_user = @siti_id AND status_bayar = 'belum_bayar';

SELECT 'Pengembalian Ditolak Rina:' AS 'Info', COUNT(*) AS 'Jumlah'
FROM pengembalian 
WHERE id_user = @rina_id AND status = 'ditolak';

SELECT '' AS '';
SELECT '============================================' AS '';
SELECT 'SELAMAT TESTING! 🚀' AS '';
SELECT '============================================' AS '';

COMMIT;
