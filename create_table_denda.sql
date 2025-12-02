-- SQL Script untuk membuat tabel denda
-- Jalankan script ini di database sistem_inventaris_ukm
-- PENTING: Nama kolom disesuaikan dengan kode Java (DendaService.java)

-- --------------------------------------------------------

--
-- Table structure for table `denda`
--

CREATE TABLE `denda` (
  `id_denda` int NOT NULL AUTO_INCREMENT,
  `id_peminjaman` int NOT NULL,
  `id_user` int NOT NULL,
  `jumlah_denda` int NOT NULL DEFAULT 0,
  `hari_telat` int NOT NULL DEFAULT 0,
  `tanggal_hitung` date NOT NULL,
  `status_bayar` enum('belum_bayar','lunas') NOT NULL DEFAULT 'belum_bayar',
  `tanggal_bayar` date DEFAULT NULL,
  `keterangan` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_denda`),
  KEY `id_peminjaman` (`id_peminjaman`),
  KEY `id_user` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Add foreign key constraints
--

ALTER TABLE `denda`
  ADD CONSTRAINT `denda_ibfk_1` FOREIGN KEY (`id_peminjaman`) REFERENCES `peminjaman` (`id_peminjaman`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `denda_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Sample data (optional - hapus jika tidak diperlukan)
-- Contoh: denda untuk user_id=2, denda_per_hari=5000
--

-- INSERT INTO `denda` (`id_peminjaman`, `id_user`, `jumlah_denda`, `hari_telat`, `tanggal_hitung`, `status_bayar`, `keterangan`) 
-- VALUES
-- (1, 2, 10000, 2, '2025-11-28', 'belum_bayar', 'Terlambat 2 hari mengembalikan kamera'),
-- (2, 2, 15000, 3, '2025-11-29', 'lunas', 'Terlambat 3 hari mengembalikan laptop');

COMMIT;
