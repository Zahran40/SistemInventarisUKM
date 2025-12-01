-- Cek apakah tabel denda sudah ada
USE sistem_inventaris_ukm;
SHOW TABLES LIKE 'denda';

-- Jika tidak ada, buat tabel denda
CREATE TABLE IF NOT EXISTS `denda` (
  `id_denda` INT NOT NULL AUTO_INCREMENT,
  `id_peminjaman` INT NOT NULL,
  `id_user` INT NOT NULL,
  `jumlah_denda` INT NOT NULL COMMENT 'Total denda dalam Rupiah',
  `hari_telat` INT NOT NULL COMMENT 'Jumlah hari keterlambatan',
  `tanggal_hitung` DATE NOT NULL COMMENT 'Tanggal denda dihitung',
  `status_bayar` ENUM('belum_bayar','lunas') NOT NULL DEFAULT 'belum_bayar',
  `tanggal_bayar` DATE NULL,
  `keterangan` VARCHAR(255) NULL,
  PRIMARY KEY (`id_denda`),
  KEY `id_peminjaman` (`id_peminjaman`),
  KEY `id_user` (`id_user`),
  CONSTRAINT `denda_ibfk_1` FOREIGN KEY (`id_peminjaman`) REFERENCES `peminjaman` (`id_peminjaman`),
  CONSTRAINT `denda_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
