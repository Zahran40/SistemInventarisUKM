-- Migration: Add bukti_validasi column to peminjaman table
-- Run this in your MySQL database

USE sistem_inventaris_ukm;

-- Add bukti_validasi column to store file upload (image/pdf)
ALTER TABLE `peminjaman` 
ADD COLUMN `bukti_validasi` LONGBLOB NULL AFTER `keterangan`;

-- Verify the change
DESCRIBE peminjaman;
