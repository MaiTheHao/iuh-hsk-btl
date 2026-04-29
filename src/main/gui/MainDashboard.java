package main.gui;

import javax.swing.*;
import java.awt.*;
import main.App;
import main.util.AppContext;
import main.entity.NhanVien;

public class MainDashboard extends JPanel {
    private App mainFrame;
    
    public MainDashboard(App mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
    }

    public void renderUI() {
        removeAll();
        
        // 1. Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(45, 52, 54));
        header.setPreferredSize(new Dimension(0, 60));
        
        JLabel lblTitle = new JLabel("  COFFEE HOUSE - HỆ THỐNG TẠI QUẦY", JLabel.LEFT);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(lblTitle, BorderLayout.WEST);

        // Right Header with User Info
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightHeader.setOpaque(false);

        NhanVien user = AppContext.getInstance().getCurrentUser();
        String userName = (user != null) ? user.getTen() : "Guest";
        JLabel lblUser = new JLabel("Xin chào, " + userName);
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        rightHeader.add(lblUser);

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFocusPainted(false);
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            AppContext.getInstance().logout();
            mainFrame.showLoginPage();
        });
        rightHeader.add(btnLogout);
        header.add(rightHeader, BorderLayout.EAST);

        // 2. Tabs với Phân quyền
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.setFocusable(false);

        tabs.addTab("Bán hàng", createPlaceholder("Giao diện Lên đơn / Bán hàng tại quầy"));

        if (AppContext.getInstance().isAdmin()) {
            tabs.addTab("Sản phẩm", createPlaceholder("Quản lý danh sách Sản phẩm"));
            tabs.addTab("Nhân viên", createPlaceholder("Quản lý danh sách Nhân viên"));
            tabs.addTab("Hóa đơn", createPlaceholder("Lịch sử giao dịch & Hóa đơn"));
        }

        tabs.addTab("Thống kê", createPlaceholder("Báo cáo doanh thu & Kho"));

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel createPlaceholder(String title) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        lbl.setForeground(Color.LIGHT_GRAY);
        p.add(lbl);
        return p;
    }
}
