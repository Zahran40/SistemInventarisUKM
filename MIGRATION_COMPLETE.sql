-- ========================================
-- COMPLETE DATABASE MIGRATION SCRIPT
-- Sistem Inventaris UKM
-- ========================================

USE sistem_inventaris_ukm;

-- ========================================
-- 1. ADD bukti_validasi column to peminjaman table
-- ========================================
-- Drop if exists, then add (to make script idempotent)
SET @sql1 = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE table_schema = 'sistem_inventaris_ukm' 
    AND table_name = 'peminjaman' 
    AND column_name = 'bukti_validasi') = 0,
    'ALTER TABLE `peminjaman` ADD COLUMN `bukti_validasi` LONGBLOB NULL COMMENT ''File upload bukti KTM/validasi peminjaman''',
    'SELECT ''Column bukti_validasi already exists'' AS message');
PREPARE stmt1 FROM @sql1;
EXECUTE stmt1;
DEALLOCATE PREPARE stmt1;

-- ========================================
-- 2. UPDATE barang status enum (ADD 'dipinjam' option)
-- ========================================
ALTER TABLE `barang` 
MODIFY COLUMN `status` ENUM('tersedia','tidak tersedia','dipinjam') 
NOT NULL DEFAULT 'tersedia';

-- ========================================
-- 3. ADD bukti_kembali column to pengembalian table
-- ========================================
-- Drop if exists, then add (to make script idempotent)
SET @sql2 = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE table_schema = 'sistem_inventaris_ukm' 
    AND table_name = 'pengembalian' 
    AND column_name = 'bukti_kembali') = 0,
    'ALTER TABLE `pengembalian` ADD COLUMN `bukti_kembali` LONGBLOB NULL COMMENT ''File upload bukti kondisi barang saat dikembalikan''',
    'SELECT ''Column bukti_kembali already exists'' AS message');
PREPARE stmt2 FROM @sql2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

-- ========================================
-- VERIFICATION - Check all changes
-- ========================================
SELECT 'Checking peminjaman table...' AS '';
DESCRIBE peminjaman;

SELECT 'Checking barang table...' AS '';
DESCRIBE barang;

SELECT 'Checking pengembalian table...' AS '';
DESCRIBE pengembalian;

SELECT 'Migration completed successfully!' AS '';
