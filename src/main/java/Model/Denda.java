package Model;

import java.sql.Date;

/**
 * Model class untuk data Denda
 * Encapsulation - semua field private dengan getter/setter
 */
public class Denda {
    private int idDenda;
    private int idPeminjaman;
    private int idUser;
    private int jumlahDenda;
    private int hariTelat;
    private Date tanggalHitung;
    private String statusBayar; // "belum_bayar" atau "lunas"
    private Date tanggalBayar;
    private String keterangan;
    
    // Data tambahan untuk display
    private String namaUser;
    private String namaBarang;
    
    // Constructor
    public Denda() {}
    
    public Denda(int idPeminjaman, int idUser, int jumlahDenda, int hariTelat) {
        this.idPeminjaman = idPeminjaman;
        this.idUser = idUser;
        this.jumlahDenda = jumlahDenda;
        this.hariTelat = hariTelat;
        this.tanggalHitung = new Date(System.currentTimeMillis());
        this.statusBayar = "belum_bayar";
    }
    
    // Getters and Setters
    public int getIdDenda() {
        return idDenda;
    }
    
    public void setIdDenda(int idDenda) {
        this.idDenda = idDenda;
    }
    
    public int getIdPeminjaman() {
        return idPeminjaman;
    }
    
    public void setIdPeminjaman(int idPeminjaman) {
        this.idPeminjaman = idPeminjaman;
    }
    
    public int getIdUser() {
        return idUser;
    }
    
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    
    public int getJumlahDenda() {
        return jumlahDenda;
    }
    
    public void setJumlahDenda(int jumlahDenda) {
        this.jumlahDenda = jumlahDenda;
    }
    
    public int getHariTelat() {
        return hariTelat;
    }
    
    public void setHariTelat(int hariTelat) {
        this.hariTelat = hariTelat;
    }
    
    public Date getTanggalHitung() {
        return tanggalHitung;
    }
    
    public void setTanggalHitung(Date tanggalHitung) {
        this.tanggalHitung = tanggalHitung;
    }
    
    public String getStatusBayar() {
        return statusBayar;
    }
    
    public void setStatusBayar(String statusBayar) {
        this.statusBayar = statusBayar;
    }
    
    public Date getTanggalBayar() {
        return tanggalBayar;
    }
    
    public void setTanggalBayar(Date tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }
    
    public String getKeterangan() {
        return keterangan;
    }
    
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    
    public String getNamaUser() {
        return namaUser;
    }
    
    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }
    
    public String getNamaBarang() {
        return namaBarang;
    }
    
    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }
    
    /**
     * Format jumlah denda ke Rupiah
     */
    public String getJumlahDendaFormatted() {
        return String.format("Rp %,d", jumlahDenda);
    }
    
    /**
     * Check apakah denda sudah lunas
     */
    public boolean isLunas() {
        return "lunas".equalsIgnoreCase(statusBayar);
    }
}
