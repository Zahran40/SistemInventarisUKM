/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Admin;

import DAO.AdminDAO;
import Model.RequestData;
import Utils.SessionHelper;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author ASUS
 */
public class RequestPeminjaman extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RequestPeminjaman.class.getName());

    /**
     * Creates new form RequestPeminjaman
     */
    public RequestPeminjaman() {
        if (!SessionHelper.checkAdmin(this)) return;
        initComponents();
        setLocationRelativeTo(null);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH); 
        
        loadDaftarRequest();
    }
    
    private void loadDaftarRequest() {
        if (jPanel1 == null) return;
        
        DAO.AdminDAO dao = new DAO.AdminDAO();
        java.util.List<Model.RequestData> listReq = dao.getPendingRequests();
        
        jPanel1.removeAll(); 
        
        jPanel1.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.NORTH; 
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; 
        gbc.weightx = 1.0; 
        gbc.insets = new java.awt.Insets(0, 0, 10, 0); 

        if (listReq.isEmpty()) {
            javax.swing.JLabel kosong = new javax.swing.JLabel("Tidak ada pengajuan peminjaman baru.");
            kosong.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            kosong.setFont(new java.awt.Font("Segoe UI", 1, 14));
            jPanel1.add(kosong, gbc);
        } else {
            for (Model.RequestData rd : listReq) {
                gbc.weighty = 0;
                javax.swing.JPanel card = createCard(rd);
                jPanel1.add(card, gbc);
                
                gbc.gridy++; 
            }
           
            java.awt.GridBagConstraints gbcFiller = new java.awt.GridBagConstraints();
            gbcFiller.gridx = 0;
            gbcFiller.gridy = gbc.gridy; 
            gbcFiller.weighty = 1.0;
            jPanel1.add(new javax.swing.JLabel(), gbcFiller);
        }
        
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    // 2. Kartu Dinamis 
   private javax.swing.JPanel createCard(Model.RequestData rd) {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(null);
        panel.setPreferredSize(new java.awt.Dimension(700, 300)); 
        panel.setBackground(new java.awt.Color(204, 255, 204));
        panel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK));

        java.awt.Font fontBold = new java.awt.Font("Segoe UI", 1, 14);
        java.awt.Font fontPlain = new java.awt.Font("Segoe UI", 0, 14);
        java.awt.Color textColor = new java.awt.Color(102, 102, 102);
        
        String realKeterangan = rd.getKeterangan();
        String namaFileBukti = rd.getBuktiValidasi(); 
        if (namaFileBukti == null) namaFileBukti = "";
        
        addLabel(panel, "Nama Peminjam :", 15, 15, fontBold, textColor);
        addLabel(panel, rd.getNamaPeminjam(), 150, 15, fontPlain, textColor);

        addLabel(panel, "Nama Barang :", 15, 40, fontBold, textColor);
        addLabel(panel, rd.getNamaBarang(), 150, 40, fontPlain, textColor);

        addLabel(panel, "Jumlah :", 15, 65, fontBold, textColor);
        addLabel(panel, String.valueOf(rd.getJumlah()), 150, 65, fontPlain, textColor);

        addLabel(panel, "Tgl Request :", 15, 90, fontBold, textColor);
        addLabel(panel, String.valueOf(rd.getTanggalPinjam()), 150, 90, fontPlain, textColor);

        addLabel(panel, "Est. Kembali :", 15, 115, fontBold, textColor);
        addLabel(panel, String.valueOf(rd.getTanggalKembali()), 150, 115, fontPlain, textColor);

        addLabel(panel, "Keterangan :", 15, 140, fontBold, textColor);
        
        javax.swing.JTextArea txtKet = new javax.swing.JTextArea(realKeterangan);
        txtKet.setLineWrap(true);
        txtKet.setWrapStyleWord(true);
        txtKet.setEditable(false);
        txtKet.setBackground(new java.awt.Color(230, 255, 230));
        txtKet.setBounds(150, 140, 400, 40);
        panel.add(txtKet);

        addLabel(panel, "Bukti Validasi :", 15, 190, fontBold, textColor);

        String statusBukti = rd.getBuktiValidasi();
        if (statusBukti != null && !statusBukti.isEmpty()) {
            javax.swing.JButton btnLihatFile = new javax.swing.JButton("Lihat File Bukti");
            btnLihatFile.setBounds(150, 190, 200, 30);
            btnLihatFile.setBackground(new java.awt.Color(0, 102, 204));
            btnLihatFile.setForeground(java.awt.Color.WHITE);

            btnLihatFile.addActionListener(e -> {
                try {
                    DAO.AdminDAO dao = new DAO.AdminDAO();
                    java.io.File fileFisik = dao.getBuktiFisik(rd.getIdPeminjaman());
                   
                    if (fileFisik != null && fileFisik.exists()) {
                        java.awt.Desktop.getDesktop().open(fileFisik);
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(this, "Gagal membuka file gambar.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            
            panel.add(btnLihatFile);
        } else {
            javax.swing.JLabel lblKosong = new javax.swing.JLabel("- Tidak ada bukti -");
            lblKosong.setBounds(150, 190, 200, 20);
            panel.add(lblKosong);
        }

        javax.swing.JLabel lblStatus = new javax.swing.JLabel("Tindakan");
        lblStatus.setFont(fontBold);
        lblStatus.setForeground(textColor);
        lblStatus.setBounds(15, 240, 80, 30);
        panel.add(lblStatus);

        javax.swing.JComboBox<String> cmbAksi = new javax.swing.JComboBox<>();
        cmbAksi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pilih Aksi...", "Setujui", "Tolak" }));
        cmbAksi.setBounds(100, 240, 150, 30);
        panel.add(cmbAksi);

        javax.swing.JButton btnKonfirm = new javax.swing.JButton("Konfirmasi");
        btnKonfirm.setBackground(new java.awt.Color(102, 204, 0));
        btnKonfirm.setForeground(java.awt.Color.WHITE);
        btnKonfirm.setFont(fontBold);
        btnKonfirm.setBounds(550, 240, 120, 35);
        
        btnKonfirm.addActionListener(e -> {
            String keputusan = cmbAksi.getSelectedItem().toString();
            if (keputusan.equals("Pilih Aksi...")) {
                javax.swing.JOptionPane.showMessageDialog(this, "Harap pilih Setujui atau Tolak!");
                return;
            }
            
            int confirm = javax.swing.JOptionPane.showConfirmDialog(this, 
                    "Proses " + keputusan + "?", "Konfirmasi", javax.swing.JOptionPane.YES_NO_OPTION);
            
            if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                DAO.AdminDAO dao = new DAO.AdminDAO();
                boolean sukses = dao.prosesPeminjaman(rd.getIdPeminjaman(), rd.getIdBarang(), rd.getJumlah(), keputusan);
                if (sukses) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Berhasil!");
                    loadDaftarRequest(); 
                }
            }
        });
        
        panel.add(btnKonfirm);
        return panel;
    }

    // Helper kecil untuk mempersingkat kode Label
    private void addLabel(javax.swing.JPanel p, String text, int x, int y, java.awt.Font f, java.awt.Color c) {
        javax.swing.JLabel l = new javax.swing.JLabel(text);
        l.setFont(f);
        l.setForeground(c);
        l.setBounds(x, y, 130, 20); 
        if (x > 100) l.setSize(400, 20);
        p.add(l);
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

        jPanel3 = new javax.swing.JPanel();
        addbarang = new javax.swing.JButton();
        editbarang = new javax.swing.JButton();
        hapusbarang = new javax.swing.JButton();
        logpeminjaman = new javax.swing.JButton();
        reqpeminjaman = new javax.swing.JButton();
        reqpengembalian = new javax.swing.JButton();
        addbarang1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        editbarang.setBackground(new java.awt.Color(60, 63, 65));
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

        reqpeminjaman.setBackground(new java.awt.Color(0, 204, 0));
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

        jButton4.setBackground(new java.awt.Color(255, 51, 51));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Log Out");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

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
                    .addComponent(logpeminjaman, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reqpeminjaman, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reqpengembalian, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addbarang1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(9, 9, 9)
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
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(0, 204, 0));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Request Peminjaman");
        jLabel2.setPreferredSize(new java.awt.Dimension(260, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 438;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 32, 0, 0);
        jPanel4.add(jLabel2, gridBagConstraints);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel1.setBackground(new java.awt.Color(0, 204, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 752, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 548, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jScrollPane1, gridBagConstraints);

        jPanel2.setBackground(new java.awt.Color(0, 204, 0));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("SiUkm - Sistem Inventaris UKM");
        jPanel2.add(jLabel1, new java.awt.GridBagConstraints());

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
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addbarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addbarangActionPerformed
        new tambahbarang().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_addbarangActionPerformed

    private void editbarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editbarangActionPerformed
        new editbarang().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_editbarangActionPerformed

    private void hapusbarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusbarangActionPerformed
        new hapusbarang().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_hapusbarangActionPerformed

    private void logpeminjamanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logpeminjamanActionPerformed
        new LogPeminjaman().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logpeminjamanActionPerformed

    private void reqpeminjamanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reqpeminjamanActionPerformed
        new RequestPeminjaman().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_reqpeminjamanActionPerformed

    private void reqpengembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reqpengembalianActionPerformed
        new RequestPengembalian().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_reqpengembalianActionPerformed

    private void addbarang1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addbarang1ActionPerformed
        new DashboardAdmin().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_addbarang1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        Utils.UserSession.getInstance().clearSession();
        javax.swing.JOptionPane.showMessageDialog(this, "Berhasil logout!");
        new Register.LoginPage().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed


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
        java.awt.EventQueue.invokeLater(() -> new RequestPeminjaman().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addbarang;
    private javax.swing.JButton addbarang1;
    private javax.swing.JButton editbarang;
    private javax.swing.JButton hapusbarang;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton logpeminjaman;
    private javax.swing.JButton reqpeminjaman;
    private javax.swing.JButton reqpengembalian;
    // End of variables declaration//GEN-END:variables
}
