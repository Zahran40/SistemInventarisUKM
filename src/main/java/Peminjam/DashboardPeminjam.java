/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Peminjam;

import Utils.UserSession;
import Register.LoginPage;
import javax.swing.JOptionPane;
import Model.Barang;
import DAO.BarangDAO;
import java.util.List;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BorderFactory;

/**
 *
 * @author ASUS
 */
public class DashboardPeminjam extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashboardPeminjam.class.getName());

    /**
     * Creates new form DashboardPeminjam
     */
    public DashboardPeminjam() {
        // Check session
        if (!checkSession()) return; 
        initComponents();
        isiComboBoxKategori();
        loadDataBarang("", "Semua");
        setLocationRelativeTo(null);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH); // Fullscreen
    }
    
    // Panggil method ini di Constructor: loadDataBarang("", "Semua");
    private void loadDataBarang(String keyword, String kategori) {
        // 1. Ambil data pakai DAO yang baru (filter)
        DAO.BarangDAO dao = new DAO.BarangDAO();
        List<Barang> data = dao.filterBarang(keyword, kategori);
        
        // 2. Bersihkan Panel
        panelDaftarBarang.removeAll();
        
        // 3. Setup Layout Grid
        int rows = (int) Math.ceil(data.size() / 2.0);
        if (rows < 1) rows = 1;
        
        panelDaftarBarang.setLayout(new java.awt.GridLayout(rows, 2, 15, 15));
        panelDaftarBarang.setBackground(new java.awt.Color(102, 204, 0)); // Sesuaikan warna background

        // 4. Looping Kartu
        for (Barang b : data) {
            JPanel card = createCard(b);
            panelDaftarBarang.add(card);
        }

        // 5. Refresh UI (PENTING)
        panelDaftarBarang.revalidate();
        panelDaftarBarang.repaint();
    }
    
    private void isiComboBoxKategori() {
        // 1. Kosongkan dulu isi combo box (hapus "Item 1", "Item 2" bawaan)
        cmbKategori.removeAllItems();
        
        // 2. Tambahkan pilihan default "Semua"
        cmbKategori.addItem("Semua");
        
        // 3. Ambil data asli dari Database lewat DAO
        DAO.BarangDAO dao = new DAO.BarangDAO();
        List<String> daftarKategori = dao.getNamaKategori();
        
        // 4. Masukkan satu per satu ke Combo Box
        for (String namaKat : daftarKategori) {
            cmbKategori.addItem(namaKat);
        }
    }
    
    private javax.swing.JPanel createCard(Model.Barang b) {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(null);
        panel.setPreferredSize(new java.awt.Dimension(300, 150));
        panel.setBackground(new java.awt.Color(64, 64, 64)); 
        panel.setBorder(null);

        // 1. Label Nama Barang
        javax.swing.JLabel lblNama = new javax.swing.JLabel(b.getNama());
        lblNama.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblNama.setForeground(java.awt.Color.WHITE);
        lblNama.setBounds(15, 15, 190, 25);
        panel.add(lblNama);

        // 2. Label Status
        javax.swing.JLabel lblStatus = new javax.swing.JLabel(b.getStatus());
        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 12));
        if ("tersedia".equalsIgnoreCase(b.getStatus())) {
            lblStatus.setForeground(new java.awt.Color(51, 255, 51)); // Hijau
        } else {
            lblStatus.setForeground(java.awt.Color.RED); // Merah
        }
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblStatus.setBounds(210, 15, 70, 25);
        panel.add(lblStatus);

        // 3. Label Kategori
        javax.swing.JLabel lblKategori = new javax.swing.JLabel(b.getNamaKategori());
        lblKategori.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblKategori.setForeground(new java.awt.Color(102, 204, 0)); // Hijau Olive
        lblKategori.setBounds(15, 45, 200, 20);
        panel.add(lblKategori);

        // 4. Label Stok
        javax.swing.JLabel lblStok = new javax.swing.JLabel("Stok: " + b.getStok());
        lblStok.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblStok.setForeground(java.awt.Color.WHITE);
        lblStok.setBounds(15, 100, 100, 30);
        panel.add(lblStok);

        // 5. TOMBOL DETAIL (BAGIAN PENTING)
        javax.swing.JButton btnDetail = new javax.swing.JButton("Detail");
        btnDetail.setBounds(190, 100, 90, 30);
        btnDetail.setBackground(new java.awt.Color(51, 153, 0));
        btnDetail.setForeground(java.awt.Color.WHITE);
        btnDetail.setFocusPainted(false);
        btnDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Saat diklik, buka DetailBarang dengan membawa ID barang ini
                new Peminjam.DetailBarang(b.getId()).setVisible(true);
                
                // Opsional: Tutup dashboard saat ini agar tidak menumpuk
                dispose(); 
            }
        });
        
        panel.add(btnDetail);
        return panel;
    }
    
    /**
     * Method untuk check apakah user sudah login sebagai peminjam
     * @return true jika valid, false jika tidak valid
     */
    private boolean checkSession() {
        UserSession session = UserSession.getInstance();
        
        if (!session.isLoggedIn()) {
            // Belum login, redirect ke login page
            JOptionPane.showMessageDialog(null, 
                "Anda harus login terlebih dahulu!", 
                "Akses Ditolak", 
                JOptionPane.WARNING_MESSAGE);
            new LoginPage().setVisible(true);
            return false;
        }
        
        if (!session.isPeminjam()) {
            // Bukan peminjam, redirect ke login
            JOptionPane.showMessageDialog(null, 
                "Akses ditolak! Halaman ini hanya untuk Peminjam.", 
                "Akses Ditolak", 
                JOptionPane.ERROR_MESSAGE);
            new LoginPage().setVisible(true);
            return false;
        }
        
        return true;
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        cmbKategori = new javax.swing.JComboBox<>();
        panelDaftarBarang = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(0, 204, 0));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("SiUkm - Sistem Inventaris UKM");
        jPanel2.add(jLabel1, new java.awt.GridBagConstraints());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Riwayat");
        jButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 85;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 89, 24, 0);
        jPanel4.add(jButton1, gridBagConstraints);

        jButton2.setBackground(new java.awt.Color(0, 153, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Catalog");
        jButton2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 87;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 42, 24, 0);
        jPanel4.add(jButton2, gridBagConstraints);

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setText("Profil");
        jButton3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 102;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 93, 24, 0);
        jPanel4.add(jButton3, gridBagConstraints);

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setText("Denda Saya");
        jButton5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 70;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 93, 24, 35);
        jPanel4.add(jButton5, gridBagConstraints);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel3.setBackground(new java.awt.Color(51, 204, 0));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("DAFTAR BARANG");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(32, 18, 0, 0);
        jPanel3.add(jLabel2, gridBagConstraints);

        txtSearch.setText("Cari Nama Barang");
        txtSearch.setMaximumSize(new java.awt.Dimension(200, 30));
        txtSearch.setMinimumSize(new java.awt.Dimension(200, 30));
        txtSearch.setPreferredSize(new java.awt.Dimension(200, 30));
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 117, 0, 0);
        jPanel3.add(txtSearch, gridBagConstraints);

        cmbKategori.setMinimumSize(new java.awt.Dimension(100, 30));
        cmbKategori.setPreferredSize(new java.awt.Dimension(100, 30));
        cmbKategori.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbKategoriItemStateChanged(evt);
            }
        });
        cmbKategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbKategoriActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 18, 0, 0);
        jPanel3.add(cmbKategori, gridBagConstraints);

        panelDaftarBarang.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Nama Barang");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.ipady = -1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 0);
        panelDaftarBarang.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 153, 0));
        jLabel4.setText("Kategori");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 57;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 8, 0, 0);
        panelDaftarBarang.add(jLabel4, gridBagConstraints);

        jLabel5.setForeground(new java.awt.Color(51, 153, 0));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 101, 0, 7);
        panelDaftarBarang.add(jLabel5, gridBagConstraints);

        jButton4.setBackground(new java.awt.Color(0, 153, 0));
        jButton4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Detail");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipady = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(30, 67, 7, 7);
        panelDaftarBarang.add(jButton4, gridBagConstraints);

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jLabel21.setText("Stok");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 83;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(29, 7, 0, 0);
        panelDaftarBarang.add(jLabel21, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(37, 117, 272, 0);
        jPanel3.add(panelDaftarBarang, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel3.add(jSeparator2, gridBagConstraints);

        jScrollPane1.setViewportView(jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Navigasi ke halaman riwayat
        new RiwayatPeminjam().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // Sudah di halaman catalog (refresh)
        new DashboardPeminjam().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // Navigasi ke halaman profil
        new ProfilPeminjam().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // Navigasi ke detail barang
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // Navigasi ke Dashboard Denda
        System.out.println("Tombol Denda Saya diklik!"); // Debug
        try {
            new DashboardDenda().setVisible(true);
            this.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void cmbKategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbKategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbKategoriActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        String keyword = txtSearch.getText();
        String kategori = cmbKategori.getSelectedItem().toString();
        loadDataBarang(keyword, kategori);       
    }//GEN-LAST:event_txtSearchKeyReleased

    private void cmbKategoriItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbKategoriItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            String keyword = txtSearch.getText();
            String kategori = cmbKategori.getSelectedItem().toString();
            loadDataBarang(keyword, kategori);
        }
    }//GEN-LAST:event_cmbKategoriItemStateChanged

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new DashboardPeminjam().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel panelDaftarBarang;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
