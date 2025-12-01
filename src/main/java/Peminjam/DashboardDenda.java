package Peminjam;

import Session.UserSession;
import Service.DendaService;
import Model.Denda;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dashboard Denda untuk Peminjam
 * Menampilkan denda belum bayar dan riwayat denda
 */
public class DashboardDenda extends javax.swing.JFrame {
    
    private int userId;
    private DendaService dendaService;
    
    public DashboardDenda() {
        // Check session
        UserSession session = UserSession.getInstance();
        if (session.getUserId() == 0) {
            JOptionPane.showMessageDialog(this, "Silakan login terlebih dahulu!");
            new Register.LoginPage().setVisible(true);
            this.dispose();
            return;
        }
        
        this.userId = session.getUserId();
        this.dendaService = new DendaService();
        
        initComponents();
        loadDendaData();
        
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Denda Saya");
        setLayout(new BorderLayout());
        
        // Top Panel - Summary
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(220, 53, 69)); // Red
        topPanel.setPreferredSize(new Dimension(getWidth(), 120));
        topPanel.setLayout(null);
        
        JLabel lblTitle = new JLabel("DENDA KETERLAMBATAN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(50, 20, 400, 30);
        topPanel.add(lblTitle);
        
        JLabel lblTotal = new JLabel("Total Denda Belum Bayar:");
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setBounds(50, 60, 200, 25);
        topPanel.add(lblTotal);
        
        int totalDenda = dendaService.getTotalDendaBelumBayar(userId);
        JLabel lblAmount = new JLabel(String.format("Rp %,d", totalDenda));
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAmount.setForeground(Color.YELLOW);
        lblAmount.setBounds(250, 55, 300, 30);
        topPanel.add(lblAmount);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - List Denda
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new GridLayout(1, 6, 10, 0));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 40));
        headerPanel.setBackground(Color.LIGHT_GRAY);
        
        String[] headers = {"Nama Barang", "Hari Telat", "Jumlah Denda", "Tanggal Hitung", "Status", "Tanggal Bayar"};
        for (String header : headers) {
            JLabel lbl = new JLabel(header, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            headerPanel.add(lbl);
        }
        
        centerPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Data Panel
        JPanel dataPanel = new JPanel();
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
            new DashboardPeminjam().setVisible(true);
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
        // Get data panel
        Container contentPane = getContentPane();
        JPanel centerPanel = (JPanel) contentPane.getComponent(1);
        JScrollPane scrollPane = (JScrollPane) centerPanel.getComponent(1);
        JPanel dataPanel = (JPanel) scrollPane.getViewport().getView();
        
        dataPanel.removeAll();
        
        List<Denda> listDenda = dendaService.getDendaByUser(userId);
        
        if (listDenda.isEmpty()) {
            JLabel lblEmpty = new JLabel("Tidak ada data denda", SwingConstants.CENTER);
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblEmpty.setForeground(Color.GRAY);
            dataPanel.add(lblEmpty);
        } else {
            for (Denda denda : listDenda) {
                JPanel rowPanel = new JPanel(new GridLayout(1, 6, 10, 0));
                rowPanel.setPreferredSize(new Dimension(getWidth() - 50, 40));
                rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
                
                JLabel lblBarang = new JLabel(denda.getNamaBarang(), SwingConstants.CENTER);
                JLabel lblHari = new JLabel(String.valueOf(denda.getHariTelat()), SwingConstants.CENTER);
                JLabel lblJumlah = new JLabel(denda.getJumlahDendaFormatted(), SwingConstants.CENTER);
                JLabel lblTanggal = new JLabel(denda.getTanggalHitung().toString(), SwingConstants.CENTER);
                JLabel lblStatus = new JLabel(denda.getStatusBayar().toUpperCase(), SwingConstants.CENTER);
                
                if (denda.isLunas()) {
                    lblStatus.setForeground(new Color(0, 153, 0)); // Green
                } else {
                    lblStatus.setForeground(Color.RED);
                    lblJumlah.setForeground(Color.RED);
                    lblJumlah.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
                
                String tanggalBayar = denda.getTanggalBayar() != null ? 
                    denda.getTanggalBayar().toString() : "-";
                JLabel lblTglBayar = new JLabel(tanggalBayar, SwingConstants.CENTER);
                
                rowPanel.add(lblBarang);
                rowPanel.add(lblHari);
                rowPanel.add(lblJumlah);
                rowPanel.add(lblTanggal);
                rowPanel.add(lblStatus);
                rowPanel.add(lblTglBayar);
                
                dataPanel.add(rowPanel);
            }
        }
        
        dataPanel.revalidate();
        dataPanel.repaint();
        
        // Update total
        updateTotalDenda();
    }
    
    private void updateTotalDenda() {
        Container contentPane = getContentPane();
        JPanel topPanel = (JPanel) contentPane.getComponent(0);
        JLabel lblAmount = (JLabel) topPanel.getComponent(3);
        
        int totalDenda = dendaService.getTotalDendaBelumBayar(userId);
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
            java.util.logging.Logger.getLogger(DashboardDenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(() -> {
            new DashboardDenda().setVisible(true);
        });
    }
}
