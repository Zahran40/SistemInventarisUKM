-- Migration: Add bukti_kembali column to pengembalian table
-- Run this in your MySQL database

USE sistem_inventaris_ukm;

-- Add bukti_kembali column to store return proof image
ALTER TABLE `pengembalian` 
ADD COLUMN `bukti_kembali` LONGBLOB NULL AFTER `status`;

-- Verify the change
DESCRIBE pengembalian;
