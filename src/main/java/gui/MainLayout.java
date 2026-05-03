package main.java.gui;

import javax.swing.*;
import java.awt.*;

import main.java.util.AppContext;
import main.java.entity.NhanVien;
import main.java.App;
import main.java.util.ImageUtil;
import main.java.enumeration.LoaiNV;

/**
 * @author: Mai Thế Hào
 */
public class MainLayout extends JPanel {
    
    private App mainFrame;
    private BanHangPanel pnlBanHang;
    private QLSanPhamPanel pnlSanPham;
    private QLLoaiSanPham pnlLoaiSP;
    private QLNhanVien pnlNhanVien;
    private QLHoaDonPanel pnlHoaDon;
    private ThongKePanel pnlThongKe;
    private QLKhachHangPanel pnlKhachHang;
    
    public MainLayout(App mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
    }

    public void renderUI() {
        removeAll();

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(45, 52, 54));
        panelHeader.setPreferredSize(new Dimension(0, 60));
        
        JLabel lblTitle = new JLabel("  COFFEE HOUSE - HỆ THỐNG TẠI QUẦY", JLabel.LEFT);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelHeader.add(lblTitle, BorderLayout.WEST);

        JPanel panelUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelUser.setOpaque(false);

        NhanVien user = AppContext.getInstance().getCurrentUser();
        if (user != null) {
            String roleStr = (user.getLoai() == LoaiNV.ADMIN) ? "Quản lý" : "Nhân viên";
            JLabel lblUserInfo = new JLabel(user.getTen() + " - " + roleStr);
            lblUserInfo.setForeground(Color.WHITE);
            lblUserInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
            panelUser.add(lblUserInfo);

            ImageIcon avatar = ImageUtil.createIcon(user.getAnh(), 40, 40);
            if (avatar != null) {
                JLabel lblAvatar = new JLabel(avatar);
                lblAvatar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                panelUser.add(lblAvatar);
            }
        }

        JButton btnDangXuat = new JButton("Đăng xuất");
        btnDangXuat.setBackground(new Color(231, 76, 60));
        btnDangXuat.setForeground(Color.WHITE);
        btnDangXuat.addActionListener(e -> {
            AppContext.getInstance().logout();
            mainFrame.showLoginPage();
        });
        panelUser.add(btnDangXuat);
        panelHeader.add(panelUser, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 16));

        if (pnlBanHang == null) pnlBanHang = new BanHangPanel();
        taoTab(tabs, "Bán hàng", pnlBanHang);
        taoTab(tabs, "Khách hàng", taoPlaceholder());

        if (AppContext.getInstance().isAdmin()) {
            taoTab(tabs, "Sản phẩm", taoPlaceholder());
            taoTab(tabs, "Loại sản phẩm", taoPlaceholder());
            taoTab(tabs, "Nhân viên", taoPlaceholder());
            taoTab(tabs, "Hóa đơn", taoPlaceholder());
        }
        taoTab(tabs, "Thống kê", taoPlaceholder());

        tabs.addChangeListener(e -> {
            int i = tabs.getSelectedIndex();
            if (i != -1 && "placeholder".equals(tabs.getComponentAt(i).getName())) {
                String title = ((JLabel) tabs.getTabComponentAt(i)).getText();
                JPanel realPanel = null;
                switch (title) {
                    case "Sản phẩm":
                        if (pnlSanPham == null) pnlSanPham = new QLSanPhamPanel();
                        realPanel = pnlSanPham;
                        break;
                    case "Loại sản phẩm":
                        if (pnlLoaiSP == null) pnlLoaiSP = new QLLoaiSanPham();
                        realPanel = pnlLoaiSP;
                        break;
                    case "Nhân viên":
                        if (pnlNhanVien == null) pnlNhanVien = new QLNhanVien();
                        realPanel = pnlNhanVien;
                        break;
                    case "Hóa đơn":
                        if (pnlHoaDon == null) pnlHoaDon = new QLHoaDonPanel();
                        realPanel = pnlHoaDon;
                        break;
                    case "Thống kê":
                        if (pnlThongKe == null) pnlThongKe = new ThongKePanel();
                        realPanel = pnlThongKe;
                        break;
                    case "Khách hàng":
                        if (pnlKhachHang == null) pnlKhachHang = new QLKhachHangPanel();
                        realPanel = pnlKhachHang;
                        break;
                }
                if (realPanel != null) tabs.setComponentAt(i, realPanel);
            }
        });

        add(tabs, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel taoPlaceholder() {
        JPanel p = new JPanel(new BorderLayout());
        p.setName("placeholder");
        p.add(new JLabel("Đang tải dữ liệu...", JLabel.CENTER));
        return p;
    }

    private void taoTab(JTabbedPane tabs, String tieuDe, JPanel noiDung) {
        int index = tabs.getTabCount();
        tabs.addTab(null, noiDung);
        JLabel lbl = new JLabel(tieuDe, SwingConstants.CENTER);
        lbl.setPreferredSize(new Dimension(130, 50));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setTabComponentAt(index, lbl);
    }
}
