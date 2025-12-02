package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class untuk mengelola koneksi database MySQL
 */
public class DatabaseConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/sistem_inventaris_ukm";
    private static final String USERNAME = "root"; // Sesuaikan dengan username MySQL Anda
    private static final String PASSWORD = ""; // Sesuaikan dengan password MySQL Anda
    
    /**
     * Mendapatkan koneksi database BARU setiap kali dipanggil
     * PENTING: Caller harus menutup connection setelah digunakan!
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connected successfully!");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Failed to connect to database!");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Menutup koneksi database
     */
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection!");
            e.printStackTrace();
        }
    }
    
    /**
     * Test koneksi database
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean isValid = conn != null && !conn.isClosed();
            if (conn != null) {
                conn.close();
            }
            return isValid;
        } catch (SQLException e) {
            return false;
        }
    }
}
