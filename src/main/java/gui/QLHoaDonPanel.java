package main.java.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Nguyễn Lương Triều Vỹ
 */
import main.java.dao.HoaDonDAO;
import main.java.dto.HoaDonGetListCriteria;
import main.java.dao.ChiTietHDDAO;
import main.java.entity.HoaDon;
import main.java.entity.ChiTietHD;
import main.java.enumeration.TrangThaiHD;
import main.java.util.AppColor;

public class QLHoaDonPanel extends JPanel {

    private JTable tableHoaDon, tableChiTiet;
    private DefaultTableModel tableModelHoaDon, tableModelChiTiet;
    private JTextField txtTimKiem;
    private JComboBox<String> cboTrangThai;
    private JButton btnHuyHoaDon;
    private List<HoaDon> dsHoaDon = new ArrayList<>();
    
    public QLHoaDonPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
        
        init();
        bindEvents();
        loadData();
    }

    private void init() {
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelTimKiem.setOpaque(false);
        panelTimKiem.add(new JLabel("Tìm kiếm (Mã HD/SĐT Khách):"));
        txtTimKiem = new JTextField(20);
        panelTimKiem.add(txtTimKiem);

        panelTimKiem.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Tất cả", "Chờ thanh toán", "Đã thanh toán", "Đã hủy"});
        cboTrangThai.setPreferredSize(new Dimension(150, 30));
        panelTimKiem.add(cboTrangThai);

        btnHuyHoaDon = new JButton("Hủy hóa đơn");
        btnHuyHoaDon.setBackground(AppColor.ERROR);
        btnHuyHoaDon.setForeground(Color.WHITE);
        panelTimKiem.add(btnHuyHoaDon);

        add(panelTimKiem, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.5);

        tableModelHoaDon = new DefaultTableModel(new String[]{"Mã hóa đơn", "Ngày lập", "Nhân viên", "Khách hàng", "Tổng tiền", "VAT", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableHoaDon = new JTable(tableModelHoaDon);
        tableHoaDon.setRowHeight(30);
        
        JPanel pnlHoaDon = new JPanel(new BorderLayout());
        pnlHoaDon.setBorder(BorderFactory.createTitledBorder(" Danh sách hóa đơn "));
        pnlHoaDon.add(new JScrollPane(tableHoaDon), BorderLayout.CENTER);
        splitPane.setTopComponent(pnlHoaDon);

        tableModelChiTiet = new DefaultTableModel(new String[]{"STT", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableChiTiet = new JTable(tableModelChiTiet);
        tableChiTiet.setRowHeight(30);
        
        JPanel pnlChiTiet = new JPanel(new BorderLayout());
        pnlChiTiet.setBorder(BorderFactory.createTitledBorder(" Chi tiết hóa đơn "));
        pnlChiTiet.add(new JScrollPane(tableChiTiet), BorderLayout.CENTER);
        splitPane.setBottomComponent(pnlChiTiet);

        add(splitPane, BorderLayout.CENTER);
    }

    private void bindEvents() {
        txtTimKiem.addActionListener(e -> loadData());
        cboTrangThai.addActionListener(e -> loadData());
        btnHuyHoaDon.addActionListener(e -> handleHuyHoaDon());

        tableHoaDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableHoaDon.getSelectedRow();
                if (row >= 0) loadDetails(dsHoaDon.get(row).getMa());
            }
        });
    }

    private void loadData() {
        tableModelHoaDon.setRowCount(0);
        HoaDonGetListCriteria criteria = new HoaDonGetListCriteria();
        criteria.setTuKhoa(txtTimKiem.getText().trim());
        
        String selectedStatus = (String) cboTrangThai.getSelectedItem();
        if (selectedStatus != null && !selectedStatus.equals("Tất cả")) {
            if (selectedStatus.equals("Chờ thanh toán")) criteria.setTrangThai(TrangThaiHD.PENDING);
            else if (selectedStatus.equals("Đã thanh toán")) criteria.setTrangThai(TrangThaiHD.PAID);
            else if (selectedStatus.equals("Đã hủy")) criteria.setTrangThai(TrangThaiHD.CANCELLED);
        }
        
        dsHoaDon = HoaDonDAO.getInstance().getList(criteria).data();
        
        for (HoaDon hd : dsHoaDon) {
            tableModelHoaDon.addRow(new Object[]{
                hd.getMa(),
                hd.getNgayLap().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                hd.getNhanVien() != null ? hd.getNhanVien().getTen() : "",
                hd.getKhachHang() != null ? hd.getKhachHang().getTen() : "Khách vãng lai",
                String.format("%,.0f", hd.getTongTien()),
                String.format("%,.0f", hd.getVat()),
                hd.getTrangThai() == TrangThaiHD.PENDING ? "Chờ thanh toán" : 
                hd.getTrangThai() == TrangThaiHD.PAID ? "Đã thanh toán" : "Đã hủy"
            });
        }
    }

    private void loadDetails(String maHD) {
        tableModelChiTiet.setRowCount(0);
        List<ChiTietHD> details = ChiTietHDDAO.getInstance().getByMaHD(maHD);
        int stt = 1;
        for (ChiTietHD ct : details) {
            tableModelChiTiet.addRow(new Object[]{
                stt++,
                ct.getSanPham().getTen(),
                String.format("%,.0f", ct.getDonGia()),
                ct.getSoLuong(),
                String.format("%,.0f", ct.getThanhTien())
            });
        }
    }

    private void handleHuyHoaDon() {
        int row = tableHoaDon.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để hủy!");
            return;
        }

        String ma = (String) tableModelHoaDon.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Hủy hóa đơn " + ma + "? Chức năng này không thể hoàn tác!", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (HoaDonDAO.getInstance().updateTrangThai(ma, main.java.enumeration.TrangThaiHD.CANCELLED)) {
                JOptionPane.showMessageDialog(this, "Đã hủy hóa đơn thành công!");
                loadData();
                tableModelChiTiet.setRowCount(0);
            }
        }
    }
}
