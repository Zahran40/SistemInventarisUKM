/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Admin;

import Utils.SessionHelper;
import DAO.AdminDAO;
import Model.RequestData;
import Utils.SessionHelper;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
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
public class RequestPengembalian extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RequestPengembalian.class.getName());

    public RequestPengembalian() {
        initComponents();
        setLocationRelativeTo(null);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        
        loadDaftarPengembalian();
    }
    
    private void loadDaftarPengembalian() {
        if (panelListKembali == null) return;
        
        DAO.AdminDAO dao = new DAO.AdminDAO();
        java.util.List<Model.RequestData> list = dao.getPendingReturns();
        
        panelListKembali.removeAll();
        panelListKembali.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.anchor = java.awt.GridBagConstraints.NORTH;
        gbc.insets = new java.awt.Insets(0, 0, 10, 0);

        if (list.isEmpty()) {
             javax.swing.JLabel kosong = new javax.swing.JLabel("Tidak ada permintaan pengembalian.");
             kosong.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
             kosong.setFont(new java.awt.Font("Segoe UI", 1, 14));
             panelListKembali.add(kosong, gbc);
        } else {
            for (Model.RequestData rd : list) {
                gbc.weighty = 0; 
                panelListKembali.add(createCard(rd), gbc);
                gbc.gridy++;
            }
            java.awt.GridBagConstraints gbcFiller = new java.awt.GridBagConstraints();
            gbcFiller.gridx = 0;
            gbcFiller.gridy = gbc.gridy;
            gbcFiller.weighty = 1.0; 
            panelListKembali.add(new javax.swing.JLabel(), gbcFiller);
        }
        
        panelListKembali.revalidate();
        panelListKembali.repaint();
    }
    
    private javax.swing.JPanel createCard(Model.RequestData rd) {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(null);
        panel.setPreferredSize(new java.awt.Dimension(700, 200));
        panel.setBackground(new java.awt.Color(204, 255, 204));
        panel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK));
        
        java.awt.Font fontBold = new java.awt.Font("Segoe UI", 1, 14);
        
        addLabel(panel, "Nama Peminjam:", rd.getNamaPeminjam(), 15, 15, fontBold);
        addLabel(panel, "Nama Barang:", rd.getNamaBarang(), 15, 40, fontBold);
        addLabel(panel, "Jml Kembali:", rd.getJumlah() + " Unit", 15, 65, fontBold);
        addLabel(panel, "Tgl Kembali:", String.valueOf(rd.getTanggalKembali()), 15, 90, fontBold);
        
        if (rd.getBuktiValidasi() != null && !rd.getBuktiValidasi().isEmpty()) {
             javax.swing.JButton btnBukti = new javax.swing.JButton("Lihat Foto Barang");
             btnBukti.setBounds(400, 15, 200, 30);
             btnBukti.setBackground(new java.awt.Color(0, 102, 204));
             btnBukti.setForeground(java.awt.Color.WHITE);
             
             btnBukti.addActionListener(e -> {
                 try {
                     DAO.AdminDAO dao = new DAO.AdminDAO();
                     // Gunakan rd.getIdPengembalian() yang benar
                     java.io.File file = dao.getBuktiPengembalian(rd.getIdPengembalian());
                     
                     if (file != null && file.exists()) {
                         java.awt.Desktop.getDesktop().open(file);
                     } else {
                         javax.swing.JOptionPane.showMessageDialog(this, "File bukti tidak ditemukan/kosong.");
                     }
                 } catch (Exception ex) {
                     ex.printStackTrace();
                 }
             });
             panel.add(btnBukti);
        }
        
        javax.swing.JComboBox<String> cmb = new javax.swing.JComboBox<>(new String[]{"Pilih...", "Setujui", "Tolak"});
        cmb.setBounds(100, 140, 150, 30);
        panel.add(cmb);
        
        javax.swing.JButton btnOk = new javax.swing.JButton("Konfirmasi");
        btnOk.setBackground(new java.awt.Color(0, 204, 0));
        btnOk.setForeground(java.awt.Color.WHITE);
        btnOk.setBounds(260, 140, 100, 30);
        
        btnOk.addActionListener(e -> {
            String aksi = cmb.getSelectedItem().toString();
            if (aksi.equals("Pilih...")) return;
            
            if (javax.swing.JOptionPane.showConfirmDialog(this, "Proses " + aksi + "?", "Cek", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION) {
                String keterangan = "";
                
                // Jika admin menolak, minta keterangan wajib
                if (aksi.equals("Tolak")) {
                    keterangan = javax.swing.JOptionPane.showInputDialog(this,
                        "Masukkan keterangan penolakan:\n(contoh: Barang rusak, jumpai saya di UKM)",
                        "Keterangan Penolakan",
                        javax.swing.JOptionPane.PLAIN_MESSAGE);
                    
                    // Validasi keterangan tidak boleh kosong untuk penolakan
                    if (keterangan == null || keterangan.trim().isEmpty()) {
                        javax.swing.JOptionPane.showMessageDialog(this, 
                            "Keterangan wajib diisi untuk penolakan!", 
                            "Peringatan", 
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                
                DAO.AdminDAO dao = new DAO.AdminDAO();
                // Gunakan rd.getIdPengembalian() bukan rd.getIdPeminjaman()
                boolean sukses = dao.prosesPengembalian(rd.getIdPengembalian(), rd.getIdBarang(), rd.getJumlah(), aksi, keterangan);
                
                if (sukses) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Berhasil diproses!");
                    loadDaftarPengembalian();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Gagal memproses! Cek console untuk detail error.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(btnOk);
        
        return panel;
    }
    
    private void addLabel(JPanel p, String title, String val, int x, int y, Font f) {
        JLabel l = new JLabel(title + " " + val);
        l.setFont(f);
        l.setBounds(x, y, 400, 20);
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

        jPanel3 = new javax.swing.JPanel();
        addbarang = new javax.swing.JButton();
        editbarang = new javax.swing.JButton();
        hapusbarang = new javax.swing.JButton();
        logpeminjaman = new javax.swing.JButton();
        reqpeminjaman = new javax.swing.JButton();
        reqpengembalian = new javax.swing.JButton();
        manajemendenda = new javax.swing.JButton();
        addbarang1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        panelListKembali = new javax.swing.JPanel();

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

        reqpengembalian.setBackground(new java.awt.Color(0, 204, 0));
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

        manajemendenda.setBackground(new java.awt.Color(60, 63, 65));
        manajemendenda.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        manajemendenda.setForeground(new java.awt.Color(255, 255, 255));
        manajemendenda.setText("Manajemen Denda");
        manajemendenda.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manajemendenda.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        manajemendenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manajemendendaActionPerformed(evt);
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
                    .addComponent(manajemendenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addbarang1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(manajemendenda, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(0, 204, 0));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("SiUkm - Sistem Inventaris UKM");
        jPanel2.add(jLabel1, new java.awt.GridBagConstraints());

        jPanel4.setBackground(new java.awt.Color(0, 204, 0));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Request Pengembalian");

        jScrollPane1.setForeground(new java.awt.Color(0, 204, 0));

        panelListKembali.setBackground(new java.awt.Color(0, 204, 0));
        panelListKembali.setForeground(new java.awt.Color(0, 204, 0));

        javax.swing.GroupLayout panelListKembaliLayout = new javax.swing.GroupLayout(panelListKembali);
        panelListKembali.setLayout(panelListKembaliLayout);
        panelListKembaliLayout.setHorizontalGroup(
            panelListKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );
        panelListKembaliLayout.setVerticalGroup(
            panelListKembaliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 462, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(panelListKembali);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 675, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1))
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
        // Sudah di halaman request pengembalian, tidak perlu navigasi
    }//GEN-LAST:event_reqpengembalianActionPerformed

    private void manajemendendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manajemendendaActionPerformed
        new ManajemenDenda().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_manajemendendaActionPerformed

    private void addbarang1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addbarang1ActionPerformed
        new DashboardAdmin().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_addbarang1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // Logout functionality
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
        java.awt.EventQueue.invokeLater(() -> new RequestPengembalian().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addbarang;
    private javax.swing.JButton addbarang1;
    private javax.swing.JButton editbarang;
    private javax.swing.JButton hapusbarang;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton logpeminjaman;
    private javax.swing.JPanel panelListKembali;
    private javax.swing.JButton reqpeminjaman;
    private javax.swing.JButton reqpengembalian;
    private javax.swing.JButton manajemendenda;
    // End of variables declaration//GEN-END:variables
}
