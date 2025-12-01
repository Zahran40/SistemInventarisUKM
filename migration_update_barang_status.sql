-- Migration: Update barang status enum to include 'dipinjam'
-- Run this in your MySQL database

USE sistem_inventaris_ukm;

-- Update status enum to include 'dipinjam' option
ALTER TABLE `barang` 
MODIFY COLUMN `status` ENUM('tersedia','tidak tersedia','dipinjam') NOT NULL DEFAULT 'tersedia';

-- Verify the change
DESCRIBE barang;
