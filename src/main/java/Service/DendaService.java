package Service;

import Database.DatabaseConnection;
import Model.Denda;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
     */
    public int getTotalDendaBelumBayar(int idUser) {
        String sql = "SELECT COALESCE(SUM(jumlah_denda), 0) as total " +
                     "FROM denda " +
                     "WHERE id_user = ? AND status_bayar = 'belum_bayar'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Check apakah user punya denda belum bayar
     */
    public boolean punyaDendaBelumBayar(int idUser) {
        return getTotalDendaBelumBayar(idUser) > 0;
    }
    
    /**
     * Get list denda untuk user
     */
    public List<Denda> getDendaByUser(int idUser) {
        List<Denda> list = new ArrayList<>();
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
        
        return list;
    }
    
    /**
     * Get all denda belum bayar (untuk admin)
     */
    public List<Denda> getAllDendaBelumBayar() {
        List<Denda> list = new ArrayList<>();
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
     */
    public int getTotalSemuaDendaBelumBayar() {
        String sql = "SELECT COALESCE(SUM(jumlah_denda), 0) as total " +
                     "FROM denda WHERE status_bayar = 'belum_bayar'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
}
