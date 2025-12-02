package Service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import Database.DatabaseConnection;
import Model.Denda;

/**
 * Service class untuk business logic Denda
 * Mengikuti prinsip Single Responsibility dan Separation of Concerns
 */
public class DendaService {
    
    private static final int DENDA_PER_HARI = 5000; // Rp 5.000 per hari
    
    /**
     * Hitung denda berdasarkan keterlambatan
     * @param idPeminjaman ID peminjaman
     * @param tanggalKembali Tanggal pengembalian barang (untuk menghitung denda yang tepat)
     * @return Denda object atau null jika tidak telat
     */
    public Denda hitungDenda(int idPeminjaman, Date tanggalKembali) {
        String sql = "SELECT p.id_peminjaman, p.id_user, p.tanggal_jatuh_tempo, p.status " +
                     "FROM peminjaman p " +
                     "WHERE p.id_peminjaman = ? AND p.status = 'disetujui'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPeminjaman);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Date tanggalJatuhTempo = rs.getDate("tanggal_jatuh_tempo");
                int idUser = rs.getInt("id_user");
                
                LocalDate jatuhTempo = tanggalJatuhTempo.toLocalDate();
                LocalDate tanggalDikembalikan = tanggalKembali.toLocalDate();
                
                // Hitung hari telat BERDASARKAN TANGGAL PENGEMBALIAN
                // Bukan tanggal hari ini, jadi denda tidak bertambah setelah dikembalikan
                long hariTelat = ChronoUnit.DAYS.between(jatuhTempo, tanggalDikembalikan);
                
                if (hariTelat > 0) {
                    int jumlahDenda = (int) hariTelat * DENDA_PER_HARI;
                    Denda denda = new Denda(idPeminjaman, idUser, jumlahDenda, (int) hariTelat);
                    denda.setKeterangan("Terlambat " + hariTelat + " hari (dikembalikan " + tanggalDikembalikan + ")");
                    return denda;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null; // Tidak telat atau tidak ditemukan
    }
    
    /**
     * Simpan denda ke database
     */
    public boolean simpanDenda(Denda denda) {
        String sql = "INSERT INTO denda (id_peminjaman, id_user, jumlah_denda, hari_telat, " +
                     "tanggal_hitung, status_bayar, keterangan) " +
                     "VALUES (?, ?, ?, ?, CURRENT_DATE, 'belum_bayar', ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, denda.getIdPeminjaman());
            ps.setInt(2, denda.getIdUser());
            ps.setInt(3, denda.getJumlahDenda());
            ps.setInt(4, denda.getHariTelat());
            ps.setString(5, denda.getKeterangan());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get total denda belum bayar untuk user tertentu
     * FIXED: Include denda real-time dari peminjaman aktif yang terlambat
     */
    public int getTotalDendaBelumBayar(int idUser) {
        int total = 0;
        
        // 1. Total dari tabel denda
        String sql = "SELECT COALESCE(SUM(jumlah_denda), 0) as total " +
                     "FROM denda " +
                     "WHERE id_user = ? AND status_bayar = 'belum_bayar'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // 2. TAMBAHKAN: Denda real-time dari peminjaman aktif yang terlambat
        // FIXED: Hitung denda freeze untuk yang ditolak, denda real-time untuk yang lainnya
        String sqlAktif = "SELECT COALESCE(SUM(" +
                         "  CASE " +
                         "    WHEN (SELECT peng.status FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman ORDER BY peng.id_pengembalian DESC LIMIT 1) = 'ditolak' " +
                         "    THEN DATEDIFF((SELECT peng.tanggal_kembali FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman AND peng.status = 'ditolak' ORDER BY peng.id_pengembalian DESC LIMIT 1), p.tanggal_jatuh_tempo) * " + DENDA_PER_HARI +
                         "    ELSE DATEDIFF(CURDATE(), p.tanggal_jatuh_tempo) * " + DENDA_PER_HARI +
                         "  END" +
                         "), 0) AS total_realtime " +
                         "FROM peminjaman p " +
                         "WHERE p.id_user = ? " +
                         "  AND p.status = 'disetujui' " +
                         "  AND DATEDIFF(CURDATE(), p.tanggal_jatuh_tempo) > 0 " +
                         "  AND NOT EXISTS (" +
                         "      SELECT 1 FROM pengembalian peng " +
                         "      WHERE peng.id_peminjaman = p.id_peminjaman " +
                         "        AND peng.status = 'disetujui'" +
                         "  ) " +
                         "  AND NOT EXISTS (" +
                         "      SELECT 1 FROM denda d " +
                         "      WHERE d.id_peminjaman = p.id_peminjaman" +
                         "  )";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlAktif)) {
            
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                total += rs.getInt("total_realtime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return total;
    }
    
    /**
     * Check apakah user punya denda belum bayar
     */
    public boolean punyaDendaBelumBayar(int idUser) {
        return getTotalDendaBelumBayar(idUser) > 0;
    }
    
    /**
     * Get list denda untuk user
     * FIXED: Gabungkan denda dari tabel + denda real-time dari peminjaman aktif yang terlambat
     */
    public List<Denda> getDendaByUser(int idUser) {
        List<Denda> list = new ArrayList<>();
        
        // 1. Ambil denda yang sudah tercatat di tabel denda
        String sql = "SELECT d.*, u.nama_user, b.nama_barang " +
                     "FROM denda d " +
                     "JOIN users u ON d.id_user = u.id_user " +
                     "JOIN peminjaman p ON d.id_peminjaman = p.id_peminjaman " +
                     "JOIN barang b ON p.id_barang = b.id_barang " +
                     "WHERE d.id_user = ? " +
                     "ORDER BY d.tanggal_hitung DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Denda denda = new Denda();
                denda.setIdDenda(rs.getInt("id_denda"));
                denda.setIdPeminjaman(rs.getInt("id_peminjaman"));
                denda.setIdUser(rs.getInt("id_user"));
                denda.setJumlahDenda(rs.getInt("jumlah_denda"));
                denda.setHariTelat(rs.getInt("hari_telat"));
                denda.setTanggalHitung(rs.getDate("tanggal_hitung"));
                denda.setStatusBayar(rs.getString("status_bayar"));
                denda.setTanggalBayar(rs.getDate("tanggal_bayar"));
                denda.setKeterangan(rs.getString("keterangan"));
                denda.setNamaUser(rs.getString("nama_user"));
                denda.setNamaBarang(rs.getString("nama_barang"));
                
                list.add(denda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // 2. TAMBAHKAN: Cek peminjaman aktif yang terlambat (belum dikembalikan)
        // FIXED: Tetap tampilkan meski ada request pengembalian yang masih 'proses'
        // FIXED: FREEZE denda jika pengembalian ditolak (denda tidak bertambah)
        String sqlAktif = "SELECT p.id_peminjaman, p.id_user, p.tanggal_jatuh_tempo, " +
                         "b.nama_barang, u.nama_user, " +
                         "(SELECT peng.status FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman ORDER BY peng.id_pengembalian DESC LIMIT 1) AS status_pengembalian, " +
                         "(SELECT peng.tanggal_kembali FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman AND peng.status = 'ditolak' ORDER BY peng.id_pengembalian DESC LIMIT 1) AS tanggal_ditolak, " +
                         "(SELECT peng.keterangan_admin FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman AND peng.status = 'ditolak' ORDER BY peng.id_pengembalian DESC LIMIT 1) AS alasan_ditolak, " +
                         "CASE " +
                         "  WHEN (SELECT peng.status FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman ORDER BY peng.id_pengembalian DESC LIMIT 1) = 'ditolak' " +
                         "  THEN DATEDIFF((SELECT peng.tanggal_kembali FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman AND peng.status = 'ditolak' ORDER BY peng.id_pengembalian DESC LIMIT 1), p.tanggal_jatuh_tempo) " +
                         "  ELSE DATEDIFF(CURDATE(), p.tanggal_jatuh_tempo) " +
                         "END AS hari_telat " +
                         "FROM peminjaman p " +
                         "JOIN barang b ON p.id_barang = b.id_barang " +
                         "JOIN users u ON p.id_user = u.id_user " +
                         "WHERE p.id_user = ? " +
                         "  AND p.status = 'disetujui' " +
                         "  AND DATEDIFF(CURDATE(), p.tanggal_jatuh_tempo) > 0 " +
                         "  AND NOT EXISTS (" +
                         "      SELECT 1 FROM pengembalian peng " +
                         "      WHERE peng.id_peminjaman = p.id_peminjaman " +
                         "        AND peng.status = 'disetujui'" +
                         "  ) " +
                         "  AND NOT EXISTS (" +
                         "      SELECT 1 FROM denda d " +
                         "      WHERE d.id_peminjaman = p.id_peminjaman" +
                         "  )";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlAktif)) {
            
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                int hariTelat = rs.getInt("hari_telat");
                int jumlahDenda = hariTelat * DENDA_PER_HARI;
                String statusPengembalian = rs.getString("status_pengembalian");
                String alasanDitolak = rs.getString("alasan_ditolak");
                Date tanggalDitolak = rs.getDate("tanggal_ditolak");
                
                Denda denda = new Denda();
                denda.setIdDenda(0); // Belum ada di tabel (virtual)
                denda.setIdPeminjaman(rs.getInt("id_peminjaman"));
                denda.setIdUser(rs.getInt("id_user"));
                denda.setJumlahDenda(jumlahDenda);
                denda.setHariTelat(hariTelat);
                denda.setTanggalHitung(new Date(System.currentTimeMillis()));
                denda.setStatusBayar("belum_bayar");
                
                // Tampilkan status pengembalian di keterangan
                String keterangan = "⚠️ TERLAMBAT " + hariTelat + " hari";
                if ("ditolak".equals(statusPengembalian)) {
                    keterangan += " (DENDA FREEZE - Pengembalian ditolak)";
                    if (alasanDitolak != null && !alasanDitolak.isEmpty()) {
                        keterangan += "\nAlasan: " + alasanDitolak;
                    }
                    keterangan += "\n➡️ Silakan ajukan pengembalian ulang";
                } else if ("proses".equals(statusPengembalian)) {
                    keterangan += " (REAL-TIME) - Menunggu approval admin";
                } else {
                    keterangan += " (REAL-TIME) - Belum dikembalikan";
                }
                denda.setKeterangan(keterangan);
                denda.setNamaUser(rs.getString("nama_user"));
                denda.setNamaBarang(rs.getString("nama_barang"));
                
                list.add(denda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Get all denda belum bayar (untuk admin)
     */
    /**
     * Get all denda belum bayar (untuk admin)
     * FIXED: Include denda real-time dari peminjaman yang terlambat
     */
    public List<Denda> getAllDendaBelumBayar() {
        List<Denda> list = new ArrayList<>();
        
        // 1. Ambil denda yang sudah tercatat di tabel denda
        String sql = "SELECT d.*, u.nama_user, b.nama_barang " +
                     "FROM denda d " +
                     "JOIN users u ON d.id_user = u.id_user " +
                     "JOIN peminjaman p ON d.id_peminjaman = p.id_peminjaman " +
                     "JOIN barang b ON p.id_barang = b.id_barang " +
                     "WHERE d.status_bayar = 'belum_bayar' " +
                     "ORDER BY d.tanggal_hitung DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Denda denda = new Denda();
                denda.setIdDenda(rs.getInt("id_denda"));
                denda.setIdPeminjaman(rs.getInt("id_peminjaman"));
                denda.setIdUser(rs.getInt("id_user"));
                denda.setJumlahDenda(rs.getInt("jumlah_denda"));
                denda.setHariTelat(rs.getInt("hari_telat"));
                denda.setTanggalHitung(rs.getDate("tanggal_hitung"));
                denda.setStatusBayar(rs.getString("status_bayar"));
                denda.setKeterangan(rs.getString("keterangan"));
                denda.setNamaUser(rs.getString("nama_user"));
                denda.setNamaBarang(rs.getString("nama_barang"));
                
                list.add(denda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // 2. TAMBAHKAN: Denda real-time dari peminjaman yang terlambat (UNTUK SEMUA USER)
        String sqlAktif = "SELECT p.id_peminjaman, p.id_user, p.tanggal_jatuh_tempo, " +
                         "b.nama_barang, u.nama_user, " +
                         "(SELECT peng.status FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman ORDER BY peng.id_pengembalian DESC LIMIT 1) AS status_pengembalian, " +
                         "(SELECT peng.tanggal_kembali FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman AND peng.status = 'ditolak' ORDER BY peng.id_pengembalian DESC LIMIT 1) AS tanggal_ditolak, " +
                         "(SELECT peng.keterangan_admin FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman AND peng.status = 'ditolak' ORDER BY peng.id_pengembalian DESC LIMIT 1) AS alasan_ditolak, " +
                         "CASE " +
                         "  WHEN (SELECT peng.status FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman ORDER BY peng.id_pengembalian DESC LIMIT 1) = 'ditolak' " +
                         "  THEN DATEDIFF((SELECT peng.tanggal_kembali FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman AND peng.status = 'ditolak' ORDER BY peng.id_pengembalian DESC LIMIT 1), p.tanggal_jatuh_tempo) " +
                         "  ELSE DATEDIFF(CURDATE(), p.tanggal_jatuh_tempo) " +
                         "END AS hari_telat " +
                         "FROM peminjaman p " +
                         "JOIN barang b ON p.id_barang = b.id_barang " +
                         "JOIN users u ON p.id_user = u.id_user " +
                         "WHERE p.status = 'disetujui' " +
                         "  AND DATEDIFF(CURDATE(), p.tanggal_jatuh_tempo) > 0 " +
                         "  AND NOT EXISTS (" +
                         "      SELECT 1 FROM pengembalian peng " +
                         "      WHERE peng.id_peminjaman = p.id_peminjaman " +
                         "        AND peng.status = 'disetujui'" +
                         "  ) " +
                         "  AND NOT EXISTS (" +
                         "      SELECT 1 FROM denda d " +
                         "      WHERE d.id_peminjaman = p.id_peminjaman" +
                         "  ) " +
                         "ORDER BY p.tanggal_jatuh_tempo ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlAktif);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int hariTelat = rs.getInt("hari_telat");
                int jumlahDenda = hariTelat * DENDA_PER_HARI;
                String statusPengembalian = rs.getString("status_pengembalian");
                String alasanDitolak = rs.getString("alasan_ditolak");
                Date tanggalDitolak = rs.getDate("tanggal_ditolak");
                
                Denda denda = new Denda();
                denda.setIdDenda(0); // Virtual (belum ada di tabel)
                denda.setIdPeminjaman(rs.getInt("id_peminjaman"));
                denda.setIdUser(rs.getInt("id_user"));
                denda.setJumlahDenda(jumlahDenda);
                denda.setHariTelat(hariTelat);
                denda.setTanggalHitung(new Date(System.currentTimeMillis()));
                denda.setStatusBayar("belum_bayar");
                
                // Keterangan
                String keterangan = "⚠️ TERLAMBAT " + hariTelat + " hari";
                if ("ditolak".equals(statusPengembalian)) {
                    keterangan += " (DENDA FREEZE - Pengembalian ditolak)";
                    if (alasanDitolak != null && !alasanDitolak.isEmpty()) {
                        keterangan += "\nAlasan: " + alasanDitolak;
                    }
                } else if ("proses".equals(statusPengembalian)) {
                    keterangan += " (REAL-TIME) - Menunggu approval admin";
                } else {
                    keterangan += " (REAL-TIME) - Belum dikembalikan";
                }
                denda.setKeterangan(keterangan);
                denda.setNamaUser(rs.getString("nama_user"));
                denda.setNamaBarang(rs.getString("nama_barang"));
                
                list.add(denda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Tandai denda sebagai lunas
     */
    public boolean bayarDenda(int idDenda) {
        String sql = "UPDATE denda SET status_bayar = 'lunas', tanggal_bayar = CURRENT_DATE " +
                     "WHERE id_denda = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idDenda);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get total semua denda belum bayar (untuk statistik admin)
     * FIXED: Include denda real-time
     */
    public int getTotalSemuaDendaBelumBayar() {
        int total = 0;
        
        // 1. Total dari tabel denda
        String sql = "SELECT COALESCE(SUM(jumlah_denda), 0) as total " +
                     "FROM denda WHERE status_bayar = 'belum_bayar'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // 2. TAMBAHKAN: Denda real-time dari semua peminjaman yang terlambat
        String sqlAktif = "SELECT COALESCE(SUM(" +
                         "  CASE " +
                         "    WHEN (SELECT peng.status FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman ORDER BY peng.id_pengembalian DESC LIMIT 1) = 'ditolak' " +
                         "    THEN DATEDIFF((SELECT peng.tanggal_kembali FROM pengembalian peng WHERE peng.id_peminjaman = p.id_peminjaman AND peng.status = 'ditolak' ORDER BY peng.id_pengembalian DESC LIMIT 1), p.tanggal_jatuh_tempo) * " + DENDA_PER_HARI +
                         "    ELSE DATEDIFF(CURDATE(), p.tanggal_jatuh_tempo) * " + DENDA_PER_HARI +
                         "  END" +
                         "), 0) AS total_realtime " +
                         "FROM peminjaman p " +
                         "WHERE p.status = 'disetujui' " +
                         "  AND DATEDIFF(CURDATE(), p.tanggal_jatuh_tempo) > 0 " +
                         "  AND NOT EXISTS (" +
                         "      SELECT 1 FROM pengembalian peng " +
                         "      WHERE peng.id_peminjaman = p.id_peminjaman " +
                         "        AND peng.status = 'disetujui'" +
                         "  ) " +
                         "  AND NOT EXISTS (" +
                         "      SELECT 1 FROM denda d " +
                         "      WHERE d.id_peminjaman = p.id_peminjaman" +
                         "  )";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlAktif);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                total += rs.getInt("total_realtime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return total;
    }
}
