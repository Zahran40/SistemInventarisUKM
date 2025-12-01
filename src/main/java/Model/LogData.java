/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import java.sql.Date;

/**
 *
 * @author aldriknoel
 */
public class LogData {
    private String namaPeminjam;
    private String namaBarang;
    private Date tanggal;
    private String status; 

    // Constructor & Gettrer Setter
    public String getNamaPeminjam() { return namaPeminjam; }
    public void setNamaPeminjam(String namaPeminjam) { this.namaPeminjam = namaPeminjam; }

    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }

    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Method untuk membuat kalimat aktivitas otomatis
    public String getAktivitas() {
        if ("disetujui".equalsIgnoreCase(status)) {
            return "Sedang meminjam " + namaBarang;
        } else if ("dikembalikan".equalsIgnoreCase(status)) {
            return "Telah mengembalikan " + namaBarang;
        } else if ("proses".equalsIgnoreCase(status)) {
            return "Request meminjam " + namaBarang;
        } else if ("ditolak".equalsIgnoreCase(status)) {
            return "Gagal meminjam " + namaBarang;
        }
        return "Interaksi dengan " + namaBarang;
    }
}
