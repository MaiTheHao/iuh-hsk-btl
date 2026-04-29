package main.java.gui;

import javax.swing.*;
import java.awt.*;

import main.java.util.AppContext;
import main.java.entity.NhanVien;
import main.java.App;

public class MainLayout extends JPanel {
    private App mainFrame;
    
    public MainLayout(App mainFrame) {
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
        add(header, BorderLayout.NORTH);

        // Điều hướng
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabs.setFocusable(false);

        createTab(tabs, "Bán hàng", new SalePanel());

        if (AppContext.getInstance().isAdmin()) {
            createTab(tabs, "Sản phẩm", new ProductPanel());
            createTab(tabs, "Loại sản phẩm", new ProductTypePanel());
            createTab(tabs, "Nhân viên", new EmployeePanel());
            createTab(tabs, "Hóa đơn", new InvoicePanel());
        }

        createTab(tabs, "Thống kê", new StatisticPanel());

        add(tabs, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void createTab(JTabbedPane tabs, String title, JPanel content) {
        int index = tabs.getTabCount();
        tabs.addTab(null, content);
        
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setPreferredSize(new Dimension(130, 50));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        tabs.setTabComponentAt(index, lbl);
    }
}
