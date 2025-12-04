/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Database.DatabaseConnection;
import Model.Barang;
/**
 *
 * @author aldriknoel
 */
public class BarangDAO {

    public List<Barang> getAllBarang() {
        List<Barang> listBarang = new ArrayList<>();
        
        String sql = "SELECT b.id_barang, b.nama_barang, k.nama_kategori, b.stok, b.status " +
                     "FROM barang b " +
                     "JOIN kategori_barang k ON b.id_kategori = k.id_kategori " +
                     "WHERE b.status = 'tersedia' " + 
                     "ORDER BY b.id_barang DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Barang b = new Barang();
                b.setId(rs.getInt("id_barang"));
                b.setNama(rs.getString("nama_barang"));
                b.setNamaKategori(rs.getString("nama_kategori"));
                b.setStok(rs.getInt("stok"));
                b.setStatus(rs.getString("status"));
                
                listBarang.add(b);
            }
        } catch (SQLException e) {
            System.out.println("Error ambil data barang: " + e.getMessage());
        }
        return listBarang;
    }
    
    // Method untuk mengambil detail satu barang berdasarkan ID
    public Barang getBarangById(int id) {
        Barang b = null;
        String sql = "SELECT b.id_barang, b.nama_barang, k.nama_kategori, b.stok, b.status " + // Tambahkan deskripsi jika ada di DB
                     "FROM barang b " +
                     "JOIN kategori_barang k ON b.id_kategori = k.id_kategori " +
                     "WHERE b.id_barang = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    b = new Barang();
                    b.setId(rs.getInt("id_barang"));
                    b.setNama(rs.getString("nama_barang"));
                    b.setNamaKategori(rs.getString("nama_kategori"));
                    b.setStok(rs.getInt("stok"));
                    b.setStatus(rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error ambil detail barang: " + e.getMessage());
        }
        return b;
    }
    
    // Method Search & Filter
    public List<Model.Barang> filterBarang(String keyword, String kategori) {
        List<Model.Barang> listBarang = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT b.id_barang, b.nama_barang, k.nama_kategori, b.stok, b.status " +
            "FROM barang b " +
            "JOIN kategori_barang k ON b.id_kategori = k.id_kategori " +
            "WHERE 1=1 ");

        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND b.nama_barang LIKE ? ");
        }

        // Filter Kategori (Jika bukan "Semua")
        if (kategori != null && !kategori.equalsIgnoreCase("Semua")) {
            sql.append("AND k.nama_kategori = ? ");
        }
        
        sql.append("ORDER BY b.id_barang DESC");

        try (Connection conn = Database.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            
            if (kategori != null && !kategori.equalsIgnoreCase("Semua")) {
                ps.setString(paramIndex++, kategori);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Model.Barang b = new Model.Barang();
                    b.setId(rs.getInt("id_barang"));
                    b.setNama(rs.getString("nama_barang"));
                    b.setNamaKategori(rs.getString("nama_kategori"));
                    b.setStok(rs.getInt("stok"));
                    b.setStatus(rs.getString("status"));
                    listBarang.add(b);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listBarang;
    }
    
    // Method untuk mengambil daftar nama kategori saja (untuk isi Combo Box)
    public List<String> getNamaKategori() {
        List<String> listKategori = new ArrayList<>();
        String sql = "SELECT nama_kategori FROM kategori_barang ORDER BY nama_kategori ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                listKategori.add(rs.getString("nama_kategori"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listKategori;
    }
    
    // Method untuk Update Barang
    public boolean updateBarang(int idBarang, String nama, int idKategori, int stok, String status) {
        // Query-nya juga harus update status
        String sql = "UPDATE barang SET nama_barang=?, id_kategori=?, stok=?, status=? WHERE id_barang=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nama);
            ps.setInt(2, idKategori);
            ps.setInt(3, stok);
            ps.setString(4, status); // Parameter ke-4 adalah status
            ps.setInt(5, idBarang);  // Parameter ke-5 adalah ID (WHERE clause)
            
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Ambil ID kategori berdasarkan Nama
    public int getKategoriId(String namaKategori) {
        int id = 0;
        String sql = "SELECT id_kategori FROM kategori_barang WHERE nama_kategori = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaKategori);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id_kategori");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    // Cek apakah nama barang sudah ada (untuk validasi tambah barang)
    public boolean isNamaBarangExists(String namaBarang) {
        String sql = "SELECT COUNT(*) FROM barang WHERE LOWER(nama_barang) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaBarang.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Cek apakah nama barang sudah dipakai barang lain (untuk validasi edit barang)
    public boolean isNamaBarangExistsForOther(String namaBarang, int idBarang) {
        String sql = "SELECT COUNT(*) FROM barang WHERE LOWER(nama_barang) = LOWER(?) AND id_barang != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaBarang.trim());
            ps.setInt(2, idBarang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Cek apakah barang sedang dipinjam (status peminjaman = 'dipinjam')
    public boolean isBarangSedangDipinjam(int idBarang) {
        String sql = "SELECT COUNT(*) FROM peminjaman WHERE id_barang = ? AND status = 'dipinjam'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBarang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tambah Barang Baru
    public boolean tambahBarang(String nama, int idKategori, int stok) {
        String sql = "INSERT INTO barang (nama_barang, id_kategori, stok, status) VALUES (?, ?, ?, 'tersedia')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nama);
            ps.setInt(2, idKategori);
            ps.setInt(3, stok);
            
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean hapusBarang(int idBarang) {
        
        String sql = "DELETE FROM barang WHERE id_barang = ?";
        try (java.sql.Connection conn = Database.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idBarang);
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


