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

public class StatisticPanel extends JPanel {

    // # Khởi tạo biến thành phần
    private JLabel lblRevenueToday, lblInvoicesToday, lblLowStock, lblRevenueMonth;
    private JTable tblRevenue, tblTopProducts;
    private DefaultTableModel modelRevenue, modelTopProducts;
    private JTextField txtStartDate, txtEndDate;
    private JButton btnFilter;

    // # Constructor
    public StatisticPanel() {
        setupPanel();
        initComponents();
        refreshData();
    }
    
    // # Giao diện

    /**
     * Cấu hình cơ bản cho Panel
     */
    private void setupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
    }

    /**
     * Khởi tạo các thành phần giao diện
     */
    private void initComponents() {
        initSummaryCards();
        initFilterSection();
        initTabbedTables();
    }

    /**
     * Khởi tạo các thẻ tóm tắt (Cards) ở trên cùng
     */
    private void initSummaryCards() {
        JPanel pnlSummary = new JPanel(new GridLayout(1, 4, 15, 0));
        pnlSummary.setOpaque(false);

        pnlSummary.add(createCard("DOANH THU HÔM NAY", "0 VND", new Color(46, 204, 113)));
        pnlSummary.add(createCard("HÓA ĐƠN HÔM NAY", "0", new Color(52, 152, 219)));
        pnlSummary.add(createCard("SẢN PHẨM SẮP HẾT", "0", new Color(231, 76, 60)));
        pnlSummary.add(createCard("DOANH THU THÁNG", "0 VND", new Color(230, 126, 34)));

        add(pnlSummary, BorderLayout.NORTH);
    }

    /**
     * Khởi tạo phần bộ lọc thời gian
     */
    private void initFilterSection() {
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setOpaque(false);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlFilter.setOpaque(false);
        pnlFilter.setBorder(BorderFactory.createTitledBorder(" Bộ lọc thời gian "));

        pnlFilter.add(new JLabel("Từ ngày (yyyy-MM-dd):"));
        txtStartDate = new JTextField(LocalDate.now().minusDays(7).toString(), 10);
        pnlFilter.add(txtStartDate);

        pnlFilter.add(new JLabel("Đến ngày (yyyy-MM-dd):"));
        txtEndDate = new JTextField(LocalDate.now().toString(), 10);
        pnlFilter.add(txtEndDate);

        btnFilter = new JButton("THỐNG KÊ");
        btnFilter.setBackground(new Color(52, 152, 219));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnFilter.setFocusPainted(false);
        btnFilter.addActionListener(e -> refreshData());
        pnlFilter.add(btnFilter);

        pnlCenter.add(pnlFilter, BorderLayout.NORTH);
        add(pnlCenter, BorderLayout.CENTER);
    }

    /**
     * Khởi tạo các bảng dữ liệu trong TabbedPane
     */
    private void initTabbedTables() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Tab Doanh thu
        String[] colRevenue = {"Ngày", "Số hóa đơn", "Doanh thu (VND)"};
        modelRevenue = new DefaultTableModel(colRevenue, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRevenue = new JTable(modelRevenue);
        setupTableStyle(tblRevenue);
        tabs.addTab("Doanh thu theo ngày", new JScrollPane(tblRevenue));

        // Tab Top sản phẩm
        String[] colProducts = {"Mã SP", "Tên sản phẩm", "Số lượng bán", "Tổng tiền (VND)"};
        modelTopProducts = new DefaultTableModel(colProducts, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTopProducts = new JTable(modelTopProducts);
        setupTableStyle(tblTopProducts);
        tabs.addTab("Top sản phẩm bán chạy", new JScrollPane(tblTopProducts));

        JPanel pnlCenter = (JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        pnlCenter.add(tabs, BorderLayout.CENTER);
    }

    // # Tương tác dữ liệu

    /**
     * Làm mới dữ liệu thống kê từ database
     */
    private void refreshData() {
        String startStr = txtStartDate.getText().trim();
        String endStr = txtEndDate.getText().trim();

        LocalDateTime start, end;
        try {
            start = LocalDate.parse(startStr).atStartOfDay();
            end = LocalDate.parse(endStr).atTime(LocalTime.MAX);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ! (Dùng yyyy-MM-dd)");
            return;
        }

        new SwingWorker<Void, Void>() {
            private ThongKeTongQuanDTO summary;
            private List<DoanhThuNgayDTO> revenueData;
            private List<TopSanPhamDTO> topProducts;

            @Override
            protected Void doInBackground() {
                ThongKeDAO dao = ThongKeDAO.getInstance();
                summary = dao.getTongQuan();
                revenueData = dao.getDoanhThuTheoNgay(start, end);
                topProducts = dao.getTopSanPhamBanChay(start, end, 10);
                return null;
            }

            @Override
            protected void done() {
                if (summary != null) {
                    lblRevenueToday.setText(String.format("%,.0f VND", summary.getDoanhThuHomNay()));
                    lblInvoicesToday.setText(String.valueOf(summary.getSoHoaDonHomNay()));
                    lblLowStock.setText(String.valueOf(summary.getSoSPCanNhap()));
                    lblRevenueMonth.setText(String.format("%,.0f VND", summary.getDoanhThuThangNay()));
                }

                modelRevenue.setRowCount(0);
                for (DoanhThuNgayDTO dto : revenueData) {
                    modelRevenue.addRow(new Object[]{
                        dto.getNgay(),
                        dto.getSoHoaDon(),
                        String.format("%,.0f", dto.getDoanhThu())
                    });
                }

                modelTopProducts.setRowCount(0);
                for (TopSanPhamDTO dto : topProducts) {
                    modelTopProducts.addRow(new Object[]{
                        dto.getMaSP(),
                        dto.getTenSP(),
                        dto.getSoLuongDaBan(),
                        String.format("%,.0f", dto.getTongTien())
                });
            }
        }
    }.execute();
}

    // # Utils

    /**
     * Tạo một thẻ
     */
    private JPanel createCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(200, 100));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(new Color(255, 255, 255, 200));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card.add(lblTitle, BorderLayout.NORTH);

        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        card.add(lblValue, BorderLayout.CENTER);

        if (title.contains("DOANH THU HÔM NAY")) lblRevenueToday = lblValue;
        else if (title.contains("HÓA ĐƠN HÔM NAY")) lblInvoicesToday = lblValue;
        else if (title.contains("SẢN PHẨM SẮP HẾT")) lblLowStock = lblValue;
        else if (title.contains("DOANH THU THÁNG")) lblRevenueMonth = lblValue;

        return card;
    }

    /**
     * Thiết lập chung cho table
     */
    private void setupTableStyle(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        
        if (table.getColumnCount() >= 3) {
            table.getColumnModel().getColumn(table.getColumnCount() - 1).setCellRenderer(rightRenderer);
        }
    }
}

