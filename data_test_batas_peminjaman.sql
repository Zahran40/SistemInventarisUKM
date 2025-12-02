-- Data Dummy untuk Testing Batas Maksimal 2 Peminjaman Aktif
-- Database: sistem_inventaris_ukm
-- User: Budi Prasetyo (id=2)

USE sistem_inventaris_ukm;

-- ============================================================
-- SCENARIO: User sudah punya 2 peminjaman aktif (disetujui, belum dikembalikan)
-- Saat coba pinjam lagi -> DIBLOKIR
-- ============================================================

-- Peminjaman Aktif 1: Bola Voli (sudah disetujui, belum dikembalikan)
INSERT INTO `peminjaman` (`id_user`, `id_barang`, `jumlah`, `tanggal_pinjam`, `tanggal_jatuh_tempo`, `status`, `keterangan`) VALUES
(2, 1, 2, '2025-11-25', '2025-12-05', 'disetujui', 'Peminjaman aktif 1 - Bola Voli untuk latihan');

-- Peminjaman Aktif 2: Raket Badminton (sudah disetujui, belum dikembalikan)
INSERT INTO `peminjaman` (`id_user`, `id_barang`, `jumlah`, `tanggal_pinjam`, `tanggal_jatuh_tempo`, `status`, `keterangan`) VALUES
(2, 5, 1, '2025-11-26', '2025-12-06', 'disetujui', 'Peminjaman aktif 2 - Raket Badminton');

-- Peminjaman Lama (sudah dikembalikan) - TIDAK DIHITUNG
INSERT INTO `peminjaman` (`id_user`, `id_barang`, `jumlah`, `tanggal_pinjam`, `tanggal_jatuh_tempo`, `status`, `keterangan`) VALUES
(2, 3, 3, '2025-11-10', '2025-11-20', 'disetujui', 'Peminjaman lama - Sudah dikembalikan');

-- Ambil ID peminjaman terakhir untuk pengembalian
SET @id_peminjaman_lama = LAST_INSERT_ID();

-- Data pengembalian untuk peminjaman lama (sudah disetujui dikembalikan)
INSERT INTO `pengembalian` (`id_peminjaman`, `id_barang`, `id_user`, `jumlah`, `tanggal_kembali`, `status`) VALUES
(@id_peminjaman_lama, 3, 2, 3, '2025-11-21', 'disetujui');

-- ============================================================
-- RINGKASAN
-- ============================================================

SELECT '============================================' AS '';
SELECT 'DATA TEST BATAS PEMINJAMAN BERHASIL!' AS 'STATUS';
SELECT '============================================' AS '';
SELECT '' AS '';
SELECT 'RINGKASAN:' AS '';
SELECT '2 Peminjaman AKTIF (belum dikembalikan)' AS 'Peminjaman Aktif';
SELECT '1 Peminjaman SELESAI (sudah dikembalikan)' AS 'Peminjaman Lama';
SELECT 'Total: 3 peminjaman' AS 'Total';
SELECT '' AS '';
SELECT '============================================' AS '';
SELECT 'CARA TEST:' AS '';
SELECT '1. Login sebagai Budi Prasetyo (NIM: 2101001)' AS '';
SELECT '2. Pilih barang dan klik "Pinjam Barang"' AS '';
SELECT '3. Sistem akan BLOKIR dengan pesan:' AS '';
SELECT '   "Anda sudah punya 2 peminjaman aktif"' AS '';
SELECT '4. Untuk bisa pinjam lagi, harus kembalikan' AS '';
SELECT '   salah satu dari 2 barang yang dipinjam' AS '';
SELECT '============================================' AS '';

COMMIT;
