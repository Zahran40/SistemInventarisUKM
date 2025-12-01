/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package Peminjam;
import DAO.PeminjamanDAO;
import Model.RequestData;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 *
 * @author aldriknoel
 */
public class FormPengembalian extends javax.swing.JDialog {

    private RequestData dataBarang;
    private File selectedFile = null;
    private JButton btnUpload; 
    private javax.swing.JSpinner spnJumlah;

    public FormPengembalian(java.awt.Frame parent, RequestData data) {
        super(parent, true);
        this.dataBarang = data;
        initCustomComponents();
        setLocationRelativeTo(parent);
    }
    
    public FormPengembalian(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initCustomComponents();
    }

    private void initCustomComponents() {
        setTitle("Form Pengembalian Barang");
        setSize(450, 450); 
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 450, 60);
        headerPanel.setBackground(new Color(0, 204, 0));
        headerPanel.setLayout(null);
        add(headerPanel);
        
        JLabel lblTitle = new JLabel("Pengembalian Barang");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(20, 15, 250, 30);
        headerPanel.add(lblTitle);

        String nama = (dataBarang != null) ? dataBarang.getNamaBarang() : "Unknown";
        int totalPinjam = (dataBarang != null) ? dataBarang.getJumlah() : 0;
        
        JLabel lblNama = new JLabel("Barang: " + nama);
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNama.setBounds(30, 80, 300, 20);
        add(lblNama);
        
        JLabel lblJumlah = new JLabel("Jumlah yang dipinjam: " + totalPinjam + " unit");
        lblJumlah.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblJumlah.setBounds(30, 105, 300, 20);
        add(lblJumlah);
        
        JLabel lblInfo = new JLabel("(Semua barang akan dikembalikan)");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setBounds(30, 125, 300, 20);
        add(lblInfo);
        
        int yPos = 155;

        JLabel lblInstruksi = new JLabel("Upload foto kondisi barang saat ini:");
        lblInstruksi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInstruksi.setBounds(30, yPos, 350, 20);
        add(lblInstruksi);

        btnUpload = new JButton("Pilih Foto Barang...");
        btnUpload.setBounds(30, yPos + 30, 375, 40);
        btnUpload.setBackground(new Color(240, 240, 240));
        btnUpload.addActionListener(e -> pilihFile());
        add(btnUpload);

        JButton btnKirim = new JButton("Ajukan Pengembalian");
        btnKirim.setBounds(30, yPos + 130, 375, 40);
        btnKirim.setBackground(new Color(0, 153, 0));
        btnKirim.setForeground(Color.WHITE);
        btnKirim.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKirim.addActionListener(e -> prosesPengembalian());
        add(btnKirim);
        
        JButton btnBatal = new JButton("Batal");
        btnBatal.setBounds(160, yPos + 80, 100, 30);
        btnBatal.addActionListener(e -> dispose());
        add(btnBatal);
    }

    private void pilihFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Gambar (JPG, PNG)", "jpg", "png", "jpeg"));
        
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
            btnUpload.setText(selectedFile.getName()); 
            btnUpload.setBackground(new Color(200, 255, 200)); 
        }
    }

    private void prosesPengembalian() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Wajib upload bukti foto barang!");
            return;
        }
        
        // Otomatis kembalikan SEMUA yang dipinjam
        int jumlahKembali = dataBarang.getJumlah();

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Ajukan pengembalian " + jumlahKembali + " unit " + dataBarang.getNamaBarang() + "?", 
                "Konfirmasi", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            PeminjamanDAO dao = new PeminjamanDAO();
            boolean sukses = dao.ajukanPengembalian(dataBarang.getIdPeminjaman(), selectedFile, jumlahKembali);
            
            if (sukses) {
                JOptionPane.showMessageDialog(this, "Pengajuan berhasil dikirim!");
                dispose();
                java.awt.Window parent = this.getOwner();
                if (parent instanceof RiwayatPeminjam) {
                    ((RiwayatPeminjam) parent).loadRiwayatData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengirim data.");
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(FormPengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormPengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormPengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormPengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FormPengembalian dialog = new FormPengembalian(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
