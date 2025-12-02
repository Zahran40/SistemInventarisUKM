/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author ASUS
 */
public class User {
    private int idUser;
    private String nim;
    private String nama;
    private String password;
    private String email;
    private String noHp;
    private String alamat;
    private String role;
    
    // Constructor kosong
    public User() {}
    
    // Constructor lengkap
    public User(int idUser, String nim, String nama, String password, 
                String email, String noHp, String alamat, String role) {
        this.idUser = idUser;
        this.nim = nim;
        this.nama = nama;
        this.password = password;
        this.email = email;
        this.noHp = noHp;
        this.alamat = alamat;
        this.role = role;
    }
    
    // Getters dan Setters
    public int getIdUser() {
        return idUser;
    }
    
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    
    public String getNim() {
        return nim;
    }
    
    public void setNim(String nim) {
        this.nim = nim;
    }
    
    public String getNama() {
        return nama;
    }
    
    public void setNama(String nama) {
        this.nama = nama;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNoHp() {
        return noHp;
    }
    
    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }
    
    public String getAlamat() {
        return alamat;
    }
    
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}
