package Utils;

/**
 * Helper class untuk menambahkan fullscreen dan navigasi ke semua halaman
 */
public class NavigationHelper {
    
    /**
     * Set window to fullscreen
     */
    public static void setFullscreen(javax.swing.JFrame frame) {
        frame.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    }
    
    /**
     * Navigate to another page dengan dispose current frame
     */
    public static void navigateTo(javax.swing.JFrame currentFrame, javax.swing.JFrame targetFrame) {
        targetFrame.setVisible(true);
        currentFrame.dispose();
    }
    
    /**
     * Logout dan kembali ke login page
     */
    public static void logout(javax.swing.JFrame currentFrame) {
        UserSession.getInstance().clearSession();
        javax.swing.JOptionPane.showMessageDialog(currentFrame, "Berhasil logout!");
        new Register.LoginPage().setVisible(true);
        currentFrame.dispose();
    }
}
