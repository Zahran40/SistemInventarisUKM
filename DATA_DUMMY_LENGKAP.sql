-- =============================================
-- DATA DUMMY LENGKAP SISTEM INVENTARIS UKM
-- =============================================
-- File ini berisi data dummy lengkap untuk menguji semua fitur sistem
-- Termasuk: Users, Barang, Peminjaman, Pengembalian, Denda, Log Aktivitas
-- =============================================

-- Pastikan database sudah ada
USE `sistem_inventaris_ukm`;

-- =============================================
-- 1. CLEAR DATA LAMA (Optional - hati-hati!)
-- =============================================
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE log_aktivitas;
-- TRUNCATE TABLE denda;
-- TRUNCATE TABLE pengembalian;
-- TRUNCATE TABLE peminjaman;
-- TRUNCATE TABLE users;
-- SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 2. DATA USERS (Admin & Peminjam)
-- =============================================
DELETE FROM users;
INSERT INTO `users` (`id_user`, `nama_user`, `NIM`, `password`, `kontak`, `email`, `role`) VALUES
-- Admin
(1, 'Ketua UKM Olahraga', 'admin', 'admin123', '081234567890', 'admin.ukm@ukm.ac.id', 'admin'),

-- Peminjam - Berbagai Skenario
(2, 'Budi Santoso', '2101001', 'password123', '081234567891', 'budi.santoso@student.ac.id', 'peminjam'),
(3, 'Siti Nurhaliza', '2101002', 'password123', '081234567892', 'siti.nur@student.ac.id', 'peminjam'),
(4, 'Andi Wijaya', '2101003', 'password123', '081234567893', 'andi.wijaya@student.ac.id', 'peminjam'),
(5, 'Dewi Lestari', '2101004', 'password123', '081234567894', 'dewi.lestari@student.ac.id', 'peminjam'),
(6, 'Rudi Hermawan', '2101005', 'password123', '081234567895', 'rudi.hermawan@student.ac.id', 'peminjam');

-- =============================================
-- 3. DATA PEMINJAMAN (Berbagai Status)
-- =============================================
DELETE FROM peminjaman;
ALTER TABLE peminjaman AUTO_INCREMENT = 1;

INSERT INTO `peminjaman` (`id_user`, `id_barang`, `jumlah`, `tanggal_pinjam`, `tanggal_kembali`, `status`, `bukti_validasi`) VALUES
-- SKENARIO 1: PROSES (Menunggu Persetujuan Admin) - 3 peminjaman
(2, 1, 2, '2025-01-15', '2025-01-20', 'proses', NULL),  -- Budi pinjam Bola Basket (2 buah)
(3, 5, 1, '2025-01-15', '2025-01-22', 'proses', NULL),  -- Siti pinjam Net Badminton
(4, 10, 3, '2025-01-15', '2025-01-18', 'proses', NULL), -- Andi pinjam Cone Latihan (3 buah)

-- SKENARIO 2: DISETUJUI - BELUM DIKEMBALIKAN (Sedang Dipinjam) - 4 peminjaman
(2, 3, 1, '2025-01-10', '2025-01-17', 'disetujui', NULL), -- Budi pinjam Net Voli (masih dipinjam)
(3, 7, 2, '2025-01-12', '2025-01-19', 'disetujui', NULL), -- Siti pinjam Raket Badminton (masih dipinjam)
(5, 12, 1, '2025-01-08', '2025-01-15', 'disetujui', NULL), -- Dewi pinjam Matras Yoga (TERLAMBAT - akan ada denda!)
(6, 15, 1, '2025-01-05', '2025-01-12', 'disetujui', NULL), -- Rudi pinjam Peluit (TERLAMBAT - akan ada denda!)

-- SKENARIO 3: DISETUJUI - SUDAH DIKEMBALIKAN TEPAT WAKTU (Tidak Ada Denda) - 2 peminjaman
(4, 2, 1, '2025-01-01', '2025-01-08', 'disetujui', NULL), -- Andi pinjam Bola Sepak (sudah dikembalikan)
(5, 6, 2, '2025-01-03', '2025-01-10', 'disetujui', NULL), -- Dewi pinjam Shuttlecock (sudah dikembalikan)

-- SKENARIO 4: DISETUJUI - DIKEMBALIKAN TERLAMBAT (Ada Denda yang Sudah Lunas) - 1 peminjaman
(6, 8, 1, '2024-12-20', '2024-12-27', 'disetujui', NULL), -- Rudi pinjam Bola Tenis (terlambat 5 hari, denda lunas)

-- SKENARIO 5: DISETUJUI - DIKEMBALIKAN TERLAMBAT (Ada Denda Belum Bayar dengan FREEZE) - 1 peminjaman
(2, 21, 2, '2024-12-15', '2024-12-22', 'disetujui', NULL), -- Budi pinjam Bola Voli (terlambat, denda freeze)

-- SKENARIO 6: DITOLAK - 2 peminjaman
(3, 13, 1, '2025-01-14', '2025-01-21', 'ditolak', NULL), -- Siti pinjam Raket Tenis (ditolak)
(4, 18, 1, '2025-01-14', '2025-01-21', 'ditolak', NULL); -- Andi pinjam Tongkat Estafet (ditolak)

-- =============================================
-- 4. DATA PENGEMBALIAN (Berbagai Status & Skenario)
-- =============================================
DELETE FROM pengembalian;
ALTER TABLE pengembalian AUTO_INCREMENT = 1;

INSERT INTO `pengembalian` (`id_peminjaman`, `id_barang`, `id_user`, `jumlah`, `tanggal_kembali`, `status`, `keterangan_admin`, `tanggal_ditolak`, `bukti_kembali`) VALUES
-- PENGEMBALIAN untuk SKENARIO 3: DISETUJUI - Dikembalikan Tepat Waktu (Tidak Ada Denda)
(9, 2, 4, 1, '2025-01-07', 'disetujui', 'Barang dikembalikan dalam kondisi baik', NULL, NULL),  -- Andi kembalikan Bola Sepak
(10, 6, 5, 2, '2025-01-09', 'disetujui', 'Barang lengkap dan dalam kondisi baik', NULL, NULL), -- Dewi kembalikan Shuttlecock

-- PENGEMBALIAN untuk SKENARIO 4: DISETUJUI - Dikembalikan Terlambat (Denda Lunas)
(11, 8, 6, 1, '2025-01-02', 'disetujui', 'Terlambat 5 hari, denda sudah dibayar', NULL, NULL), -- Rudi kembalikan Bola Tenis (terlambat 5 hari, 2025-01-02 - 2024-12-27 = 6 hari, denda = 6 * 5000 = 30000)

-- PENGEMBALIAN untuk SKENARIO 5: DITOLAK - Denda FREEZE (Ini yang penting untuk testing!)
-- Peminjaman ID 12: Budi pinjam Bola Voli, deadline 2024-12-22
-- Pengembalian 1: DITOLAK pada 2025-01-05 (terlambat 14 hari sejak deadline 2024-12-22)
(12, 21, 2, 2, '2025-01-05', 'ditolak', 'Bola voli kempes, harus dipompa ulang terlebih dahulu', '2025-01-05', NULL),

-- Pengembalian 2: PROSES - Ajukan Ulang (Menunggu persetujuan admin)
-- User mengajukan ulang setelah memperbaiki kondisi barang
(12, 21, 2, 2, '2025-01-10', 'proses', NULL, NULL, NULL),

-- PENGEMBALIAN PROSES - Menunggu Persetujuan Admin (untuk testing)
(5, 1, 2, 2, '2025-01-16', 'proses', NULL, NULL, NULL), -- Budi ajukan pengembalian Bola Basket (status masih proses)
(6, 5, 3, 1, '2025-01-16', 'proses', NULL, NULL, NULL); -- Siti ajukan pengembalian Net Badminton (status masih proses)

-- =============================================
-- 5. DATA DENDA (Berbagai Kondisi)
-- =============================================
DELETE FROM denda;
ALTER TABLE denda AUTO_INCREMENT = 1;

INSERT INTO `denda` (`id_peminjaman`, `id_user`, `hari_terlambat`, `jumlah_denda`, `status`, `keterangan`, `tanggal_bayar`) VALUES
-- DENDA LUNAS (Sudah Dibayar)
(11, 6, 6, 30000, 'lunas', 'Terlambat 6 hari mengembalikan Bola Tenis', '2025-01-03'), -- Rudi bayar denda Rp 30.000 (6 hari × Rp 5.000)

-- DENDA FREEZE (Pengembalian Ditolak, Denda Berhenti di Tanggal Penolakan)
-- Peminjaman ID 12: deadline 2024-12-22, ditolak 2025-01-05 (14 hari terlambat)
-- Denda freeze = 14 hari × Rp 5.000 = Rp 70.000
(12, 2, 14, 70000, 'belum_bayar', 'Denda freeze karena pengembalian ditolak tanggal 2025-01-05. Bola voli kempes.', NULL);

-- CATATAN PENTING:
-- Denda untuk peminjaman ID 7 (Dewi - Matras Yoga) dan ID 8 (Rudi - Peluit) akan dihitung REAL-TIME
-- karena belum ada pengembalian, sehingga jumlah denda akan bertambah setiap hari
-- sesuai dengan rumus: DATEDIFF(CURDATE(), tanggal_kembali) × 5000

-- Data untuk tabel kategori_barang
INSERT INTO `kategori_barang` (`id_kategori`, `nama_kategori`) VALUES
(1, 'Bola'),
(2, 'Raket & Net'),
(3, 'Matras & Alas'),
(4, 'Pelindung'),
(5, 'Perlengkapan Futsal'),
(6, 'Perlengkapan Basket'),
(7, 'Perlengkapan Atletik');

-- Data untuk tabel barang
INSERT INTO `barang` (`id_barang`, `nama_barang`, `id_kategori`, `stok`, `status`) VALUES
(1, 'Bola Voli Mikasa MVA200', 1, 10, 'tersedia'),
(2, 'Bola Basket Molten GG7X', 1, 8, 'tersedia'),
(3, 'Bola Futsal Specs Radiance', 1, 12, 'tersedia'),
(4, 'Bola Sepak Adidas UCL', 1, 6, 'tersedia'),
(5, 'Raket Badminton Yonex', 2, 15, 'tersedia'),
(6, 'Net Voli Standar Kompetisi', 2, 2, 'tersedia'),
(7, 'Net Badminton', 2, 3, 'tersedia'),
(8, 'Matras Yoga/Senam', 3, 20, 'tersedia'),
(9, 'Matras Taekwondo', 3, 8, 'tersedia'),
(10, 'Cone Latihan', 3, 30, 'tersedia'),
(11, 'Pelindung Lutut', 4, 15, 'tersedia'),
(12, 'Pelindung Siku', 4, 15, 'tersedia'),
(13, 'Rompi Latihan', 4, 20, 'tersedia'),
(14, 'Sepatu Futsal Specs', 5, 10, 'tersedia'),
(15, 'Gawang Futsal Mini', 5, 2, 'tersedia'),
(16, 'Jersey Latihan Set', 6, 25, 'tersedia'),
(17, 'Ring Basket Portable', 6, 1, 'tersedia'),
(18, 'Stopwatch Digital', 7, 5, 'tersedia'),
(19, 'Peluit Wasit', 7, 8, 'tersedia'),
(20, 'Skipping Rope', 7, 25, 'tersedia');

-- =============================================
-- 6. DATA LOG AKTIVITAS (History Sistem)
-- =============================================
DELETE FROM log_aktivitas;
ALTER TABLE log_aktivitas AUTO_INCREMENT = 1;

INSERT INTO `log_aktivitas` (`id_user`, `aktivitas`, `timestamp`) VALUES
-- Login Admin
(1, 'Login sebagai admin', '2025-01-15 08:00:00'),

-- Aktivitas Peminjaman
(2, 'Mengajukan peminjaman Bola Basket (2 buah)', '2025-01-15 09:00:00'),
(3, 'Mengajukan peminjaman Net Badminton', '2025-01-15 09:15:00'),
(4, 'Mengajukan peminjaman Cone Latihan (3 buah)', '2025-01-15 09:30:00'),

-- Approval Peminjaman oleh Admin
(1, 'Menyetujui peminjaman Bola Sepak oleh Andi Wijaya', '2025-01-01 10:00:00'),
(1, 'Menyetujui peminjaman Shuttlecock oleh Dewi Lestari', '2025-01-03 10:00:00'),
(1, 'Menyetujui peminjaman Net Voli oleh Budi Santoso', '2025-01-10 10:00:00'),
(1, 'Menyetujui peminjaman Raket Badminton oleh Siti Nurhaliza', '2025-01-12 10:00:00'),

-- Aktivitas Pengembalian
(4, 'Mengajukan pengembalian Bola Sepak', '2025-01-07 14:00:00'),
(5, 'Mengajukan pengembalian Shuttlecock', '2025-01-09 14:00:00'),
(6, 'Mengajukan pengembalian Bola Tenis (terlambat)', '2025-01-02 14:00:00'),

-- Approval Pengembalian oleh Admin
(1, 'Menyetujui pengembalian Bola Sepak oleh Andi Wijaya', '2025-01-07 15:00:00'),
(1, 'Menyetujui pengembalian Shuttlecock oleh Dewi Lestari', '2025-01-09 15:00:00'),
(1, 'Menyetujui pengembalian Bola Tenis oleh Rudi Hermawan (denda Rp 30.000)', '2025-01-02 15:00:00'),

-- Penolakan Pengembalian (Denda Freeze)
(2, 'Mengajukan pengembalian Bola Voli', '2025-01-05 14:00:00'),
(1, 'Menolak pengembalian Bola Voli oleh Budi Santoso - Alasan: Bola kempes', '2025-01-05 15:00:00'),

-- Pengajuan Ulang Pengembalian
(2, 'Mengajukan ulang pengembalian Bola Voli setelah dipompa', '2025-01-10 14:00:00'),

-- Pembayaran Denda
(6, 'Membayar denda Rp 30.000 untuk keterlambatan Bola Tenis', '2025-01-03 16:00:00'),
(1, 'Mengonfirmasi pembayaran denda Rp 30.000 dari Rudi Hermawan', '2025-01-03 16:05:00');

-- =============================================
-- 7. VERIFIKASI DATA
-- =============================================
-- Tampilkan summary data yang telah diinsert

SELECT 'SUMMARY DATA DUMMY' AS Info;

SELECT '--- USERS ---' AS Info;
SELECT role, COUNT(*) AS jumlah FROM users GROUP BY role;

SELECT '--- PEMINJAMAN (by Status) ---' AS Info;
SELECT status, COUNT(*) AS jumlah FROM peminjaman GROUP BY status;

SELECT '--- PENGEMBALIAN (by Status) ---' AS Info;
SELECT status, COUNT(*) AS jumlah FROM pengembalian GROUP BY status;

SELECT '--- DENDA (by Status) ---' AS Info;
SELECT status, COUNT(*) AS jumlah, SUM(jumlah_denda) AS total_rupiah FROM denda GROUP BY status;

SELECT '--- LOG AKTIVITAS ---' AS Info;
SELECT COUNT(*) AS total_aktivitas FROM log_aktivitas;

-- =============================================
-- 8. QUERY TESTING UNTUK FITUR-FITUR PENTING
-- =============================================

-- Cek Denda Real-Time (untuk barang yang belum dikembalikan dan terlambat)
SELECT 
    'DENDA REAL-TIME (Belum Dikembalikan, Terlambat)' AS Info;
SELECT 
    p.id_peminjaman,
    u.nama_user,
    b.nama_barang,
    p.tanggal_kembali AS deadline,
    DATEDIFF(CURDATE(), p.tanggal_kembali) AS hari_terlambat,
    DATEDIFF(CURDATE(), p.tanggal_kembali) * 5000 AS denda_realtime
FROM peminjaman p
JOIN users u ON p.id_user = u.id_user
JOIN barang b ON p.id_barang = b.id_barang
WHERE p.status = 'disetujui'
  AND p.tanggal_kembali < CURDATE()
  AND NOT EXISTS (
      SELECT 1 FROM pengembalian pg 
      WHERE pg.id_peminjaman = p.id_peminjaman 
        AND pg.status = 'disetujui'
  );

-- Cek Denda Freeze (pengembalian ditolak)
SELECT 
    'DENDA FREEZE (Pengembalian Ditolak)' AS Info;
SELECT 
    d.id_denda,
    u.nama_user,
    b.nama_barang,
    d.hari_terlambat,
    d.jumlah_denda,
    d.keterangan
FROM denda d
JOIN users u ON d.id_user = u.id_user
JOIN peminjaman p ON d.id_peminjaman = p.id_peminjaman
JOIN barang b ON p.id_barang = b.id_barang
WHERE d.status = 'belum_bayar'
  AND d.keterangan LIKE '%freeze%';

-- Cek Peminjaman dengan Pengembalian Ditolak (untuk tombol Ajukan Ulang)
SELECT 
    'PEMINJAMAN DENGAN PENGEMBALIAN DITOLAK (Ajukan Ulang)' AS Info;
SELECT 
    p.id_peminjaman,
    u.nama_user,
    b.nama_barang,
    pg.tanggal_kembali AS tanggal_ditolak,
    pg.keterangan_admin AS alasan_ditolak
FROM peminjaman p
JOIN users u ON p.id_user = u.id_user
JOIN barang b ON p.id_barang = b.id_barang
JOIN pengembalian pg ON p.id_peminjaman = pg.id_peminjaman
WHERE p.status = 'disetujui'
  AND pg.status = 'ditolak';

-- =============================================
-- SELESAI
-- =============================================
SELECT 'DATA DUMMY BERHASIL DI-INSERT!' AS Status;
SELECT 'Silakan login dengan:' AS Info;
SELECT 'Admin: username = admin, password = admin123' AS Login_Admin;
SELECT 'Peminjam: username = 2101001, password = password123 (Budi Santoso)' AS Login_Peminjam1;
SELECT 'Peminjam: username = 2101002, password = password123 (Siti Nurhaliza)' AS Login_Peminjam2;
