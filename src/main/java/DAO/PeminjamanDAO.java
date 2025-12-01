/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;
import Database.DatabaseConnection; 
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author aldriknoel
 */
public class PeminjamanDAO {
    // Method untuk memproses pengajuan peminjaman
    public boolean ajukanPeminjaman(int idUser, int idBarang, int jumlah, String keterangan, java.io.File fileBukti) {
        Connection conn = null;
        PreparedStatement psInsert = null;

        // Query: Insert data peminjaman dengan status 'proses' (belum approved)
        // STOK TIDAK DIKURANGI DI SINI, tapi saat admin approve di PeminjamanService
        String sqlInsert = "INSERT INTO peminjaman (id_user, id_barang, jumlah, tanggal_pinjam, tanggal_jatuh_tempo, status, keterangan, bukti_validasi) " +
                           "VALUES (?, ?, ?, ?, ?, 'proses', ?, ?)";

        try {
            conn = DatabaseConnection.getConnection();

            // Insert Peminjaman
            psInsert = conn.prepareStatement(sqlInsert);
            psInsert.setInt(1, idUser);
            psInsert.setInt(2, idBarang);
            psInsert.setInt(3, jumlah);
            psInsert.setDate(4, Date.valueOf(LocalDate.now())); 
            psInsert.setDate(5, Date.valueOf(LocalDate.now().plusDays(7)));
            psInsert.setString(6, keterangan);

            // Set bukti validasi (file gambar/pdf)
            if (fileBukti != null && fileBukti.exists()) {
                try {
                    java.io.FileInputStream fis = new java.io.FileInputStream(fileBukti);
                    psInsert.setBinaryStream(7, fis, (int) fileBukti.length());
                } catch (java.io.FileNotFoundException ex) {
                    ex.printStackTrace();
                    psInsert.setNull(7, java.sql.Types.BLOB);
                }
            } else {
                psInsert.setNull(7, java.sql.Types.BLOB);
            }
            
            int rowsAffected = psInsert.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (psInsert != null) psInsert.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<Model.RequestData> getPeminjamanAktif(int idUser) {
        List<Model.RequestData> list = new ArrayList<>();
        String sql = "SELECT b.nama_barang, p.tanggal_jatuh_tempo, p.jumlah " +
                     "FROM peminjaman p " +
                     "JOIN barang b ON p.id_barang = b.id_barang " +
                     "WHERE p.id_user = ? AND p.status = 'disetujui'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Model.RequestData rd = new Model.RequestData();
                    rd.setNamaBarang(rs.getString("nama_barang"));
                    rd.setTanggalKembali(rs.getDate("tanggal_jatuh_tempo"));
                    list.add(rd);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    public int hitungTotalRiwayat(int idUser) {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM peminjaman WHERE id_user = ?";

        try (java.sql.Connection conn = Database.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUser);
            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
    
    // Method untuk mengambil riwayat peminjaman spesifik user
    public java.util.List<Model.RequestData> getRiwayatUser(int idUser) {
        java.util.List<Model.RequestData> list = new java.util.ArrayList<>();
        
        String sql = "SELECT p.id_peminjaman, b.nama_barang, p.tanggal_pinjam, p.tanggal_jatuh_tempo, p.status, p.jumlah, p.keterangan " +
                     "FROM peminjaman p " +
                     "JOIN barang b ON p.id_barang = b.id_barang " +
                     "WHERE p.id_user = ? " +
                     "ORDER BY p.tanggal_pinjam DESC";

        try (java.sql.Connection conn = Database.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idUser);
            
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Model.RequestData rd = new Model.RequestData();
                    rd.setIdPeminjaman(rs.getInt("id_peminjaman")); 
                    rd.setNamaBarang(rs.getString("nama_barang"));
                    rd.setTanggalPinjam(rs.getDate("tanggal_pinjam"));
                    rd.setTanggalKembali(rs.getDate("tanggal_jatuh_tempo"));
                    rd.setStatus(rs.getString("status"));
                    rd.setJumlah(rs.getInt("jumlah"));
                    rd.setKeterangan(rs.getString("keterangan"));
                    
                    list.add(rd);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean ajukanPengembalian(int idPeminjaman, java.io.File fileBuktiKembali, int jumlahKembali) {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement psCek = null;
        java.sql.PreparedStatement psUpdateLama = null;
        java.sql.PreparedStatement psInsertBaru = null;
        java.sql.PreparedStatement psInsertLog = null;
        java.sql.ResultSet rs = null;

        try {
            conn = Database.DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlCek = "SELECT jumlah, id_barang, id_user, tanggal_pinjam, tanggal_jatuh_tempo, keterangan, bukti_validasi FROM peminjaman WHERE id_peminjaman = ?";
            psCek = conn.prepareStatement(sqlCek);
            psCek.setInt(1, idPeminjaman);
            rs = psCek.executeQuery();

            if (!rs.next()) {
                throw new Exception("Data peminjaman ID " + idPeminjaman + " tidak ditemukan!");
            }

            int jumlahAwal = rs.getInt("jumlah");
            int idBarang = rs.getInt("id_barang");
            int idUser = rs.getInt("id_user");
            java.sql.Date tglPinjam = rs.getDate("tanggal_pinjam");
            java.sql.Date tglTempo = rs.getDate("tanggal_jatuh_tempo");
            String ket = rs.getString("keterangan");
            java.sql.Blob buktiLama = rs.getBlob("bukti_validasi"); // Ambil bukti lama

            int idTransaksiTarget = idPeminjaman; 

            if (jumlahKembali < jumlahAwal) {
               
                String sqlKurang = "UPDATE peminjaman SET jumlah = ? WHERE id_peminjaman = ?";
                psUpdateLama = conn.prepareStatement(sqlKurang);
                psUpdateLama.setInt(1, jumlahAwal - jumlahKembali);
                psUpdateLama.setInt(2, idPeminjaman);
                psUpdateLama.executeUpdate();

                // Insert new peminjaman record untuk yang dikembalikan dengan status 'proses'
                String sqlBaru = "INSERT INTO peminjaman (id_user, id_barang, jumlah, tanggal_pinjam, tanggal_jatuh_tempo, status, keterangan, bukti_validasi) " +
                                 "VALUES (?, ?, ?, ?, ?, 'proses', ?, ?)";
                
                psInsertBaru = conn.prepareStatement(sqlBaru, java.sql.Statement.RETURN_GENERATED_KEYS);
                psInsertBaru.setInt(1, idUser);
                psInsertBaru.setInt(2, idBarang);
                psInsertBaru.setInt(3, jumlahKembali);
                psInsertBaru.setDate(4, tglPinjam);
                psInsertBaru.setDate(5, tglTempo);
                psInsertBaru.setString(6, ket);
                psInsertBaru.setBlob(7, buktiLama);
                
                psInsertBaru.executeUpdate();

                java.sql.ResultSet rsKey = psInsertBaru.getGeneratedKeys();
                if (rsKey.next()) {
                    idTransaksiTarget = rsKey.getInt(1); 
                }

            } else {
                // Full return - set status ke 'proses' untuk menandai sedang dalam proses pengembalian
                String sqlUpdate = "UPDATE peminjaman SET status = 'proses' WHERE id_peminjaman = ?";
                psUpdateLama = conn.prepareStatement(sqlUpdate);
                psUpdateLama.setInt(1, idPeminjaman);
                psUpdateLama.executeUpdate();
            }

            // 2. CATAT KE TABEL PENGEMBALIAN
            String sqlLog = "INSERT INTO pengembalian (id_peminjaman, id_barang, id_user, jumlah, tanggal_kembali, status, bukti_kembali) " +
                            "VALUES (?, ?, ?, ?, CURRENT_DATE, 'proses', ?)";
            
            psInsertLog = conn.prepareStatement(sqlLog);
            psInsertLog.setInt(1, idTransaksiTarget);
            psInsertLog.setInt(2, idBarang);
            psInsertLog.setInt(3, idUser);
            psInsertLog.setInt(4, jumlahKembali);
            
            if (fileBuktiKembali != null) {
                java.io.FileInputStream fis = new java.io.FileInputStream(fileBuktiKembali);
                psInsertLog.setBinaryStream(5, fis, (int) fileBuktiKembali.length());
            } else {
                psInsertLog.setNull(5, java.sql.Types.BLOB);
            }
            
            psInsertLog.executeUpdate();

            conn.commit(); 
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            e.printStackTrace(); 
            javax.swing.JOptionPane.showMessageDialog(null, "Error Database: " + e.getMessage());
            
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (psCek != null) psCek.close(); } catch (Exception e) {}
            try { if (psUpdateLama != null) psUpdateLama.close(); } catch (Exception e) {}
            try { if (psInsertBaru != null) psInsertBaru.close(); } catch (Exception e) {}
            try { if (psInsertLog != null) psInsertLog.close(); } catch (Exception e) {}
        }
    }
}
