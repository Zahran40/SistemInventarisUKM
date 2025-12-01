-- ========================================
-- SIMPLE DATABASE MIGRATION SCRIPT
-- Sistem Inventaris UKM
-- Run this in phpMyAdmin SQL tab
-- ========================================

USE sistem_inventaris_ukm;

-- 1. Add bukti_validasi column to peminjaman
-- (Skip this if you already added it)
ALTER TABLE `peminjaman` 
ADD COLUMN `bukti_validasi` LONGBLOB NULL;

-- 2. Update barang status enum to include 'dipinjam'
-- THIS IS THE MOST IMPORTANT ONE!
ALTER TABLE `barang` 
MODIFY COLUMN `status` ENUM('tersedia','tidak tersedia','dipinjam') 
NOT NULL DEFAULT 'tersedia';

-- 3. Add bukti_kembali column to pengembalian
-- (Skip this if you already added it)
ALTER TABLE `pengembalian` 
ADD COLUMN `bukti_kembali` LONGBLOB NULL;

-- Done! Now test your application
