package main.java.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import main.java.dao.ThongKeDAO;
import main.java.dto.DoanhThuNgayDTO;
import main.java.dto.ThongKeTongQuanDTO;
import main.java.dto.TopSanPhamDTO;

public class ThongKePanel extends JPanel {

    private JLabel lblDoanhThuNgay, lblSoHoaDonNgay, lblSpSapHet, lblDoanhThuThang;
    private JTable tableDoanhThu, tableTopSP;
    private DefaultTableModel tableModelDoanhThu, tableModelTopSP;
    private JTextField txtNgayBatDau, txtNgayKetThuc;
    private JButton btnThongKe;

    public ThongKePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
        
        init();
        bindEvents();
        loadData();
    }

    private void init() {
        JPanel panelThe = new JPanel(new GridLayout(1, 4, 15, 0));
        panelThe.setOpaque(false);
        panelThe.add(taoTheThongKe("DOANH THU HÔM NAY", "0 VND", new Color(46, 204, 113)));
        panelThe.add(taoTheThongKe("HÓA ĐƠN HÔM NAY", "0", new Color(52, 152, 219)));
        panelThe.add(taoTheThongKe("SẢN PHẨM SẮP HẾT", "0", new Color(231, 76, 60)));
        panelThe.add(taoTheThongKe("DOANH THU THÁNG", "0 VND", new Color(230, 126, 34)));
        add(panelThe, BorderLayout.NORTH);

        JPanel panelGiua = new JPanel(new BorderLayout(0, 10));
        panelGiua.setOpaque(false);

        JPanel panelBoLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBoLoc.setOpaque(false);
        panelBoLoc.setBorder(BorderFactory.createTitledBorder(" Bộ lọc thời gian "));

        panelBoLoc.add(new JLabel("Từ ngày (yyyy-MM-dd):"));
        txtNgayBatDau = new JTextField(LocalDate.now().minusDays(7).toString(), 10);
        panelBoLoc.add(txtNgayBatDau);

        panelBoLoc.add(new JLabel("Đến ngày (yyyy-MM-dd):"));
        txtNgayKetThuc = new JTextField(LocalDate.now().toString(), 10);
        panelBoLoc.add(txtNgayKetThuc);

        btnThongKe = new JButton("THỐNG KÊ");
        btnThongKe.setBackground(new Color(52, 152, 219));
        btnThongKe.setForeground(Color.WHITE);
        btnThongKe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelBoLoc.add(btnThongKe);
        panelGiua.add(panelBoLoc, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tableModelDoanhThu = new DefaultTableModel(new String[]{"Ngày", "Số hóa đơn", "Doanh thu (VND)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableDoanhThu = new JTable(tableModelDoanhThu);
        thietLapBang(tableDoanhThu);
        tabs.addTab("Doanh thu theo ngày", new JScrollPane(tableDoanhThu));

        tableModelTopSP = new DefaultTableModel(new String[]{"Mã SP", "Tên sản phẩm", "Số lượng bán", "Tổng tiền (VND)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableTopSP = new JTable(tableModelTopSP);
        thietLapBang(tableTopSP);
        tabs.addTab("Top sản phẩm bán chạy", new JScrollPane(tableTopSP));

        panelGiua.add(tabs, BorderLayout.CENTER);
        add(panelGiua, BorderLayout.CENTER);
    }

    private void bindEvents() {
        btnThongKe.addActionListener(e -> loadData());
    }

    private void loadData() {
        String startStr = txtNgayBatDau.getText().trim();
        String endStr = txtNgayKetThuc.getText().trim();

        LocalDateTime start, end;
        try {
            start = LocalDate.parse(startStr).atStartOfDay();
            end = LocalDate.parse(endStr).atTime(LocalTime.MAX);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày không hợp lệ! (yyyy-MM-dd)");
            return;
        }

        new SwingWorker<Void, Void>() {
            private ThongKeTongQuanDTO tongQuan;
            private List<DoanhThuNgayDTO> dsDoanhThu;
            private List<TopSanPhamDTO> dsTopSP;

            @Override
            protected Void doInBackground() {
                ThongKeDAO dao = ThongKeDAO.getInstance();
                tongQuan = dao.getTongQuan();
                dsDoanhThu = dao.getDoanhThuTheoNgay(start, end);
                dsTopSP = dao.getTopSanPhamBanChay(start, end, 10);
                return null;
            }

            @Override
            protected void done() {
                if (tongQuan != null) {
                    lblDoanhThuNgay.setText(String.format("%,.0f VND", tongQuan.getDoanhThuHomNay()));
                    lblSoHoaDonNgay.setText(String.valueOf(tongQuan.getSoHoaDonHomNay()));
                    lblSpSapHet.setText(String.valueOf(tongQuan.getSoSPCanNhap()));
                    lblDoanhThuThang.setText(String.format("%,.0f VND", tongQuan.getDoanhThuThangNay()));
                }

                tableModelDoanhThu.setRowCount(0);
                for (DoanhThuNgayDTO d : dsDoanhThu) {
                    tableModelDoanhThu.addRow(new Object[]{d.getNgay(), d.getSoHoaDon(), String.format("%,.0f", d.getDoanhThu())});
                }

                tableModelTopSP.setRowCount(0);
                for (TopSanPhamDTO t : dsTopSP) {
                    tableModelTopSP.addRow(new Object[]{t.getMaSP(), t.getTenSP(), t.getSoLuongDaBan(), String.format("%,.0f", t.getTongTien())});
                }
            }
        }.execute();
    }

    private JPanel taoTheThongKe(String tieuDe, String giaTri, Color mau) {
        JPanel the = new JPanel(new BorderLayout());
        the.setBackground(mau);
        the.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setForeground(new Color(255, 255, 255, 200));
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 12));
        the.add(lblTieuDe, BorderLayout.NORTH);

        JLabel lblGiaTri = new JLabel(giaTri);
        lblGiaTri.setForeground(Color.WHITE);
        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 22));
        the.add(lblGiaTri, BorderLayout.CENTER);

        if (tieuDe.contains("DOANH THU HÔM NAY")) lblDoanhThuNgay = lblGiaTri;
        else if (tieuDe.contains("HÓA ĐƠN HÔM NAY")) lblSoHoaDonNgay = lblGiaTri;
        else if (tieuDe.contains("SẢN PHẨM SẮP HẾT")) lblSpSapHet = lblGiaTri;
        else if (tieuDe.contains("DOANH THU THÁNG")) lblDoanhThuThang = lblGiaTri;

        return the;
    }

    private void thietLapBang(JTable bang) {
        bang.setRowHeight(30);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);
        if (bang.getColumnCount() >= 3) bang.getColumnModel().getColumn(bang.getColumnCount() - 1).setCellRenderer(right);
    }
}
