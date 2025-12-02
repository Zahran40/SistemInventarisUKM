-- SQL Script untuk menambahkan kolom keterangan_admin di tabel pengembalian
-- Database: sistem_inventaris_ukm
-- Tujuan: Admin bisa memberikan keterangan saat menolak/menyetujui pengembalian

USE sistem_inventaris_ukm;

-- ============================================================
-- Tambah kolom keterangan_admin ke tabel pengembalian
-- ============================================================

ALTER TABLE `pengembalian` 
ADD COLUMN `keterangan_admin` TEXT NULL DEFAULT NULL 
AFTER `status`;

-- Penjelasan:
-- - keterangan_admin: Keterangan dari admin (opsional)
-- - TEXT: Bisa menampung keterangan panjang
-- - NULL: Boleh kosong (jika disetujui mungkin tidak perlu keterangan)
-- - DEFAULT NULL: Default value adalah NULL
-- - AFTER status: Ditaruh setelah kolom status

-- ============================================================
-- Contoh penggunaan:
-- ============================================================

-- UPDATE pengembalian 
-- SET status = 'ditolak', 
--     keterangan_admin = 'Barang yang kamu kembalikan rusak, jumpai saya di UKM'
-- WHERE id_pengembalian = 1;

-- UPDATE pengembalian 
-- SET status = 'disetujui', 
--     keterangan_admin = 'Pengembalian diterima dengan baik, terima kasih'
-- WHERE id_pengembalian = 2;

SELECT '============================================' AS '';
SELECT 'KOLOM KETERANGAN_ADMIN BERHASIL DITAMBAHKAN!' AS 'STATUS';
SELECT '============================================' AS '';
SELECT '' AS '';
SELECT 'STRUKTUR TABEL PENGEMBALIAN SEKARANG:' AS '';
SELECT '- id_pengembalian' AS 'Kolom';
SELECT '- id_peminjaman' AS '';
SELECT '- id_barang' AS '';
SELECT '- id_user' AS '';
SELECT '- jumlah' AS '';
SELECT '- tanggal_kembali' AS '';
SELECT '- status (proses/disetujui/ditolak)' AS '';
SELECT '- keterangan_admin (TEXT, NULL) ← BARU!' AS '';
SELECT '' AS '';
SELECT '============================================' AS '';

COMMIT;
