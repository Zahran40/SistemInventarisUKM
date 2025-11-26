-- Data Dummy untuk Sistem Inventaris UKM
-- Database: sistem_inventaris_ukm

USE sistem_inventaris_ukm;

-- Data untuk tabel kategori_barang
INSERT INTO `kategori_barang` (`id_kategori`, `nama_kategori`) VALUES
(1, 'Bola'),
(2, 'Raket & Net'),
(3, 'Matras & Alas'),
(4, 'Pelindung'),
(5, 'Perlengkapan Futsal'),
(6, 'Perlengkapan Basket'),
(7, 'Perlengkapan Atletik');

-- Data untuk tabel barang
INSERT INTO `barang` (`id_barang`, `nama_barang`, `id_kategori`, `stok`, `status`) VALUES
(1, 'Bola Voli Mikasa MVA200', 1, 10, 'tersedia'),
(2, 'Bola Basket Molten GG7X', 1, 8, 'tersedia'),
(3, 'Bola Futsal Specs Radiance', 1, 12, 'tersedia'),
(4, 'Bola Sepak Adidas UCL', 1, 6, 'tersedia'),
(5, 'Raket Badminton Yonex', 2, 15, 'tersedia'),
(6, 'Net Voli Standar Kompetisi', 2, 2, 'tersedia'),
(7, 'Net Badminton', 2, 3, 'tersedia'),
(8, 'Matras Yoga/Senam', 3, 20, 'tersedia'),
(9, 'Matras Taekwondo', 3, 8, 'tersedia'),
(10, 'Cone Latihan', 3, 30, 'tersedia'),
(11, 'Pelindung Lutut', 4, 15, 'tersedia'),
(12, 'Pelindung Siku', 4, 15, 'tersedia'),
(13, 'Rompi Latihan', 4, 20, 'tersedia'),
(14, 'Sepatu Futsal Specs', 5, 10, 'tersedia'),
(15, 'Gawang Futsal Mini', 5, 2, 'tersedia'),
(16, 'Jersey Latihan Set', 6, 25, 'tersedia'),
(17, 'Ring Basket Portable', 6, 1, 'tersedia'),
(18, 'Stopwatch Digital', 7, 5, 'tersedia'),
(19, 'Peluit Wasit', 7, 8, 'tersedia'),
(20, 'Skipping Rope', 7, 25, 'tersedia');

-- Data untuk tabel users
-- Password untuk semua user adalah: password123
INSERT INTO `users` (`id_user`, `nama_user`, `NIM`, `password`, `kontak`, `email`, `role`) VALUES
(1, 'Ketua UKM Olahraga', 'ADM001', 'password123', '081234567890', 'admin.olahraga@ukm.ac.id', 'admin'),
(2, 'Budi Prasetyo', '2101001', 'password123', '081234567891', 'budi.prasetyo@student.ac.id', 'peminjam');

-- Selesai
SELECT 'Data dummy berhasil ditambahkan!' AS message;
