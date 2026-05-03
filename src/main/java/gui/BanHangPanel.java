package main.java.gui;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import main.java.dao.*;
import main.java.entity.*;
import main.java.enumeration.TrangThaiHD;
import main.java.util.AppContext;
import main.java.util.AppRegex;
import main.java.util.ImageUtil;
import main.java.util.AppColor;
import main.java.dto.SanPhamGetListCriteria;
import main.java.dto.LoaiSPGetListCriteria;

/**
 * @author: Mai Thế Hào
 */
public class BanHangPanel extends JPanel {

    private JTable tableSanPham, tableGioHang;
    private DefaultTableModel tableModelSanPham, tableModelGioHang;
    private JTextField txtTimKiem, txtSdtKhach, txtTenKhach;
    private JComboBox<String> cboLoaiSP;
    private JLabel lblTongCong, lblThue, lblTongThanhToan, lblGiamGia, lblDiemHienCo;
    private JCheckBox chkDungDiem;
    private List<SanPham> dsSanPham = new ArrayList<>();
    private List<ChiTietHD> dsGioHang = new ArrayList<>();
    private KhachHang khachHangHienTai = null;

    public BanHangPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        init();
        bindEvents();
        
        napLoaiSP();
        loadData();
    }

    private void init() {
        JPanel panelTrai = new JPanel(new BorderLayout(5, 5));
        panelTrai.setPreferredSize(new Dimension(800, 0));
        
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTimKiem.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(20);
        panelTimKiem.add(txtTimKiem);
        panelTimKiem.add(new JLabel("Loại:"));
        cboLoaiSP = new JComboBox<>(new String[]{"Tất cả"});
        panelTimKiem.add(cboLoaiSP);
        panelTrai.add(panelTimKiem, BorderLayout.NORTH);

        tableModelSanPham = new DefaultTableModel(new String[]{"Ảnh", "Mã", "Tên", "Giá", "Tồn kho"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Icon.class : Object.class;
            }
        };
        tableSanPham = new JTable(tableModelSanPham) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                try {
                    int sl = (int) getValueAt(row, 4);
                    if (sl <= 0) {
                        if (!isRowSelected(row)) c.setForeground(Color.RED);
                    } else {
                        if (!isRowSelected(row)) {
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                        }
                    }
                } catch (Exception e) {}
                return c;
            }
        };
        tableSanPham.setRowHeight(80);
        tableSanPham.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        panelTrai.add(new JScrollPane(tableSanPham), BorderLayout.CENTER);
        add(panelTrai, BorderLayout.WEST);

        JPanel panelPhai = new JPanel(new BorderLayout(5, 5));
        
        JPanel panelKhachHang = new JPanel();
        panelKhachHang.setLayout(new BoxLayout(panelKhachHang, BoxLayout.Y_AXIS));
        panelKhachHang.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(" Thông tin khách hàng "),
            BorderFactory.createEmptyBorder(5, 10, 10, 10)
        ));

        Dimension labelSize = new Dimension(100, 30);

        JPanel row1 = new JPanel(new BorderLayout(5, 0));
        row1.setOpaque(false);
        JLabel lblSdt = new JLabel("Số điện thoại:");
        lblSdt.setPreferredSize(labelSize);
        row1.add(lblSdt, BorderLayout.WEST);
        txtSdtKhach = new JTextField();
        row1.add(txtSdtKhach, BorderLayout.CENTER);
        JButton btnTimKhach = new JButton("Tìm");
        btnTimKhach.addActionListener(e -> handleTimKhachHang());
        row1.add(btnTimKhach, BorderLayout.EAST);
        panelKhachHang.add(row1);

        panelKhachHang.add(Box.createVerticalStrut(5));

        JPanel row2 = new JPanel(new BorderLayout(5, 0));
        row2.setOpaque(false);
        JLabel lblTen = new JLabel("Tên khách:");
        lblTen.setPreferredSize(labelSize);
        row2.add(lblTen, BorderLayout.WEST);
        txtTenKhach = new JTextField();
        txtTenKhach.setEditable(false);
        row2.add(txtTenKhach, BorderLayout.CENTER);
        panelKhachHang.add(row2);

        panelKhachHang.add(Box.createVerticalStrut(5));

        JPanel row3 = new JPanel(new BorderLayout(5, 0));
        row3.setOpaque(false);
        lblDiemHienCo = new JLabel("Điểm hiện có: 0");
        lblDiemHienCo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        row3.add(lblDiemHienCo, BorderLayout.WEST);
        chkDungDiem = new JCheckBox("Dùng điểm (100đ/100đ)");
        chkDungDiem.setOpaque(false);
        chkDungDiem.setEnabled(false);
        row3.add(chkDungDiem, BorderLayout.EAST);
        panelKhachHang.add(row3);

        panelPhai.add(panelKhachHang, BorderLayout.NORTH);

        tableModelGioHang = new DefaultTableModel(new String[]{"Sản phẩm", "Đơn giá", "SL", "Thành tiền", "Xóa"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 2; }
        };
        tableGioHang = new JTable(tableModelGioHang);
        tableGioHang.setRowHeight(30);
        
        tableGioHang.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setForeground(Color.RED);
                setFont(c.getFont().deriveFont(Font.BOLD));
                return c;
            }
        });

        panelPhai.add(new JScrollPane(tableGioHang), BorderLayout.CENTER);

        JPanel panelThanhToan = new JPanel(new GridLayout(6, 2, 5, 5));
        panelThanhToan.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        panelThanhToan.add(new JLabel("Tổng cộng:"));
        lblTongCong = new JLabel("0.0", JLabel.RIGHT);
        panelThanhToan.add(lblTongCong);

        panelThanhToan.add(new JLabel("VAT (10%):"));
        lblThue = new JLabel("0.0", JLabel.RIGHT);
        panelThanhToan.add(lblThue);

        panelThanhToan.add(new JLabel("Chiết khấu (điểm):"));
        lblGiamGia = new JLabel("0.0", JLabel.RIGHT);
        panelThanhToan.add(lblGiamGia);

        panelThanhToan.add(new JLabel("Tổng thanh toán:"));
        lblTongThanhToan = new JLabel("0.0", JLabel.RIGHT);
        lblTongThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongThanhToan.setForeground(AppColor.ERROR);
        panelThanhToan.add(lblTongThanhToan);

        JButton btnHuyDon = new JButton("Hủy đơn");
        btnHuyDon.addActionListener(e -> handleHuyDon());
        panelThanhToan.add(btnHuyDon);

        JButton btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setBackground(AppColor.SUCCESS);
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThanhToan.addActionListener(e -> handleThanhToan());
        panelThanhToan.add(btnThanhToan);

        JButton btnIn = new JButton("IN HÓA ĐƠN");
        btnIn.setBackground(AppColor.INFO);
        btnIn.setForeground(Color.WHITE);
        btnIn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIn.addActionListener(e -> handleInHoaDon());
        panelThanhToan.add(btnIn);

        JButton btnXuat = new JButton("XUẤT PDF");
        btnXuat.setBackground(AppColor.WARN);
        btnXuat.setForeground(Color.WHITE);
        btnXuat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXuat.addActionListener(e -> handleXuatPDF());
        panelThanhToan.add(btnXuat);

        panelPhai.add(panelThanhToan, BorderLayout.SOUTH);
        add(panelPhai, BorderLayout.CENTER);
    }

    private void bindEvents() {
        txtTimKiem.addActionListener(e -> loadData());
        cboLoaiSP.addActionListener(e -> loadData());
        chkDungDiem.addActionListener(e -> capNhatBangGioHang());

        tableSanPham.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) handleThemGioHang();
            }
        });

        tableModelGioHang.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2) {
                handleCapNhatSL(e.getFirstRow());
            }
        });

        tableGioHang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = tableGioHang.getColumnModel().getColumnIndexAtX(e.getX());
                int row = tableGioHang.getSelectedRow();
                if (row >= 0 && column == 4) {
                    dsGioHang.remove(row);
                    capNhatBangGioHang();
                }
            }
        });
    }

    private void napLoaiSP() {
        List<LoaiSP> list = LoaiSPDAO.getInstance().getList(new LoaiSPGetListCriteria()).data();
        for (LoaiSP l : list) {
            cboLoaiSP.addItem(l.getTen());
        }
    }

    private void loadData() {
        tableModelSanPham.setRowCount(0);
        SanPhamGetListCriteria criteria = new SanPhamGetListCriteria();
        criteria.setTrangThai(main.java.enumeration.TrangThaiSP.ACTIVE);
        String selectedLoai = (String) cboLoaiSP.getSelectedItem();
        
        if (selectedLoai != null && !selectedLoai.equals("Tất cả")) {
            List<LoaiSP> loais = LoaiSPDAO.getInstance().getList(new LoaiSPGetListCriteria()).data();
            for (LoaiSP l : loais) {
                if (l.getTen().equals(selectedLoai)) {
                    criteria.setMaLoai(l.getMa());
                    break;
                }
            }
        }

        dsSanPham = SanPhamDAO.getInstance().getList(criteria).data();
        final String search = txtTimKiem.getText().toLowerCase().trim();
        
        new SwingWorker<Void, Object[]>() {
            @Override
            protected Void doInBackground() {
                for (SanPham sp : dsSanPham) {
                    if (search.isEmpty() || sp.getTen().toLowerCase().contains(search) || sp.getMa().toLowerCase().contains(search)) {
                        String tenHienThi = sp.getTen();
                        if (sp.getSoLuong() <= 0) tenHienThi = "<html><font color='red'>[HẾT HÀNG]</font> " + tenHienThi + "</html>";
                        
                        publish(new Object[]{
                            ImageUtil.createIcon(sp.getAnh(), 70, 70), 
                            sp.getMa(), tenHienThi, String.format("%,.0f", sp.getGia()), sp.getSoLuong()
                        });
                    }
                }
                return null;
            }
            @Override
            protected void process(List<Object[]> chunks) {
                for (Object[] row : chunks) tableModelSanPham.addRow(row);
            }
        }.execute();
    }

    private void handleTimKhachHang() {
        String sdt = txtSdtKhach.getText().trim();
        if (sdt.isEmpty()) { JOptionPane.showMessageDialog(this, "Nhập số điện thoại!"); return; }
        if (!AppRegex.PHONE.matcher(sdt).matches()) { JOptionPane.showMessageDialog(this, "SĐT không hợp lệ!"); return; }
        
        Optional<KhachHang> kh = KhachHangDAO.getInstance().getBySdt(sdt);
        if (kh.isPresent()) {
            khachHangHienTai = kh.get();
            txtTenKhach.setText(khachHangHienTai.getTen());
            lblDiemHienCo.setText("Điểm hiện có: " + khachHangHienTai.getDiem());
            chkDungDiem.setEnabled(khachHangHienTai.getDiem() > 0);
        } else {
            if (JOptionPane.showConfirmDialog(this, "Khách hàng mới, thêm?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String ten = JOptionPane.showInputDialog(this, "Nhập tên:");
                if (ten != null && !ten.trim().isEmpty()) {
                    KhachHang newKH = new KhachHang(sdt, ten, 0);
                    if (KhachHangDAO.getInstance().add(newKH)) {
                        khachHangHienTai = newKH;
                        txtTenKhach.setText(khachHangHienTai.getTen());
                        lblDiemHienCo.setText("Điểm hiện có: 0");
                        chkDungDiem.setEnabled(false);
                    }
                }
            }
        }
        capNhatBangGioHang();
    }

    private void handleThemGioHang() {
        int row = tableSanPham.getSelectedRow();
        if (row < 0) return;
        String ma = (String) tableModelSanPham.getValueAt(row, 1);
        SanPham sp = dsSanPham.stream().filter(s -> s.getMa().equals(ma)).findFirst().orElse(null);
        if (sp == null || sp.getSoLuong() <= 0) { JOptionPane.showMessageDialog(this, "Hết hàng!"); return; }

        for (ChiTietHD ct : dsGioHang) {
            if (ct.getSanPham().getMa().equals(ma)) {
                if (ct.getSoLuong() + 1 > sp.getSoLuong()) { JOptionPane.showMessageDialog(this, "Không đủ tồn kho!"); return; }
                ct.setSoLuong(ct.getSoLuong() + 1);
                ct.setThanhTien(ct.getSoLuong() * ct.getDonGia());
                capNhatBangGioHang();
                return;
            }
        }
        dsGioHang.add(new ChiTietHD(null, sp, 1, sp.getGia(), sp.getGia()));
        capNhatBangGioHang();
    }

    private void handleCapNhatSL(int row) {
        if (row < 0 || row >= dsGioHang.size()) return;
        ChiTietHD ct = dsGioHang.get(row);
        try {
            int sl = Integer.parseInt(tableModelGioHang.getValueAt(row, 2).toString());
            if (sl <= 0) dsGioHang.remove(row);
            else {
                SanPham sp = SanPhamDAO.getInstance().getByMa(ct.getSanPham().getMa()).orElse(null);
                if (sp != null && sl > sp.getSoLuong()) {
                    JOptionPane.showMessageDialog(this, "Tồn kho không đủ! (Tối đa: " + sp.getSoLuong() + ")");
                    sl = sp.getSoLuong();
                }
                ct.setSoLuong(sl);
                ct.setThanhTien(sl * ct.getDonGia());
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!"); }
        capNhatBangGioHang();
    }

    private void capNhatBangGioHang() {
        tableModelGioHang.setRowCount(0);
        double tong = 0;
        for (ChiTietHD ct : dsGioHang) {
            tableModelGioHang.addRow(new Object[]{
                ct.getSanPham().getTen(), String.format("%,.0f", ct.getDonGia()), ct.getSoLuong(), String.format("%,.0f", ct.getThanhTien()), "Xóa"
            });
            tong += ct.getThanhTien();
        }
        double thue = tong * 0.1;
        double giamGia = 0;
        if (chkDungDiem.isSelected() && khachHangHienTai != null) {
            giamGia = Math.min(khachHangHienTai.getDiem(), tong + thue);
        }
        
        lblTongCong.setText(String.format("%,.0f VND", tong));
        lblThue.setText(String.format("%,.0f VND", thue));
        lblGiamGia.setText(String.format("-%,.0f VND", giamGia));
        lblTongThanhToan.setText(String.format("%,.0f VND", tong + thue - giamGia));
    }

    private void handleHuyDon() {
        dsGioHang.clear();
        khachHangHienTai = null;
        txtSdtKhach.setText("");
        txtTenKhach.setText("");
        lblDiemHienCo.setText("Điểm hiện có: 0");
        chkDungDiem.setSelected(false);
        chkDungDiem.setEnabled(false);
        capNhatBangGioHang();
    }

    private void handleThanhToan() {
        if (dsGioHang.isEmpty()) { JOptionPane.showMessageDialog(this, "Giỏ hàng trống!"); return; }
        NhanVien nv = AppContext.getInstance().getCurrentUser();
        if (nv == null) { JOptionPane.showMessageDialog(this, "Lỗi đăng nhập!"); return; }

        double tong = dsGioHang.stream().mapToDouble(ChiTietHD::getThanhTien).sum();
        double thue = tong * 0.1;
        double giamGia = 0;
        if (chkDungDiem.isSelected() && khachHangHienTai != null) {
            giamGia = Math.min(khachHangHienTai.getDiem(), tong + thue);
        }

        String maHD = "HD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-" + nv.getMa();
        HoaDon hd = new HoaDon(maHD, nv, khachHangHienTai, LocalDateTime.now(), tong + thue - giamGia, thue, TrangThaiHD.PAID);

        if (HoaDonDAO.getInstance().add(hd)) {
            for (ChiTietHD ct : dsGioHang) {
                ct.setHoaDon(hd);
                ChiTietHDDAO.getInstance().add(ct);
                SanPham sp = ct.getSanPham();
                sp.setSoLuong(sp.getSoLuong() - ct.getSoLuong());
                SanPhamDAO.getInstance().update(sp);
            }
            
            if (khachHangHienTai != null) {
                int diemHienTai = khachHangHienTai.getDiem();
                int diemTru = (int) giamGia;
                int diemMoi = (int) (hd.getTongTien() / 100);
                int diemSauCung = diemHienTai - diemTru + diemMoi;
                
                khachHangHienTai.setDiem(diemSauCung);
                boolean updateOk = KhachHangDAO.getInstance().update(khachHangHienTai);
                
                if (updateOk) {
                    JOptionPane.showMessageDialog(this, "Thành công! Mã: " + maHD + "\nĐiểm trừ: " + diemTru + " | Cộng: " + diemMoi + "\nTổng điểm mới: " + diemSauCung);
                } else {
                    JOptionPane.showMessageDialog(this, "Hóa đơn đã lưu nhưng cập nhật điểm thất bại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Thành công! Mã: " + maHD);
            }
            handleHuyDon();
            loadData();
        } else JOptionPane.showMessageDialog(this, "Lỗi lưu hóa đơn!");
    }

    private void handleInHoaDon() { JOptionPane.showMessageDialog(this, "Đang kết nối máy in..."); }
    private void handleXuatPDF() { JOptionPane.showMessageDialog(this, "Đang xuất PDF..."); }
}
