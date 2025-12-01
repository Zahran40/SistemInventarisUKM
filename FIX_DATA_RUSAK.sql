-- =====================================================
-- FIX DATA RUSAK - Peminjaman yang Ditolak Setelah Pengembalian
-- =====================================================
USE sistem_inventaris_ukm;

-- 1. CEK DATA YANG RUSAK
-- Cari peminjaman yang statusnya 'ditolak' tapi sudah ada pengembaliannya
SELECT 
    p.id_peminjaman,
    p.id_barang,
    b.nama_barang,
    p.jumlah,
    p.status as status_peminjaman,
    pg.status as status_pengembalian,
    p.tanggal_pinjam,
    pg.tanggal_kembali
FROM peminjaman p
LEFT JOIN pengembalian pg ON p.id_peminjaman = pg.id_peminjaman
JOIN barang b ON p.id_barang = b.id_barang
WHERE p.status = 'ditolak' 
  AND pg.id_pengembalian IS NOT NULL;

-- Jika query di atas menampilkan data, artinya ada bug!
-- Peminjaman yang sudah dikembalikan seharusnya statusnya 'disetujui', bukan 'ditolak'

-- =====================================================
-- 2. FIX: Update status peminjaman yang salah
-- =====================================================
-- Kembalikan status peminjaman yang punya pengembalian jadi 'disetujui'
UPDATE peminjaman p
JOIN pengembalian pg ON p.id_peminjaman = pg.id_peminjaman
SET p.status = 'disetujui'
WHERE p.status = 'ditolak'
  AND pg.id_pengembalian IS NOT NULL;

-- =====================================================
-- 3. FIX: Perbaiki stok barang yang double-dikembalikan
-- =====================================================
-- Cek stok barang yang mungkin jadi lebih besar dari seharusnya
SELECT 
    b.id_barang,
    b.nama_barang,
    b.stok as stok_sekarang,
    SUM(CASE WHEN p.status = 'disetujui' THEN p.jumlah ELSE 0 END) as sedang_dipinjam,
    SUM(CASE WHEN pg.status = 'selesai' THEN pg.jumlah ELSE 0 END) as sudah_dikembalikan
FROM barang b
LEFT JOIN peminjaman p ON b.id_barang = p.id_barang
LEFT JOIN pengembalian pg ON b.id_barang = pg.id_barang
WHERE b.nama_barang = 'Bola Voli'  -- Sesuaikan dengan nama barang yang bermasalah
GROUP BY b.id_barang, b.nama_barang, b.stok;

-- =====================================================
-- 4. MANUAL FIX untuk Bola Voli (contoh)
-- =====================================================
-- Jika stok Bola Voli seharusnya 10, tapi sekarang jadi 13 karena double return:
-- UPDATE barang SET stok = 10 WHERE nama_barang = 'Bola Voli';

-- Uncomment dan sesuaikan nilai yang benar:
-- UPDATE barang SET stok = NILAI_YANG_BENAR WHERE id_barang = ID_BARANG;

-- =====================================================
-- 5. VERIFIKASI
-- =====================================================
SELECT 
    'Peminjaman' as tabel,
    COUNT(*) as jumlah_record,
    status
FROM peminjaman
GROUP BY status

UNION ALL

SELECT 
    'Pengembalian' as tabel,
    COUNT(*) as jumlah_record,
    status
FROM pengembalian
GROUP BY status;

-- Cek stok semua barang
SELECT id_barang, nama_barang, stok, status FROM barang ORDER BY nama_barang;
