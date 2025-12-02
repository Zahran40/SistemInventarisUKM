
package Admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import Model.Denda;
import Service.DendaService;

/**
 * Dashboard Admin untuk monitor dan konfirmasi pembayaran denda
 */
public class ManajemenDenda extends javax.swing.JFrame {
    
    private DendaService dendaService;
    private JPanel dataPanel;
    private JLabel lblAmount; // Simpan referensi untuk update
    
    public ManajemenDenda() {
        try {
            this.dendaService = new DendaService();
            
            initComponents();
            loadDendaData();
            
            setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
            setLocationRelativeTo(null);
            setVisible(true); // PENTING: Set visible di constructor
        } catch (Exception e) {
            System.out.println("ERROR ManajemenDenda constructor: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error membuka Manajemen Denda:\n" + e.getMessage() + 
                "\n\nPastikan tabel 'denda' sudah dibuat di database!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Manajemen Denda - Admin");
        setLayout(new BorderLayout());
        
        // Sidebar Panel
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Dashboard button
        JButton btnDashboard = createSidebarButton("Dashboard", new Color(0, 204, 0));
        btnDashboard.addActionListener(e -> {
            new DashboardAdmin().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnDashboard);
        sidebarPanel.add(Box.createVerticalStrut(6));
        
        // Tambah Barang
        JButton btnTambah = createSidebarButton("Tambah Barang", new Color(60, 63, 65));
        btnTambah.addActionListener(e -> {
            new tambahbarang().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnTambah);
        sidebarPanel.add(Box.createVerticalStrut(6));
        
        // Edit Barang
        JButton btnEdit = createSidebarButton("Edit Barang", new Color(60, 63, 65));
        btnEdit.addActionListener(e -> {
            new editbarang().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnEdit);
        sidebarPanel.add(Box.createVerticalStrut(6));
        
        // Hapus Barang
        JButton btnHapus = createSidebarButton("Hapus Barang", new Color(60, 63, 65));
        btnHapus.addActionListener(e -> {
            new hapusbarang().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnHapus);
        sidebarPanel.add(Box.createVerticalStrut(6));
        
        // Log Peminjaman
        JButton btnLog = createSidebarButton("Log Peminjaman", new Color(60, 63, 65));
        btnLog.addActionListener(e -> {
            new LogPeminjaman().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnLog);
        sidebarPanel.add(Box.createVerticalStrut(6));
        
        // Request Peminjaman
        JButton btnReqPeminjaman = createSidebarButton("Request Peminjaman", new Color(60, 63, 65));
        btnReqPeminjaman.addActionListener(e -> {
            new RequestPeminjaman().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnReqPeminjaman);
        sidebarPanel.add(Box.createVerticalStrut(6));
        
        // Request Pengembalian
        JButton btnReqPengembalian = createSidebarButton("Request Pengembalian", new Color(60, 63, 65));
        btnReqPengembalian.addActionListener(e -> {
            new RequestPengembalian().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnReqPengembalian);
        sidebarPanel.add(Box.createVerticalStrut(6));
        
        // Manajemen Denda (current page - highlighted)
        JButton btnDenda = createSidebarButton("Manajemen Denda", new Color(60, 63, 65));
        btnDenda.setEnabled(false); // Disable karena sedang di halaman ini
        sidebarPanel.add(btnDenda);
        sidebarPanel.add(Box.createVerticalStrut(18));
        
        // Logout button
        JButton btnLogout = createSidebarButton("Log Out", new Color(255, 51, 51));
        btnLogout.addActionListener(e -> {
            Utils.UserSession.getInstance().clearSession();
            JOptionPane.showMessageDialog(this, "Berhasil logout!");
            new Register.LoginPage().setVisible(true);
            this.dispose();
        });
        sidebarPanel.add(btnLogout);
        
        add(sidebarPanel, BorderLayout.WEST);
        
        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Top Panel - Header with green background
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(0, 204, 0)); // Green like other admin pages
        topPanel.setPreferredSize(new Dimension(getWidth(), 80));
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        
        JLabel lblTitle = new JLabel("SiUkm - Sistem Inventaris UKM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        topPanel.add(lblTitle);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Content Panel - Denda summary and table
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(0, 204, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Summary Panel (red box)
        JPanel summaryPanel = new JPanel();
        summaryPanel.setBackground(new Color(220, 53, 69));
        summaryPanel.setPreferredSize(new Dimension(getWidth(), 100));
        summaryPanel.setLayout(null);
        
        JLabel lblSummaryTitle = new JLabel("MANAJEMEN DENDA");
        lblSummaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblSummaryTitle.setForeground(Color.WHITE);
        lblSummaryTitle.setBounds(20, 10, 400, 30);
        summaryPanel.add(lblSummaryTitle);
        
        JLabel lblTotal = new JLabel("Total Denda Belum Dibayar (Semua User):");
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setBounds(20, 45, 300, 25);
        summaryPanel.add(lblTotal);
        
        int totalDenda = dendaService.getTotalSemuaDendaBelumBayar();
        lblAmount = new JLabel(String.format("Rp %,d", totalDenda)); // Assign ke instance variable
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAmount.setForeground(Color.YELLOW);
        lblAmount.setBounds(330, 42, 300, 30);
        summaryPanel.add(lblAmount);
        
        contentPanel.add(summaryPanel, BorderLayout.NORTH);
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
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
        
        tablePanel.add(headerPanel, BorderLayout.NORTH);
        
        // Data Panel
        dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(dataPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Refresh button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setBackground(new Color(0, 123, 255));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> loadDendaData());
        buttonPanel.add(btnRefresh);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JButton createSidebarButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 32));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }
    
    private void loadDendaData() {
        try {
            dataPanel.removeAll();
            
            List<Denda> listDenda = dendaService.getAllDendaBelumBayar();
            
            System.out.println("DEBUG loadDendaData: Found " + listDenda.size() + " denda records");
            
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
        } catch (Exception e) {
            System.out.println("ERROR loadDendaData: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error memuat data denda: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
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
        try {
            int totalDenda = dendaService.getTotalSemuaDendaBelumBayar();
            lblAmount.setText(String.format("Rp %,d", totalDenda));
        } catch (Exception e) {
            System.out.println("ERROR updateTotalDenda: " + e.getMessage());
            e.printStackTrace();
        }
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
