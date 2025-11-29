/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Admin;
import Database.DatabaseConnection; // Pastikan ini sesuai lokasi file koneksi Anda
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import Utils.SessionHelper;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author asus
 */
public class editbarang extends javax.swing.JFrame {

    /**
     * Creates new form editbarang
     */
    private void tampilkanData() {
        // 1. Bersihkan layar dulu
        panelContainer.removeAll();

        // 2. Set Layout: 2 Kolom, baris otomatis nambah
        panelContainer.setLayout(new GridLayout(0, 2, 20, 20)); 

        // 3. Query Data
        String sql = "SELECT b.id_barang, b.nama_barang, b.status, k.nama_kategori " +
                     "FROM barang b " +
                     "LEFT JOIN kategori_barang k ON b.id_kategori = k.id_kategori";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            boolean adaData = false;

            while (rs.next()) {
                adaData = true;

                // Ambil data dari database
                String id = rs.getString("id_barang");
                String nama = rs.getString("nama_barang");
                String kat = rs.getString("nama_kategori");
                String sts = rs.getString("status");

                // Jika kategori null (karena LEFT JOIN), ganti jadi strip
                if (kat == null) kat = "-";

                // --- MULAI GAMBAR KARTU ---

                // 1. Buat Panel Kartu
                JPanel card = new JPanel();
                card.setLayout(new GridLayout(4, 1, 5, 5)); // Susunan vertikal
                card.setBackground(Color.WHITE);
                // Kasih garis pinggir biar jelas
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 153, 0), 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                // 2. Buat Label Nama Barang
                JLabel lblNama = new JLabel(nama);
                lblNama.setFont(new java.awt.Font("Segoe UI", 1, 16));

                // 3. Buat Label Kategori
                JLabel lblKat = new JLabel("Kategori: " + kat);
                lblKat.setForeground(Color.GRAY);

                // 4. Buat Label Status
                JLabel lblSts = new JLabel("Status: " + sts);
                if(sts != null && sts.equalsIgnoreCase("Tersedia")) {
                    lblSts.setForeground(new Color(0, 153, 0)); // Hijau
                } else {
                    lblSts.setForeground(Color.RED); // Merah
                }

                // 5. Buat Tombol Edit
                JButton btnEdit = new JButton("Edit");
                btnEdit.setBackground(new Color(0, 153, 0));
                btnEdit.setForeground(Color.WHITE);
                btnEdit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    new formeditbarang(editbarang.this, true, id).setVisible(true);                      
                    tampilkanData();
                    }
                });

                // 6. TEMPELKAN KOMPONEN KE KARTU (PENTING!)
                card.add(lblNama);
                card.add(lblKat);
                card.add(lblSts);
                card.add(btnEdit);

                // 7. TEMPELKAN KARTU KE CONTAINER UTAMA (PENTING!)
                panelContainer.add(card);
            }

            // Jika data kosong
            if (!adaData) {
                JLabel kosong = new JLabel("Tidak ada data barang.");
                kosong.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                panelContainer.add(kosong);
            }

            // 8. UPDATE TAMPILAN (REFRESH)
            panelContainer.revalidate();
            panelContainer.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal load data: " + e.getMessage());
        }
    }
    public editbarang() {
        if (!SessionHelper.checkAdmin(this)) return;
        initComponents();
        setLocationRelativeTo(null);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH); // Fullscreen
        tampilkanData();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        panelContainer = new javax.swing.JPanel();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        javax.swing.JButton addbarang = new javax.swing.JButton();
        javax.swing.JButton editbarang = new javax.swing.JButton();
        javax.swing.JButton hapusbarang = new javax.swing.JButton();
        javax.swing.JButton logpeminjaman = new javax.swing.JButton();
        javax.swing.JButton reqpeminjaman = new javax.swing.JButton();
        javax.swing.JButton reqpengembalian = new javax.swing.JButton();
        javax.swing.JButton addbarang1 = new javax.swing.JButton();
        javax.swing.JButton jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel4.setBackground(new java.awt.Color(0, 204, 0));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("DAFTAR BARANG");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel4.add(jLabel2, gridBagConstraints);

        jScrollPane1.setViewportView(panelContainer);

        javax.swing.GroupLayout panelContainerLayout = new javax.swing.GroupLayout(panelContainer);
        panelContainer.setLayout(panelContainerLayout);
        panelContainerLayout.setHorizontalGroup(
            panelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 303, Short.MAX_VALUE)
        );
        panelContainerLayout.setVerticalGroup(
            panelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 296, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(panelContainer);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jScrollPane1, gridBagConstraints);
        jScrollPane1.getAccessibleContext().setAccessibleName("");

        jPanel2.setBackground(new java.awt.Color(0, 204, 0));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("SiUkm - Sistem Inventaris UKM");
        jPanel2.add(jLabel1, new java.awt.GridBagConstraints());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        addbarang.setBackground(new java.awt.Color(60, 63, 65));
        addbarang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addbarang.setForeground(new java.awt.Color(255, 255, 255));
        addbarang.setText("Tambah Barang");
        addbarang.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        addbarang.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        addbarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addbarangActionPerformed(evt);
            }
        });

        editbarang.setBackground(new java.awt.Color(0, 204, 0));
        editbarang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        editbarang.setForeground(new java.awt.Color(255, 255, 255));
        editbarang.setText("Edit Barang");
        editbarang.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        editbarang.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        editbarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editbarangActionPerformed(evt);
            }
        });

        hapusbarang.setBackground(new java.awt.Color(60, 63, 65));
        hapusbarang.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        hapusbarang.setForeground(new java.awt.Color(255, 255, 255));
        hapusbarang.setText("Hapus Barang");
        hapusbarang.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        hapusbarang.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        hapusbarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusbarangActionPerformed(evt);
            }
        });

        logpeminjaman.setBackground(new java.awt.Color(60, 63, 65));
        logpeminjaman.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        logpeminjaman.setForeground(new java.awt.Color(255, 255, 255));
        logpeminjaman.setText("Log Peminjaman");
        logpeminjaman.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        logpeminjaman.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        logpeminjaman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logpeminjamanActionPerformed(evt);
            }
        });

        reqpeminjaman.setBackground(new java.awt.Color(60, 63, 65));
        reqpeminjaman.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        reqpeminjaman.setForeground(new java.awt.Color(255, 255, 255));
        reqpeminjaman.setText("Request Peminjaman");
        reqpeminjaman.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        reqpeminjaman.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        reqpeminjaman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reqpeminjamanActionPerformed(evt);
            }
        });

        reqpengembalian.setBackground(new java.awt.Color(60, 63, 65));
        reqpengembalian.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        reqpengembalian.setForeground(new java.awt.Color(255, 255, 255));
        reqpengembalian.setText("Request Pengembalian");
        reqpengembalian.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        reqpengembalian.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        reqpengembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reqpengembalianActionPerformed(evt);
            }
        });

        addbarang1.setBackground(new java.awt.Color(60, 63, 65));
        addbarang1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        addbarang1.setForeground(new java.awt.Color(255, 255, 255));
        addbarang1.setText("Dashboard");
        addbarang1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        addbarang1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        addbarang1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addbarang1ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(255, 51, 51));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Log Out");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addbarang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editbarang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hapusbarang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reqpeminjaman, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reqpengembalian, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addbarang1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logpeminjaman, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(addbarang1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addbarang, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editbarang, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hapusbarang, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logpeminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reqpeminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reqpengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void reqpengembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reqpengembalianActionPerformed
        new RequestPengembalian().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_reqpengembalianActionPerformed

    private void reqpeminjamanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reqpeminjamanActionPerformed
        new RequestPeminjaman().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_reqpeminjamanActionPerformed

    private void logpeminjamanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logpeminjamanActionPerformed
        new LogPeminjaman().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logpeminjamanActionPerformed

    private void hapusbarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusbarangActionPerformed
        new hapusbarang().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_hapusbarangActionPerformed

    private void editbarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editbarangActionPerformed
        new DashboardAdmin().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_editbarangActionPerformed

    private void addbarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addbarangActionPerformed
        new tambahbarang().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_addbarangActionPerformed

    private void addbarang1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addbarang1ActionPerformed
        new DashboardAdmin().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_addbarang1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // Logout
        Utils.UserSession.getInstance().clearSession();
        javax.swing.JOptionPane.showMessageDialog(this, "Berhasil logout!");
        new Register.LoginPage().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(editbarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(editbarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(editbarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(editbarang.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new editbarang().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JPanel panelContainer;
    // End of variables declaration//GEN-END:variables
}
