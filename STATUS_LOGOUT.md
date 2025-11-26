# STATUS UPDATE TOMBOL LOGOUT

## ✅ SUDAH BERFUNGSI:
1. DashboardAdmin → jButton1 (Log Out) ✅
2. tambahbarang → jButton6 (Log Out) ✅  
3. editbarang → jButton6 (Log Out) ✅
4. hapusbarang → jButton6 (Log Out) ✅
5. LogPeminjaman → jButton2 (Log Out) ✅

## ⚠️ PERLU DITAMBAHKAN MANUAL:
6. RequestPeminjaman → jButton4 (Log Out)
7. RequestPengembalian → jButton4 (Log Out)  
8. ProfilPeminjam → jButton6 (Log Out)

---

## CARA MENAMBAHKAN LOGOUT HANDLER (Via NetBeans):

### Untuk RequestPeminjaman.java:
1. Buka file di NetBeans
2. Klik kanan tombol "Log Out" (jButton4) → Events → Action → actionPerformed
3. NetBeans akan membuat method jButton4ActionPerformed
4. Isi method tersebut dengan kode berikut:

```java
private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
    // Logout
    Utils.UserSession.getInstance().clearSession();
    javax.swing.JOptionPane.showMessageDialog(this, "Berhasil logout!");
    new Register.LoginPage().setVisible(true);
    this.dispose();
}
```

### Untuk RequestPengembalian.java:
1. Buka file di NetBeans
2. Klik kanan tombol "Log Out" (jButton4) → Events → Action → actionPerformed
3. Isi method tersebut dengan kode berikut:

```java
private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
    // Logout
    Utils.UserSession.getInstance().clearSession();
    javax.swing.JOptionPane.showMessageDialog(this, "Berhasil logout!");
    new Register.LoginPage().setVisible(true);
    this.dispose();
}
```

### Untuk ProfilPeminjam.java:
1. Buka file di NetBeans
2. Klik kanan tombol "Log Out" (jButton6) → Events → Action → actionPerformed
3. Isi method tersebut dengan kode berikut:

```java
private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
    // Logout
    Utils.UserSession.getInstance().clearSession();
    javax.swing.JOptionPane.showMessageDialog(this, "Berhasil logout!");
    new Register.LoginPage().setVisible(true);
    this.dispose();
}
```

---

## ATAU VIA KODE LANGSUNG (Edit manual):

Jika tidak pakai NetBeans, tambahkan kode berikut secara manual di file masing-masing.

**File yang SUDAH SELESAI akan langsung berfungsi setelah compile!**
**File yang BELUM (3 file di atas) perlu ditambahkan handler manual.**
