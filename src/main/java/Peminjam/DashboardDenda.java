package Peminjam;

import Utils.UserSession;
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
    private JLabel lblAmount; // Instance variable untuk total denda
    
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
        
        // Top Panel - Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 204, 0));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 67));
        headerPanel.setLayout(null);
        
        JLabel lblHeader = new JLabel("SiUkm - Sistem Inventaris UKM");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setBounds(0, 0, 800, 67);
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblHeader);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Container
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());
        
        // Navigation Panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridBagLayout());
        navPanel.setPreferredSize(new Dimension(getWidth(), 70));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.ipadx = 87;
        gbc.ipady = 8;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        // Catalog Button
        JButton btnCatalog = new JButton("Catalog");
        btnCatalog.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCatalog.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCatalog.addActionListener(e -> {
            new DashboardPeminjam().setVisible(true);
            this.dispose();
        });
        gbc.gridx = 0;
        gbc.insets = new Insets(14, 42, 24, 0);
        navPanel.add(btnCatalog, gbc);
        
        // Riwayat Button
        JButton btnRiwayat = new JButton("Riwayat");
        btnRiwayat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRiwayat.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnRiwayat.addActionListener(e -> {
            new RiwayatPeminjam().setVisible(true);
            this.dispose();
        });
        gbc.gridx = 1;
        gbc.ipadx = 85;
        gbc.insets = new Insets(14, 89, 24, 0);
        navPanel.add(btnRiwayat, gbc);
        
        // Profil Button
        JButton btnProfil = new JButton("Profil");
        btnProfil.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProfil.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnProfil.addActionListener(e -> {
            new ProfilPeminjam().setVisible(true);
            this.dispose();
        });
        gbc.gridx = 2;
        gbc.ipadx = 102;
        gbc.insets = new Insets(14, 93, 24, 0);
        navPanel.add(btnProfil, gbc);
        
        // Denda Saya Button (Current - highlighted)
        JButton btnDenda = new JButton("Denda Saya");
        btnDenda.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDenda.setBackground(new Color(0, 153, 0));
        btnDenda.setForeground(Color.WHITE);
        btnDenda.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        gbc.gridx = 3;
        gbc.ipadx = 70;
        gbc.insets = new Insets(14, 93, 24, 35);
        navPanel.add(btnDenda, gbc);
        
        mainContainer.add(navPanel, BorderLayout.NORTH);
        
        // Summary Panel - Total Denda
        JPanel summaryPanel = new JPanel();
        summaryPanel.setBackground(new Color(220, 53, 69)); // Red
        summaryPanel.setPreferredSize(new Dimension(getWidth(), 100));
        summaryPanel.setLayout(null);
        
        JLabel lblTitle = new JLabel("DENDA KETERLAMBATAN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(50, 15, 400, 30);
        summaryPanel.add(lblTitle);
        
        JLabel lblTotal = new JLabel("Total Denda Belum Bayar:");
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setBounds(50, 50, 200, 25);
        summaryPanel.add(lblTotal);
        
        int totalDenda = dendaService.getTotalDendaBelumBayar(userId);
        lblAmount = new JLabel(String.format("Rp %,d", totalDenda));
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAmount.setForeground(Color.YELLOW);
        lblAmount.setBounds(250, 48, 300, 30);
        summaryPanel.add(lblAmount);
        
        // Content Panel with Summary and Data
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(summaryPanel, BorderLayout.NORTH);
        
        // Center Panel - List Denda
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerListPanel = new JPanel(new GridLayout(1, 6, 10, 0));
        headerListPanel.setPreferredSize(new Dimension(getWidth(), 40));
        headerListPanel.setBackground(Color.LIGHT_GRAY);
        
        String[] headers = {"Nama Barang", "Hari Telat", "Jumlah Denda", "Tanggal Hitung", "Status", "Tanggal Bayar"};
        for (String header : headers) {
            JLabel lbl = new JLabel(header, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            headerListPanel.add(lbl);
        }
        
        centerPanel.add(headerListPanel, BorderLayout.NORTH);
        
        // Data Panel
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(dataPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        
        add(mainContainer, BorderLayout.CENTER);
        
        // Bottom Panel - Buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(getWidth(), 60));
        bottomPanel.setBackground(Color.WHITE);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setBackground(new Color(0, 153, 0));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> loadDendaData());
        bottomPanel.add(btnRefresh);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadDendaData() {
        // Get data panel - navigate through the correct component hierarchy
        Container contentPane = getContentPane();
        JPanel mainContainer = (JPanel) contentPane.getComponent(1); // mainContainer (CENTER of JFrame)
        JPanel contentPanel = (JPanel) mainContainer.getComponent(1); // contentPanel (CENTER of mainContainer, after navPanel[0])
        JPanel centerPanel = (JPanel) contentPanel.getComponent(1); // centerPanel (CENTER of contentPanel, after summaryPanel[0])
        JScrollPane scrollPane = (JScrollPane) centerPanel.getComponent(1); // scrollPane (CENTER of centerPanel, after headerListPanel[0])
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
