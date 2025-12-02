-- Data Dummy untuk Testing Sistem Denda
-- Database: sistem_inventaris_ukm
-- Pastikan sudah ada data di tabel users, barang, dan peminjaman

USE sistem_inventaris_ukm;

-- ============================================================
-- STEP 1: Buat beberapa peminjaman yang sudah jatuh tempo
-- ============================================================

-- Peminjaman untuk testing denda (id_user=2, Budi Prasetyo)
-- Menggunakan ID 100+ untuk menghindari konflik dengan data existing
INSERT INTO `peminjaman` (`id_user`, `id_barang`, `jumlah`, `tanggal_pinjam`, `tanggal_jatuh_tempo`, `status`, `keterangan`) VALUES
(2, 1, 2, '2025-11-15', '2025-11-25', 'disetujui', 'Peminjaman bola voli untuk latihan'),
(2, 5, 1, '2025-11-10', '2025-11-20', 'disetujui', 'Peminjaman raket badminton'),
(2, 3, 3, '2025-11-12', '2025-11-27', 'disetujui', 'Peminjaman bola futsal untuk turnamen'),
(2, 8, 2, '2025-11-18', '2025-11-28', 'disetujui', 'Peminjaman matras yoga'),
(2, 11, 1, '2025-11-20', '2025-11-30', 'disetujui', 'Peminjaman pelindung lutut');

-- Ambil ID peminjaman yang baru saja dibuat
SET @pinjam1 = LAST_INSERT_ID();
SET @pinjam2 = @pinjam1 + 1;
SET @pinjam3 = @pinjam1 + 2;
SET @pinjam4 = @pinjam1 + 3;
SET @pinjam5 = @pinjam1 + 4;

-- ============================================================
-- STEP 2: Insert data denda berdasarkan keterlambatan
-- ============================================================

-- Denda 1: Peminjaman 1 - Terlambat 6 hari (jatuh tempo: 2025-11-25, sekarang: 2025-12-01)
-- Denda: 6 hari × Rp 5.000 = Rp 30.000
INSERT INTO `denda` (`id_peminjaman`, `id_user`, `jumlah_denda`, `hari_telat`, `tanggal_hitung`, `status_bayar`, `keterangan`) VALUES
(@pinjam1, 2, 30000, 6, '2025-12-01', 'belum_bayar', 'Terlambat 6 hari mengembalikan bola voli');

-- Denda 2: Peminjaman 2 - Terlambat 11 hari (jatuh tempo: 2025-11-20, sekarang: 2025-12-01)
-- Denda: 11 hari × Rp 5.000 = Rp 55.000
INSERT INTO `denda` (`id_peminjaman`, `id_user`, `jumlah_denda`, `hari_telat`, `tanggal_hitung`, `status_bayar`, `keterangan`) VALUES
(@pinjam2, 2, 55000, 11, '2025-12-01', 'belum_bayar', 'Terlambat 11 hari mengembalikan raket badminton');

-- Denda 3: Peminjaman 3 - Terlambat 4 hari (jatuh tempo: 2025-11-27, sekarang: 2025-12-01)
-- Denda: 4 hari × Rp 5.000 = Rp 20.000 (SUDAH LUNAS)
INSERT INTO `denda` (`id_peminjaman`, `id_user`, `jumlah_denda`, `hari_telat`, `tanggal_hitung`, `status_bayar`, `tanggal_bayar`, `keterangan`) VALUES
(@pinjam3, 2, 20000, 4, '2025-11-30', 'lunas', '2025-12-01', 'Terlambat 4 hari mengembalikan bola futsal - Sudah dibayar');

-- Denda 4: Peminjaman 4 - Terlambat 3 hari (jatuh tempo: 2025-11-28, sekarang: 2025-12-01)
-- Denda: 3 hari × Rp 5.000 = Rp 15.000
INSERT INTO `denda` (`id_peminjaman`, `id_user`, `jumlah_denda`, `hari_telat`, `tanggal_hitung`, `status_bayar`, `keterangan`) VALUES
(@pinjam4, 2, 15000, 3, '2025-12-01', 'belum_bayar', 'Terlambat 3 hari mengembalikan matras yoga');

-- Denda 5: Peminjaman 5 - Terlambat 1 hari (jatuh tempo: 2025-11-30, sekarang: 2025-12-01)
-- Denda: 1 hari × Rp 5.000 = Rp 5.000
INSERT INTO `denda` (`id_peminjaman`, `id_user`, `jumlah_denda`, `hari_telat`, `tanggal_hitung`, `status_bayar`, `keterangan`) VALUES
(@pinjam5, 2, 5000, 1, '2025-12-01', 'belum_bayar', 'Terlambat 1 hari mengembalikan pelindung lutut');

-- ============================================================
-- RINGKASAN DATA DENDA
-- ============================================================

-- Total Denda Belum Bayar untuk user id=2 (Budi Prasetyo):
-- Rp 30.000 + Rp 55.000 + Rp 15.000 + Rp 5.000 = Rp 105.000

-- Total Denda Lunas:
-- Rp 20.000

-- Total Keseluruhan Denda:
-- Rp 125.000

SELECT '============================================' AS '';
SELECT 'DATA DUMMY DENDA BERHASIL DITAMBAHKAN!' AS 'STATUS';
SELECT '============================================' AS '';
SELECT '' AS '';
SELECT 'RINGKASAN:' AS '';
SELECT '5 Peminjaman dengan status disetujui' AS 'Peminjaman';
SELECT '5 Data denda (4 belum bayar, 1 lunas)' AS 'Denda';
SELECT 'Total Denda Belum Bayar: Rp 105.000' AS 'Total';
SELECT 'User Testing: Budi Prasetyo (id=2)' AS 'User';
SELECT '' AS '';
SELECT '============================================' AS '';
SELECT 'LOGIN SEBAGAI PEMINJAM:' AS 'CARA TEST';
SELECT 'NIM: 2101001' AS '';
SELECT 'Password: password123' AS '';
SELECT 'Klik tombol "Denda Saya" di Dashboard' AS '';
SELECT '============================================' AS '';

COMMIT;
