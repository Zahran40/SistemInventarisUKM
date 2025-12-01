-- CHECK CURRENT BARANG TABLE STRUCTURE
SHOW CREATE TABLE barang;

-- CHECK CURRENT DATA
SELECT id_barang, nama_barang, stok, status FROM barang LIMIT 10;

-- TRY UPDATE ONE ROW TO TEST
-- UPDATE barang SET status = 'tidak tersedia' WHERE id_barang = 1;
