package Utils;

/**
 * Class untuk mengelola session user yang sedang login
 */
public class UserSession {
    
    private static UserSession instance;
    
    private int userId;
    private String namaUser;
    private String nim;
    private String email;
    private String kontak;
    private String role; // "admin" atau "peminjam"
    
    private UserSession() {
        // Private constructor untuk singleton pattern
    }

  
    
    /**
     * Mendapatkan instance UserSession (Singleton)
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    
    
    /**
     * Set data user yang login
     */
    public void setUser(int userId, String namaUser, String nim, String email, String kontak, String role) {
        this.userId = userId;
        this.namaUser = namaUser;
        this.nim = nim;
        this.email = email;
        this.kontak = kontak;
        this.role = role;
    }
    
    /**
     * Clear session (logout)
     */
    public void clearSession() {
        this.userId = 0;
        this.namaUser = null;
        this.nim = null;
        this.email = null;
        this.kontak = null;
        this.role = null;
    }
    
    /**
     * Check apakah user sudah login
     */
    public boolean isLoggedIn() {
        return userId != 0 && namaUser != null && role != null;
    }
    
    /**
     * Check apakah user adalah admin
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }
    
    /**
     * Check apakah user adalah peminjam
     */
    public boolean isPeminjam() {
        return "peminjam".equalsIgnoreCase(role);
    }
    
    // Getters
    public int getUserId() {
        return userId;
    }
    
    public String getNamaUser() {
        return namaUser;
    }
    
    public String getNim() {
        return nim;
    }
    
    public String getEmail() {
        return email;
    }

    public String getKontak() {  
    return kontak;
}
    
    public String getRole() {
        return role;
    }
}
