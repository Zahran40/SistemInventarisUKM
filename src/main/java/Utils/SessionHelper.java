package Utils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Register.LoginPage;

/**
 * Utility class untuk session authentication
 */
public class SessionHelper {
    
    /**
     * Check apakah user sudah login, jika belum redirect ke login
     * @param frame Frame yang akan di-dispose jika belum login
     * @return true jika sudah login, false jika belum
     */
    public static boolean checkLogin(JFrame frame) {
        UserSession session = UserSession.getInstance();
        
        if (!session.isLoggedIn()) {
            JOptionPane.showMessageDialog(null, 
                "Anda harus login terlebih dahulu!", 
                "Akses Ditolak", 
                JOptionPane.WARNING_MESSAGE);
            new LoginPage().setVisible(true);
            if (frame != null) {
                frame.dispose();
            }
            return false;
        }
        return true;
    }
    
    /**
     * Check apakah user adalah admin
     * @param frame Frame yang akan di-dispose jika bukan admin
     * @return true jika admin, false jika bukan
     */
    public static boolean checkAdmin(JFrame frame) {
        if (!checkLogin(frame)) {
            return false;
        }
        
        UserSession session = UserSession.getInstance();
        if (!session.isAdmin()) {
            JOptionPane.showMessageDialog(null, 
                "Akses ditolak! Halaman ini hanya untuk Admin.", 
                "Akses Ditolak", 
                JOptionPane.ERROR_MESSAGE);
            new LoginPage().setVisible(true);
            if (frame != null) {
                frame.dispose();
            }
            return false;
        }
        return true;
    }
    
    /**
     * Check apakah user adalah peminjam
     * @param frame Frame yang akan di-dispose jika bukan peminjam
     * @return true jika peminjam, false jika bukan
     */
    public static boolean checkPeminjam(JFrame frame) {
        if (!checkLogin(frame)) {
            return false;
        }
        
        UserSession session = UserSession.getInstance();
        if (!session.isPeminjam()) {
            JOptionPane.showMessageDialog(null, 
                "Akses ditolak! Halaman ini hanya untuk Peminjam.", 
                "Akses Ditolak", 
                JOptionPane.ERROR_MESSAGE);
            new LoginPage().setVisible(true);
            if (frame != null) {
                frame.dispose();
            }
            return false;
        }
        return true;
    }
}
