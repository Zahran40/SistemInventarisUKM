package Admin;

import Service.DendaService;
import Model.Denda;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dashboard Admin untuk monitor dan konfirmasi pembayaran denda
 */
public class ManajemenDenda extends javax.swing.JFrame {
    
    private DendaService dendaService;
    private JPanel dataPanel;
    
    public ManajemenDenda() {
        this.dendaService = new DendaService();
        
        initComponents();
        loadDendaData();
        
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Manajemen Denda - Admin");
        setLayout(new BorderLayout());
        
        // Top Panel - Summary
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(220, 53, 69)); // Red
        topPanel.setPreferredSize(new Dimension(getWidth(), 120));
        topPanel.setLayout(null);
        
        JLabel lblTitle = new JLabel("MANAJEMEN DENDA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(50, 20, 400, 30);
        topPanel.add(lblTitle);
        
        JLabel lblTotal = new JLabel("Total Denda Belum Dibayar (Semua User):");
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setBounds(50, 60, 300, 25);
        topPanel.add(lblTotal);
        
        int totalDenda = dendaService.getTotalSemuaDendaBelumBayar();
        JLabel lblAmount = new JLabel(String.format("Rp %,d", totalDenda));
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAmount.setForeground(Color.YELLOW);
        lblAmount.setBounds(360, 55, 300, 30);
        topPanel.add(lblAmount);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - List Denda
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new GridLayout(1, 7, 10, 0));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 40));
        headerPanel.setBackground(Color.LIGHT_GRAY);
        
        String[] headers = {"Nama User", "Nama Barang", "Hari Telat", "Jumlah Denda", "Tanggal Hitung", "Status", "Aksi"};
        for (String header : headers) {
            JLabel lbl = new JLabel(header, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            headerPanel.add(lbl);
        }
        
        centerPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Data Panel
        dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(dataPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel - Buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JButton btnKembali = new JButton("Kembali");
        btnKembali.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKembali.addActionListener(e -> {
            new DashboardAdmin().setVisible(true);
            this.dispose();
        });
        bottomPanel.add(btnKembali);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.addActionListener(e -> loadDendaData());
        bottomPanel.add(btnRefresh);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadDendaData() {
        dataPanel.removeAll();
        
        List<Denda> listDenda = dendaService.getAllDendaBelumBayar();
        
        if (listDenda.isEmpty()) {
            JLabel lblEmpty = new JLabel("Tidak ada denda belum bayar", SwingConstants.CENTER);
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblEmpty.setForeground(Color.GRAY);
            dataPanel.add(lblEmpty);
        } else {
            for (Denda denda : listDenda) {
                JPanel rowPanel = new JPanel(new GridLayout(1, 7, 10, 0));
                rowPanel.setPreferredSize(new Dimension(getWidth() - 50, 50));
                rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
                
                JLabel lblUser = new JLabel(denda.getNamaUser(), SwingConstants.CENTER);
                JLabel lblBarang = new JLabel(denda.getNamaBarang(), SwingConstants.CENTER);
                JLabel lblHari = new JLabel(String.valueOf(denda.getHariTelat()) + " hari", SwingConstants.CENTER);
                JLabel lblJumlah = new JLabel(denda.getJumlahDendaFormatted(), SwingConstants.CENTER);
                lblJumlah.setForeground(Color.RED);
                lblJumlah.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                JLabel lblTanggal = new JLabel(denda.getTanggalHitung().toString(), SwingConstants.CENTER);
                JLabel lblStatus = new JLabel("BELUM BAYAR", SwingConstants.CENTER);
                lblStatus.setForeground(Color.RED);
                lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
                
                // Tombol Konfirmasi Bayar
                JButton btnBayar = new JButton("Konfirmasi Bayar");
                btnBayar.setBackground(new Color(40, 167, 69)); // Green
                btnBayar.setForeground(Color.WHITE);
                btnBayar.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btnBayar.addActionListener(e -> konfirmasiBayar(denda));
                
                rowPanel.add(lblUser);
                rowPanel.add(lblBarang);
                rowPanel.add(lblHari);
                rowPanel.add(lblJumlah);
                rowPanel.add(lblTanggal);
                rowPanel.add(lblStatus);
                rowPanel.add(btnBayar);
                
                dataPanel.add(rowPanel);
            }
        }
        
        dataPanel.revalidate();
        dataPanel.repaint();
        
        updateTotalDenda();
    }
    
    private void konfirmasiBayar(Denda denda) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Konfirmasi pembayaran denda?\n\n" +
            "User: " + denda.getNamaUser() + "\n" +
            "Barang: " + denda.getNamaBarang() + "\n" +
            "Jumlah: " + denda.getJumlahDendaFormatted(),
            "Konfirmasi Pembayaran",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean sukses = dendaService.bayarDenda(denda.getIdDenda());
            
            if (sukses) {
                JOptionPane.showMessageDialog(this, 
                    "Pembayaran denda berhasil dikonfirmasi!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                loadDendaData(); // Refresh data
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Gagal konfirmasi pembayaran!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateTotalDenda() {
        Container contentPane = getContentPane();
        JPanel topPanel = (JPanel) contentPane.getComponent(0);
        JLabel lblAmount = (JLabel) topPanel.getComponent(3);
        
        int totalDenda = dendaService.getTotalSemuaDendaBelumBayar();
        lblAmount.setText(String.format("Rp %,d", totalDenda));
    }
    
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ManajemenDenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(() -> {
            new ManajemenDenda().setVisible(true);
        });
    }
}
