/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author aldriknoel
 */
public class RequestData {
    private int idPeminjaman;
    private int idPengembalian; // ID untuk tabel pengembalian
    private int idBarang;
    private String namaPeminjam;
    private String namaBarang;
    private int jumlah;
    private java.sql.Date tanggalPinjam;
    private java.sql.Date tanggalKembali;
    private String keterangan; 
    private String status;
    private String buktiValidasi;

    // Constructor Kosong
    public RequestData() {}

    // Getter & Setter (Penting!)
    public int getIdPeminjaman() { return idPeminjaman; }
    public void setIdPeminjaman(int idPeminjaman) { this.idPeminjaman = idPeminjaman; }
    
    public int getIdPengembalian() { return idPengembalian; }
    public void setIdPengembalian(int idPengembalian) { this.idPengembalian = idPengembalian; }

    public int getIdBarang() { return idBarang; }
    public void setIdBarang(int idBarang) { this.idBarang = idBarang; }

    public String getNamaPeminjam() { return namaPeminjam; }
    public void setNamaPeminjam(String namaPeminjam) { this.namaPeminjam = namaPeminjam; }

    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public java.sql.Date getTanggalPinjam() { return tanggalPinjam; }
    public void setTanggalPinjam(java.sql.Date tanggalPinjam) { this.tanggalPinjam = tanggalPinjam; }

    public java.sql.Date getTanggalKembali() { return tanggalKembali; }
    public void setTanggalKembali(java.sql.Date tanggalKembali) { this.tanggalKembali = tanggalKembali; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
    
    public String getBuktiValidasi() { return buktiValidasi; }
    public void setBuktiValidasi(String buktiValidasi) { this.buktiValidasi = buktiValidasi; }
    
    public String getStatus() { return status; } 
    public void setStatus(String status) { this.status = status; }
}