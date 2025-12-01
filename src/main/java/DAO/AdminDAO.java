/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Database.DatabaseConnection;
import Model.RequestData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aldriknoel
 */
public class AdminDAO {
    public java.util.Map<String, Integer> getDashboardStats() {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        
        // Default values
        stats.put("total_stok", 0);
        stats.put("total_jenis", 0);
        stats.put("tersedia", 0);
        stats.put("dipinjam", 0);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            // 1. Hitung Total Stok & Total Jenis
            String sql1 = "SELECT SUM(stok) as total_stok, COUNT(*) as total_jenis FROM barang";
            try (PreparedStatement ps = conn.prepareStatement(sql1); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("total_stok", rs.getInt("total_stok"));
                    stats.put("total_jenis", rs.getInt("total_jenis"));
                }
            }

            // 2. Hitung Barang Tersedia (Stok > 0)
            String sql2 = "SELECT COUNT(*) as tersedia FROM barang WHERE stok > 0 AND status = 'tersedia'";
            try (PreparedStatement ps = conn.prepareStatement(sql2); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("tersedia", rs.getInt("tersedia"));
                }
            }

            // 3. Hitung Barang Sedang Dipinjam (Status 'disetujui' atau 'proses')
            // Kita hitung SUM(jumlah) agar tahu berapa unit yang sedang keluar
            String sql3 = "SELECT SUM(jumlah) as dipinjam FROM peminjaman WHERE status IN ('disetujui', 'proses')";
            try (PreparedStatement ps = conn.prepareStatement(sql3); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("dipinjam", rs.getInt("dipinjam"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    
    // Request yang statusnya 'proses'
    public java.util.List<Model.RequestData> getPendingRequests() {
        java.util.List<Model.RequestData> list = new java.util.ArrayList<>();
        String sql = "SELECT p.id_peminjaman, p.id_barang, u.nama_user, b.nama_barang, " +
                     "p.jumlah, p.tanggal_pinjam, p.tanggal_jatuh_tempo, p.keterangan, p.bukti_validasi " +
                     "FROM peminjaman p " +
                     "JOIN users u ON p.id_user = u.id_user " +
                     "JOIN barang b ON p.id_barang = b.id_barang " +
                     "WHERE p.status = 'proses' " +
                     "ORDER BY p.tanggal_pinjam ASC";

        try (java.sql.Connection conn = Database.DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Model.RequestData rd = new Model.RequestData();
                rd.setIdPeminjaman(rs.getInt("id_peminjaman"));
                rd.setIdBarang(rs.getInt("id_barang"));
                rd.setNamaPeminjam(rs.getString("nama_user"));
                rd.setNamaBarang(rs.getString("nama_barang"));
                rd.setJumlah(rs.getInt("jumlah"));
                rd.setTanggalPinjam(rs.getDate("tanggal_pinjam"));
                rd.setTanggalKembali(rs.getDate("tanggal_jatuh_tempo"));
                rd.setKeterangan(rs.getString("keterangan"));
                
                Object blobBukti = rs.getObject("bukti_validasi");
                
                if (blobBukti != null) {
                    rd.setBuktiValidasi("Tersedia (Klik untuk Lihat)"); 
                } else {
                    rd.setBuktiValidasi("");
                }            
                list.add(rd);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // 2. Proses Approval (Terima / Tolak)
    public boolean prosesPeminjaman(int idPeminjaman, int idBarang, int jumlah, String keputusan) {
        Connection conn = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psStok = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (keputusan.equals("Setujui")) {
                String sqlApprove = "UPDATE peminjaman SET status = 'disetujui', " +
                                    "tanggal_pinjam = ?, tanggal_jatuh_tempo = ? " +
                                    "WHERE id_peminjaman = ?";
                
                psUpdate = conn.prepareStatement(sqlApprove);
                
                java.time.LocalDate today = java.time.LocalDate.now();
                java.time.LocalDate deadline = today.plusDays(3);
                
                psUpdate.setDate(1, java.sql.Date.valueOf(today));
                psUpdate.setDate(2, java.sql.Date.valueOf(deadline));
                psUpdate.setInt(3, idPeminjaman);
                
                psUpdate.executeUpdate();
                
            } else if (keputusan.equals("Tolak")) {
                String sqlTolak = "UPDATE peminjaman SET status = 'ditolak' WHERE id_peminjaman = ?";
                psUpdate = conn.prepareStatement(sqlTolak);
                psUpdate.setInt(1, idPeminjaman);
                psUpdate.executeUpdate();

                // Balikin Stok
                String sqlBalikStok = "UPDATE barang SET stok = stok + ? WHERE id_barang = ?";
                psStok = conn.prepareStatement(sqlBalikStok);
                psStok.setInt(1, jumlah);
                psStok.setInt(2, idBarang);
                psStok.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) {} }
            e.printStackTrace();
            return false;
        }
    }
    
    // Method untuk mengambil Log Peminjaman
    public List<Model.LogData> getLogPeminjaman() {
        List<Model.LogData> list = new ArrayList<>();
        
        // Mengambil data dari tabel peminjaman, di-join dengan user dan barang
        String sql = "SELECT u.nama_user, b.nama_barang, p.tanggal_pinjam, p.status " +
                     "FROM peminjaman p " +
                     "JOIN users u ON p.id_user = u.id_user " +
                     "JOIN barang b ON p.id_barang = b.id_barang " +
                     "ORDER BY p.tanggal_pinjam DESC"; // Urutkan dari yang terbaru

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Model.LogData log = new Model.LogData();
                log.setNamaPeminjam(rs.getString("nama_user"));
                log.setNamaBarang(rs.getString("nama_barang"));
                log.setTanggal(rs.getDate("tanggal_pinjam"));
                log.setStatus(rs.getString("status"));
                
                list.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Method untuk mengambil gambar dari DB dan menyimpannya sebagai file sementara
    public java.io.File getBuktiFisik(int idPeminjaman) {
        String sql = "SELECT bukti_validasi FROM peminjaman WHERE id_peminjaman = ?";
        java.io.File imageFile = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPeminjaman);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Ambil data BLOB
                    java.io.InputStream is = rs.getBinaryStream("bukti_validasi");
                    
                    if (is != null) {
                        // Buat file sementara di laptop Admin (misal: temp_bukti.jpg)
                        imageFile = new java.io.File("temp_bukti_" + idPeminjaman + ".jpg");
                        java.io.FileOutputStream fos = new java.io.FileOutputStream(imageFile);
                        
                        byte[] buffer = new byte[1024];
                        while (is.read(buffer) > 0) {
                            fos.write(buffer);
                        }
                        fos.close();
                        is.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageFile; 
    }
    
    // Ambil Data Pengembalian (Pending)
    public java.util.List<Model.RequestData> getPendingReturns() {
        java.util.List<Model.RequestData> list = new java.util.ArrayList<>();
        String sql = "SELECT pg.id_pengembalian, pg.id_peminjaman, pg.id_barang, u.nama_user, b.nama_barang, " +
                     "pg.jumlah, pg.tanggal_kembali, pg.bukti_kembali " +
                     "FROM pengembalian pg " +
                     "JOIN users u ON pg.id_user = u.id_user " +
                     "JOIN barang b ON pg.id_barang = b.id_barang " +
                     "WHERE pg.status = 'proses' " +
                     "ORDER BY pg.tanggal_kembali ASC";

        try (java.sql.Connection conn = Database.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Model.RequestData rd = new Model.RequestData();
                rd.setIdPeminjaman(rs.getInt("id_pengembalian")); 
                rd.setIdBarang(rs.getInt("id_barang"));
                rd.setNamaPeminjam(rs.getString("nama_user"));
                rd.setNamaBarang(rs.getString("nama_barang"));
                rd.setJumlah(rs.getInt("jumlah"));
                rd.setTanggalKembali(rs.getDate("tanggal_kembali"));
                Object blob = rs.getObject("bukti_kembali");
                if (blob != null) rd.setBuktiValidasi("Ada");
                else rd.setBuktiValidasi("");
                
                list.add(rd);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Proses Konfirmasi Pengembalian
    public boolean prosesPengembalian(int idPengembalian, int idBarang, int jumlah, String keputusan) {
        String statusAkhir = keputusan.equals("Setujui") ? "selesai" : "ditolak";
        String sqlUpdate = "UPDATE pengembalian SET status = ? WHERE id_pengembalian = ?";
        String sqlStok = "UPDATE barang SET stok = stok + ? WHERE id_barang = ?";
        String sqlStatus = "UPDATE barang SET status = CASE " +
                          "WHEN stok + ? > 0 THEN 'tersedia' " +
                          "ELSE 'tidak tersedia' " +
                          "END WHERE id_barang = ?";

        try (java.sql.Connection conn = Database.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // 1. Update status pengembalian
            java.sql.PreparedStatement ps = conn.prepareStatement(sqlUpdate);
            ps.setString(1, statusAkhir);
            ps.setInt(2, idPengembalian);
            ps.executeUpdate();
            ps.close();
            
            // 2. Jika disetujui, kembalikan stok dan update status barang
            if (keputusan.equals("Setujui")) {
                // Tambah stok
                java.sql.PreparedStatement ps2 = conn.prepareStatement(sqlStok);
                ps2.setInt(1, jumlah);
                ps2.setInt(2, idBarang);
                ps2.executeUpdate();
                ps2.close();
                
                // Update status barang jadi 'tersedia' karena stok bertambah
                java.sql.PreparedStatement ps3 = conn.prepareStatement(sqlStatus);
                ps3.setInt(1, jumlah);
                ps3.setInt(2, idBarang);
                ps3.executeUpdate();
                ps3.close();
            }
            
            conn.commit();
            return true;
        } catch (Exception e) { 
            e.printStackTrace();
            return false; 
        }
    }
    
    public java.io.File getBuktiPengembalian(int idPengembalian) {
        String sql = "SELECT bukti_kembali FROM pengembalian WHERE id_pengembalian = ?";
        java.io.File imageFile = null;

        try (java.sql.Connection conn = Database.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPengembalian);
            
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.io.InputStream is = rs.getBinaryStream("bukti_kembali");
                    
                    if (is != null) {
                        // Buat file sementara: bukti_kembali_101.jpg
                        imageFile = new java.io.File("bukti_kembali_" + idPengembalian + ".jpg");
                        java.io.FileOutputStream fos = new java.io.FileOutputStream(imageFile);
                        
                        byte[] buffer = new byte[1024];
                        while (is.read(buffer) > 0) {
                            fos.write(buffer);
                        }
                        fos.close();
                        is.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageFile;
    }
}
