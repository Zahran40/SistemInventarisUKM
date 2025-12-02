/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Peminjam;

import Utils.SessionHelper;
import DAO.PeminjamanDAO;
import Model.RequestData;
import Utils.SessionHelper;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author User
 */
public class RiwayatPeminjam extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RiwayatPeminjam.class.getName());

    public RiwayatPeminjam() {
        if (!SessionHelper.checkPeminjam(this)) return;
        initComponents();
        setLocationRelativeTo(null);
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

        // Load data saat dibuka
        loadRiwayatData();
    }

    public void loadRiwayatData() {
        // PENGAMAN: Cek panel sudah siap atau belum
        if (panelRiwayatContainer == null) return;

        int idUser = 2; // SEMENTARA (Nanti ganti SessionHelper.getUserId())
        
        PeminjamanDAO dao = new PeminjamanDAO();
        List<RequestData> list = dao.getRiwayatUser(idUser);
        
        panelRiwayatContainer.removeAll();
        
        // Layout 1 kolom ke bawah
        panelRiwayatContainer.setLayout(new GridLayout(0, 1, 0, 5));

        if (list.isEmpty()) {
            JLabel kosong = new JLabel("Belum ada riwayat peminjaman.");
            kosong.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            panelRiwayatContainer.add(kosong);
        } else {
            for (RequestData item : list) {
                JPanel row = createRiwayatRow(item);
                panelRiwayatContainer.add(row);
            }
        }
        
        panelRiwayatContainer.revalidate();
        panelRiwayatContainer.repaint();
    }

    private javax.swing.JPanel createRiwayatRow(Model.RequestData item) {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(null);
        panel.setPreferredSize(new java.awt.Dimension(820, 55)); 
        panel.setBackground(java.awt.Color.WHITE);
        panel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.LIGHT_GRAY));

        java.awt.Font fontIsi = new java.awt.Font("Segoe UI", 0, 14);

        javax.swing.JLabel lblNama = new javax.swing.JLabel(item.getNamaBarang());
        lblNama.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblNama.setBounds(20, 17, 200, 20);
        panel.add(lblNama);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy");
        String tglPinjam = (item.getTanggalPinjam() != null) ? sdf.format(item.getTanggalPinjam()) : "-";
        
        String tglKembali = "-";
        if ("disetujui".equalsIgnoreCase(item.getStatus()) && item.getTanggalKembali() != null) {
            tglKembali = sdf.format(item.getTanggalKembali());
        }

        javax.swing.JLabel lblPinjam = new javax.swing.JLabel(tglPinjam);
        lblPinjam.setFont(fontIsi);
        lblPinjam.setBounds(230, 17, 110, 20);
        panel.add(lblPinjam);
        
        javax.swing.JLabel lblKembali = new javax.swing.JLabel(tglKembali);
        lblKembali.setFont(fontIsi);
        lblKembali.setBounds(350, 17, 110, 20);
        panel.add(lblKembali);

        String status = item.getStatus();
        javax.swing.JLabel lblStatus = new javax.swing.JLabel(status.toUpperCase());
        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 12));
        
        if ("disetujui".equalsIgnoreCase(status)) {
            lblStatus.setForeground(new java.awt.Color(0, 153, 0));
        } else if ("ditolak".equalsIgnoreCase(status)) {
            lblStatus.setForeground(java.awt.Color.RED);
        } else if ("dikembalikan".equalsIgnoreCase(status)) {
            lblStatus.setText("DIKEMBALIKAN");
            lblStatus.setForeground(java.awt.Color.BLUE);
        } else if ("pengembalian_ditolak".equalsIgnoreCase(status)) {
            lblStatus.setText("PENGEMBALIAN DITOLAK");
            lblStatus.setForeground(java.awt.Color.RED);
        } else if ("proses_pengembalian".equalsIgnoreCase(status)) {
            lblStatus.setText("PROSES KEMBALI");
            lblStatus.setForeground(java.awt.Color.ORANGE);
        } else {
            lblStatus.setForeground(java.awt.Color.ORANGE);
        }
        lblStatus.setBounds(470, 17, 150, 20);
        panel.add(lblStatus);
        
        // Tambahkan tombol info jika ada keterangan admin (untuk pengembalian ditolak)
        if ("pengembalian_ditolak".equalsIgnoreCase(status) && 
            item.getKeteranganAdmin() != null && 
            !item.getKeteranganAdmin().trim().isEmpty()) {
            
            javax.swing.JButton btnInfo = new javax.swing.JButton("ℹ");
            btnInfo.setFont(new java.awt.Font("Segoe UI", 1, 12));
            btnInfo.setForeground(java.awt.Color.WHITE);
            btnInfo.setBackground(new java.awt.Color(255, 102, 102));
            btnInfo.setBounds(622, 15, 30, 25);
            btnInfo.setToolTipText("Lihat keterangan admin");
            btnInfo.addActionListener(e -> {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Alasan penolakan:\n" + item.getKeteranganAdmin(),
                    "Keterangan Admin",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            });
            panel.add(btnInfo);
        }
        
        javax.swing.JLabel lblDenda = new javax.swing.JLabel("-");
        lblDenda.setFont(fontIsi);
        
        // CEK DENDA DARI DATABASE (bukan hardcoded lagi!)
        if ("disetujui".equalsIgnoreCase(status)) {
            Service.DendaService dendaService = new Service.DendaService();
            java.util.List<Model.Denda> listDenda = dendaService.getDendaByUser(
                Utils.UserSession.getInstance().getUserId()
            );
            
            // Cari denda untuk peminjaman ini
            for (Model.Denda denda : listDenda) {
                if (denda.getIdPeminjaman() == item.getIdPeminjaman() && 
                    "belum_bayar".equalsIgnoreCase(denda.getStatusBayar())) {
                    lblDenda.setText(denda.getJumlahDendaFormatted());
                    lblDenda.setForeground(java.awt.Color.RED);
                    break;
                }
            }
        }
        lblDenda.setBounds(580, 17, 80, 20);
        panel.add(lblDenda);

        javax.swing.JButton btnDetail = new javax.swing.JButton("Detail");
        btnDetail.setFont(new java.awt.Font("Segoe UI", 0, 11));
        btnDetail.setBounds(670, 15, 65, 25);
        btnDetail.addActionListener(e -> {
             new DetailRiwayat(this, item, true).setVisible(true);
        });
        panel.add(btnDetail);

        if ("disetujui".equalsIgnoreCase(status)) {
            javax.swing.JButton btnKembali = new javax.swing.JButton("Kembalikan");
            btnKembali.setBackground(new java.awt.Color(255, 102, 102)); 
            btnKembali.setForeground(java.awt.Color.WHITE);
            btnKembali.setFont(new java.awt.Font("Segoe UI", 1, 11));
            btnKembali.setBounds(740, 15, 95, 25);
            btnKembali.addActionListener(e -> {
                 new FormPengembalian(this, item).setVisible(true);
            });
            panel.add(btnKembali);
        }

        return panel;
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
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        panelRiwayatContainer = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 693, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 842, Short.MAX_VALUE)
        );

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("jRadioButtonMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(0, 204, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("SiUkm - Sistem Inventaris UKM");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 67, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jPanel8.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(jPanel8, gridBagConstraints);

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));
        jPanel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Nama Barang");
        jLabel18.setMaximumSize(new java.awt.Dimension(220, 17));
        jLabel18.setMinimumSize(new java.awt.Dimension(220, 17));
        jLabel18.setPreferredSize(new java.awt.Dimension(220, 17));
        jPanel17.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 15, 200, 20));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Tanggal Pinjam");
        jPanel17.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 115, 30));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Tanggal Kembali");
        jPanel17.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 140, 30));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Status");
        jPanel17.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 10, 100, 30));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Denda");
        jPanel17.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 10, 100, 30));

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setText("RIWAYAT PEMINJAMAN DAN PENGEMBALIAN");

        javax.swing.GroupLayout panelRiwayatContainerLayout = new javax.swing.GroupLayout(panelRiwayatContainer);
        panelRiwayatContainer.setLayout(panelRiwayatContainerLayout);
        panelRiwayatContainerLayout.setHorizontalGroup(
            panelRiwayatContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 4798, Short.MAX_VALUE)
        );
        panelRiwayatContainerLayout.setVerticalGroup(
            panelRiwayatContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 268, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(panelRiwayatContainer);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(136, 136, 136))
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.add(jPanel18, new java.awt.GridBagConstraints());

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setLayout(new java.awt.GridBagLayout());
        jPanel5.add(jPanel10, new java.awt.GridBagConstraints());

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setLayout(new java.awt.GridBagLayout());
        jPanel5.add(jPanel14, new java.awt.GridBagConstraints());

        jPanel6.add(jPanel5, java.awt.BorderLayout.CENTER);

        jScrollPane1.setViewportView(jPanel6);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jButton1.setBackground(new java.awt.Color(0, 153, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
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

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 909, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        new RiwayatPeminjam().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        new DashboardPeminjam().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        new ProfilPeminjam().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        new DashboardDenda().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new RiwayatPeminjam().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel panelRiwayatContainer;
    // End of variables declaration//GEN-END:variables
}
