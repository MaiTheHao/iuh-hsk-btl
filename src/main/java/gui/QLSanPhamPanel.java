package main.java.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import main.java.dao.SanPhamDAO;
import main.java.dao.LoaiSPDAO;
import main.java.entity.SanPham;
import main.java.entity.LoaiSP;
import main.java.dto.SanPhamGetListCriteria;
import main.java.enumeration.TrangThaiSP;
import main.java.util.AppColor;
import main.java.util.ImageUtil;

/**
 * @author: Trần Thanh Nhựt
 */
public class QLSanPhamPanel extends JPanel {

    private JTable tableSanPham;
    private DefaultTableModel tableModelSanPham;
    private JTextField txtMaSP, txtTenSP, txtGiaSP, txtSoLuongSP, txtAnhSP, txtTimKiem, txtMoTaSP;
    private JComboBox<LoaiSP> cboLoaiSP;
    private JComboBox<TrangThaiSP> cboTrangThaiSP;
    private JComboBox<String> cboLocLoaiSP;
    private JComboBox<String> cboLocTrangThaiSP;
    private JLabel lblAnhPreview;
    private JButton btnThem, btnSua, btnKinhDoanhLai, btnNgungKinhDoanh, btnXoaTrang;
    
    private List<SanPham> dsSanPham = new ArrayList<>();
    private List<LoaiSP> dsLoai = new ArrayList<>();
    private SwingWorker<Void, SanPham> swingworker;

    public QLSanPhamPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
        
        init();
        bindEvents();
        
        napDanhSachLoai();
        loadData();
    }

    private void init() {
        JPanel panelTrai = new JPanel(new BorderLayout(10, 10));
        panelTrai.setPreferredSize(new Dimension(400, 0));
        panelTrai.setOpaque(false);

        JPanel containerForm = new JPanel();
        containerForm.setLayout(new BoxLayout(containerForm, BoxLayout.Y_AXIS));
        containerForm.setBackground(Color.WHITE);
        containerForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(" Thông tin sản phẩm "),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        containerForm.add(taoRowNhapLieu("Mã sản phẩm:", txtMaSP = new JTextField()));
        txtMaSP.setEditable(false);
        txtMaSP.setBackground(new Color(240, 240, 240));

        containerForm.add(taoRowNhapLieu("Tên sản phẩm:", txtTenSP = new JTextField()));
        
        JPanel pLoai = new JPanel(new BorderLayout(5, 5));
        pLoai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pLoai.setOpaque(false);
        pLoai.add(new JLabel("Loại sản phẩm:"), BorderLayout.NORTH);
        cboLoaiSP = new JComboBox<>();
        pLoai.add(cboLoaiSP, BorderLayout.CENTER);
        containerForm.add(pLoai);
        containerForm.add(Box.createVerticalStrut(10));

        containerForm.add(taoRowNhapLieu("Giá bán:", txtGiaSP = new JTextField()));
        containerForm.add(taoRowNhapLieu("Số lượng tồn:", txtSoLuongSP = new JTextField()));
        containerForm.add(taoRowNhapLieu("Mô tả:", txtMoTaSP = new JTextField()));
        containerForm.add(taoRowNhapLieu("URL Ảnh:", txtAnhSP = new JTextField()));

        JPanel pTrangThai = new JPanel(new BorderLayout(5, 5));
        pTrangThai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pTrangThai.setOpaque(false);
        pTrangThai.add(new JLabel("Trạng thái:"), BorderLayout.NORTH);
        cboTrangThaiSP = new JComboBox<>(TrangThaiSP.values());
        pTrangThai.add(cboTrangThaiSP, BorderLayout.CENTER);
        containerForm.add(pTrangThai);
        containerForm.add(Box.createVerticalStrut(10));

        lblAnhPreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblAnhPreview.setPreferredSize(new Dimension(150, 150));
        lblAnhPreview.setMaximumSize(new Dimension(150, 150));
        lblAnhPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblAnhPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerForm.add(lblAnhPreview);
        containerForm.add(Box.createVerticalGlue());

        panelTrai.add(containerForm, BorderLayout.CENTER);

        JPanel panelNut = new JPanel(new GridLayout(3, 2, 5, 5));
        panelNut.setOpaque(false);
        
        btnThem = new JButton("Thêm mới");
        btnThem.setBackground(AppColor.INFO);
        btnThem.setForeground(Color.WHITE);
        
        btnSua = new JButton("Cập nhật");
        btnSua.setBackground(AppColor.WARN);
        btnSua.setForeground(Color.WHITE);
        
        btnKinhDoanhLai = new JButton("Kinh doanh lại");
        btnKinhDoanhLai.setBackground(AppColor.SUCCESS);
        btnKinhDoanhLai.setForeground(Color.WHITE);
        
        btnNgungKinhDoanh = new JButton("Ngừng kinh doanh");
        btnNgungKinhDoanh.setBackground(AppColor.ERROR);
        btnNgungKinhDoanh.setForeground(Color.WHITE);
        
        btnXoaTrang = new JButton("Reset Form");
        btnXoaTrang.setBackground(Color.GRAY);
        btnXoaTrang.setForeground(Color.WHITE);

        panelNut.add(btnThem);
        panelNut.add(btnSua);
        panelNut.add(btnKinhDoanhLai);
        panelNut.add(btnNgungKinhDoanh);
        panelNut.add(btnXoaTrang);
        panelNut.add(new JLabel("")); 
        
        panelTrai.add(panelNut, BorderLayout.SOUTH);
        add(panelTrai, BorderLayout.WEST);

        JPanel panelPhai = new JPanel(new BorderLayout(5, 5));
        panelPhai.setOpaque(false);

        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTimKiem.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(15);
        panelTimKiem.add(txtTimKiem);

        panelTimKiem.add(new JLabel("Lọc loại:"));
        cboLocLoaiSP = new JComboBox<>();
        panelTimKiem.add(cboLocLoaiSP);

        panelTimKiem.add(new JLabel("Lọc trạng thái:"));
        cboLocTrangThaiSP = new JComboBox<>(new String[]{"Tất cả", "Đang kinh doanh", "Ngừng kinh doanh"});
        panelTimKiem.add(cboLocTrangThaiSP);
        
        panelPhai.add(panelTimKiem, BorderLayout.NORTH);

        tableModelSanPham = new DefaultTableModel(new String[]{"Ảnh", "Mã SP", "Tên sản phẩm", "Loại", "Giá", "Tồn kho", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Icon.class : Object.class;
            }
        };
        tableSanPham = new JTable(tableModelSanPham);
        tableSanPham.setRowHeight(80);

        panelPhai.add(new JScrollPane(tableSanPham), BorderLayout.CENTER);
        add(panelPhai, BorderLayout.CENTER);
    }

    private void bindEvents() {
        btnThem.addActionListener(e -> handleThem());
        btnSua.addActionListener(e -> handleSua());
        btnKinhDoanhLai.addActionListener(e -> handleUpdateTrangThai(TrangThaiSP.ACTIVE));
        btnNgungKinhDoanh.addActionListener(e -> handleUpdateTrangThai(TrangThaiSP.INACTIVE));
        btnXoaTrang.addActionListener(e -> xoaTrangForm());

        txtTimKiem.addActionListener(e -> loadData());
        cboLocLoaiSP.addActionListener(e -> loadData());
        cboLocTrangThaiSP.addActionListener(e -> loadData());

        tableSanPham.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableSanPham.getSelectedRow();
                if (row >= 0) fillForm(row);
            }
        });

        txtAnhSP.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { taiAnhPreview(); }
            public void removeUpdate(DocumentEvent e) { taiAnhPreview(); }
            public void changedUpdate(DocumentEvent e) { taiAnhPreview(); }
        });
    }

    private void napDanhSachLoai() {
        dsLoai = LoaiSPDAO.getInstance().getList(null).data();
        cboLoaiSP.removeAllItems();
        cboLocLoaiSP.removeAllItems();
        cboLocLoaiSP.addItem("Tất cả");
        for (LoaiSP l : dsLoai) {
            cboLoaiSP.addItem(l);
            cboLocLoaiSP.addItem(l.getTen());
        }
    }

    private void loadData() {
        if (swingworker != null && !swingworker.isDone()) {
            swingworker.cancel(true);
        }

        tableModelSanPham.setRowCount(0);
        dsSanPham.clear(); 
        
        SanPhamGetListCriteria criteria = new SanPhamGetListCriteria();
        criteria.setTuKhoa(txtTimKiem.getText().trim());
        
        String filterLoai = (String) cboLocLoaiSP.getSelectedItem();
        if (filterLoai != null && !filterLoai.equals("Tất cả")) {
            for (LoaiSP l : dsLoai) {
                if (l.getTen().equals(filterLoai)) {
                    criteria.setMaLoai(l.getMa());
                    break;
                }
            }
        }

        String filterTrangThai = (String) cboLocTrangThaiSP.getSelectedItem();
        if (filterTrangThai != null && !filterTrangThai.equals("Tất cả")) {
            criteria.setTrangThai(filterTrangThai.equals("Đang kinh doanh") ? TrangThaiSP.ACTIVE : TrangThaiSP.INACTIVE);
        }

        swingworker = new SwingWorker<Void, SanPham>() {
            @Override
            protected Void doInBackground() {
                List<SanPham> data = SanPhamDAO.getInstance().getList(criteria).data();
                for (SanPham sp : data) {
                    if (isCancelled()) return null;
                    ImageUtil.createIcon(sp.getAnh(), 70, 70);
                    publish(sp);
                }
                return null;
            }

            @Override
            protected void process(List<SanPham> chunks) {
                if (isCancelled()) return;
                for (SanPham sp : chunks) {
                    dsSanPham.add(sp);
                    tableModelSanPham.addRow(new Object[]{
                        ImageUtil.createIcon(sp.getAnh(), 70, 70), 
                        sp.getMa(),
                        sp.getTen(),
                        sp.getLoai().getTen(),
                        String.format("%,.0f", sp.getGia()),
                        sp.getSoLuong(),
                        sp.getTrangThai() == TrangThaiSP.ACTIVE ? "Đang kinh doanh" : "Ngừng kinh doanh"
                    });
                }
            }
        };
        swingworker.execute();
    }

    private void handleThem() {
        if (!kiemTraDuLieu()) return;

        String ma = "SP" + (System.currentTimeMillis() % 10000);
        SanPham sp = new SanPham(
            ma, 
            txtTenSP.getText().trim(), 
            txtMoTaSP.getText().trim(),
            txtAnhSP.getText().trim(), 
            Double.parseDouble(txtGiaSP.getText().trim()), 
            Integer.parseInt(txtSoLuongSP.getText().trim()), 
            (LoaiSP) cboLoaiSP.getSelectedItem(),
            (TrangThaiSP) cboTrangThaiSP.getSelectedItem()
        );

        if (SanPhamDAO.getInstance().add(sp)) {
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
            loadData();
            xoaTrangForm();
        }
    }

    private void handleSua() {
        String ma = txtMaSP.getText();
        if (ma.isEmpty()) return;
        if (!kiemTraDuLieu()) return;

        SanPham sp = new SanPham(
            ma, 
            txtTenSP.getText().trim(), 
            txtMoTaSP.getText().trim(),
            txtAnhSP.getText().trim(), 
            Double.parseDouble(txtGiaSP.getText().trim()), 
            Integer.parseInt(txtSoLuongSP.getText().trim()), 
            (LoaiSP) cboLoaiSP.getSelectedItem(),
            (TrangThaiSP) cboTrangThaiSP.getSelectedItem()
        );

        if (SanPhamDAO.getInstance().update(sp)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData();
        }
    }

    private void handleUpdateTrangThai(TrangThaiSP newStatus) {
        int[] rows = tableSanPham.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một sản phẩm!");
            return;
        }

        String label = newStatus == TrangThaiSP.ACTIVE ? "kinh doanh lại" : "ngừng kinh doanh";
        String msg = rows.length == 1 
            ? "Bạn có chắc muốn " + label + " sản phẩm này?" 
            : "Bạn có chắc muốn " + label + " " + rows.length + " sản phẩm đã chọn?";

        if (JOptionPane.showConfirmDialog(this, msg, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            boolean ok = true;
            for (int r : rows) {
                String ma = (String) tableModelSanPham.getValueAt(r, 1);
                if (!SanPhamDAO.getInstance().updateTrangThai(ma, newStatus)) ok = false;
            }

            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái thành công!");
                loadData();
                xoaTrangForm();
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi cập nhật!");
                loadData();
            }
        }
    }

    private boolean kiemTraDuLieu() {
        String ten = txtTenSP.getText().trim();
        String giaStr = txtGiaSP.getText().trim();
        String slStr = txtSoLuongSP.getText().trim();

        if (ten.isEmpty()) { JOptionPane.showMessageDialog(this, "Tên không được để trống!"); return false; }
        try {
            double gia = Double.parseDouble(giaStr);
            if (gia < 0) throw new Exception();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Giá phải là số dương!");
            return false;
        }
        try {
            int sl = Integer.parseInt(slStr);
            if (sl < 0) throw new Exception();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên dương!");
            return false;
        }
        return true;
    }

    private void fillForm(int row) {
        if (row < 0 || row >= dsSanPham.size()) return;
        SanPham sp = dsSanPham.get(row);
        txtMaSP.setText(sp.getMa());
        txtTenSP.setText(sp.getTen());
        txtMoTaSP.setText(sp.getMoTa());
        txtGiaSP.setText(String.valueOf(sp.getGia().intValue()));
        txtSoLuongSP.setText(String.valueOf(sp.getSoLuong()));
        txtAnhSP.setText(sp.getAnh());
        cboLoaiSP.setSelectedItem(sp.getLoai());
        cboTrangThaiSP.setSelectedItem(sp.getTrangThai());
        taiAnhPreview();
    }

    private void xoaTrangForm() {
        txtMaSP.setText(""); txtTenSP.setText(""); txtGiaSP.setText(""); txtSoLuongSP.setText("");
        txtAnhSP.setText(""); txtMoTaSP.setText(""); lblAnhPreview.setIcon(null); lblAnhPreview.setText("Chưa có ảnh");
        cboTrangThaiSP.setSelectedIndex(0);
        tableSanPham.clearSelection();
    }

    private void taiAnhPreview() {
        String path = txtAnhSP.getText().trim();
        if (path.isEmpty()) {
            lblAnhPreview.setIcon(null); 
            lblAnhPreview.setText("Chưa có ảnh");
        } else {
            lblAnhPreview.setText("Đang tải...");
            new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() {
                    return ImageUtil.createIcon(path, 150, 150);
                }

                @Override
                protected void done() {
                    try {
                        ImageIcon icon = get();
                        if (txtAnhSP.getText().trim().equals(path)) {
                            if (icon != null) {
                                lblAnhPreview.setIcon(icon);
                                lblAnhPreview.setText("");
                            } else {
                                lblAnhPreview.setIcon(null);
                                lblAnhPreview.setText("Lỗi nạp ảnh");
                            }
                        }
                    } catch (Exception e) {
                        lblAnhPreview.setText("Lỗi nạp ảnh");
                    }
                }
            }.execute();
        }
    }

    private JPanel taoRowNhapLieu(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        p.setOpaque(false);
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        p.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        return p;
    }
}
