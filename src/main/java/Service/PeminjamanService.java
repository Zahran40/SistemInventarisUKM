package Service;

import DAO.BarangDAO;
import DAO.PeminjamanDAO;
import Database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Service class untuk menangani business logic peminjaman
 * Mengikuti prinsip Separation of Concerns
 */
public class PeminjamanService {
    
    private final BarangDAO barangDAO;
    private final PeminjamanDAO peminjamanDAO;
    
    public PeminjamanService() {
        this.barangDAO = new BarangDAO();
        this.peminjamanDAO = new PeminjamanDAO();
    }
    
    /**
     * Approve peminjaman dan update stok barang
     * @param idPeminjaman ID peminjaman yang disetujui
     * @param idBarang ID barang yang dipinjam
     * @param jumlah Jumlah yang dipinjam
     * @return true jika berhasil
     */
    public boolean approvePeminjamanDanUpdateStok(int idPeminjaman, int idBarang, int jumlah) {
        Connection conn = null;
        PreparedStatement psUpdatePeminjaman = null;
        PreparedStatement psUpdateStok = null;
        PreparedStatement psUpdateStatus = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // 1. Update status peminjaman jadi 'disetujui'
            String sqlPeminjaman = "UPDATE peminjaman SET status = 'disetujui' WHERE id_peminjaman = ?";
            psUpdatePeminjaman = conn.prepareStatement(sqlPeminjaman);
            psUpdatePeminjaman.setInt(1, idPeminjaman);
            psUpdatePeminjaman.executeUpdate();
            
            // 2. Kurangi stok barang
            String sqlStok = "UPDATE barang SET stok = stok - ? WHERE id_barang = ?";
            psUpdateStok = conn.prepareStatement(sqlStok);
            psUpdateStok.setInt(1, jumlah);
            psUpdateStok.setInt(2, idBarang);
            psUpdateStok.executeUpdate();
            
            // 3. Cek apakah stok jadi 0, kalau ya ubah status jadi 'dipinjam'
            String sqlUpdateStatus = 
                "UPDATE barang SET status = CASE " +
                "WHEN stok = 0 THEN 'dipinjam' " +
                "WHEN stok > 0 THEN 'tersedia' " +
                "END WHERE id_barang = ?";
            psUpdateStatus = conn.prepareStatement(sqlUpdateStatus);
            psUpdateStatus.setInt(1, idBarang);
            psUpdateStatus.executeUpdate();
            
            conn.commit(); // Commit transaction
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback jika ada error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
            
        } finally {
            try {
                if (psUpdatePeminjaman != null) psUpdatePeminjaman.close();
                if (psUpdateStok != null) psUpdateStok.close();
                if (psUpdateStatus != null) psUpdateStatus.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Reject peminjaman
     * @param idPeminjaman ID peminjaman yang ditolak
     * @return true jika berhasil
     */
    public boolean rejectPeminjaman(int idPeminjaman) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE peminjaman SET status = 'ditolak' WHERE id_peminjaman = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idPeminjaman);
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
            
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
