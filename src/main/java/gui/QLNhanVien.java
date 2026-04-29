package main.java.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import main.java.dao.NhanVienDAO;
import main.java.entity.NhanVien;
import main.java.enumeration.LoaiNV;
import main.java.util.AppRegex;
import main.java.util.ImageUtil;
import main.java.util.AppColor;
import main.java.dto.NhanVienGetListCriteria;
import main.java.dto.PaginatedResponse;

public class QLNhanVien extends JPanel {

    private JTable tableNhanVien;
    private DefaultTableModel tableModelNhanVien;
    private JTextField txtMaNV, txtTenNV, txtSdtNV, txtAnhNV, txtTimKiem;
    private JPasswordField txtMatKhauNV;
    private JComboBox<LoaiNV> cboLoaiNV;
    private JComboBox<String> cboLocLoaiNV;
    private JLabel lblAnhPreview;
    private JButton btnThem, btnSua, btnXoa, btnXoaTrang;
    private List<NhanVien> dsNhanVien = new ArrayList<>();
    private SwingWorker<Void, NhanVien> swingworker;

    public QLNhanVien() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
        
        init();
        bindEvents();
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
            BorderFactory.createTitledBorder(" Thông tin nhân viên "),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        containerForm.add(taoRowNhapLieu("Mã nhân viên:", txtMaNV = new JTextField()));
        txtMaNV.setEditable(false);
        txtMaNV.setBackground(new Color(240, 240, 240));

        containerForm.add(taoRowNhapLieu("Họ tên:", txtTenNV = new JTextField()));
        containerForm.add(taoRowNhapLieu("Số điện thoại:", txtSdtNV = new JTextField()));
        containerForm.add(taoRowNhapLieu("Mật khẩu:", txtMatKhauNV = new JPasswordField()));
        
        JPanel pLoai = new JPanel(new BorderLayout(5, 5));
        pLoai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pLoai.setOpaque(false);
        pLoai.add(new JLabel("Chức vụ:"), BorderLayout.NORTH);
        cboLoaiNV = new JComboBox<>(LoaiNV.values());
        pLoai.add(cboLoaiNV, BorderLayout.CENTER);
        containerForm.add(pLoai);
        containerForm.add(Box.createVerticalStrut(10));

        containerForm.add(taoRowNhapLieu("URL Ảnh đại diện:", txtAnhNV = new JTextField()));

        lblAnhPreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblAnhPreview.setPreferredSize(new Dimension(200, 260));
        lblAnhPreview.setMaximumSize(new Dimension(200, 260));
        lblAnhPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblAnhPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerForm.add(lblAnhPreview);
        containerForm.add(Box.createVerticalGlue());

        panelTrai.add(containerForm, BorderLayout.CENTER);

        JPanel panelNut = new JPanel(new GridLayout(2, 2, 5, 5));
        panelNut.setOpaque(false);
        btnThem = new JButton("Thêm mới");
        btnThem.setBackground(AppColor.SUCCESS);
        btnThem.setForeground(Color.WHITE);
        btnSua = new JButton("Cập nhật");
        btnSua.setBackground(AppColor.WARN);
        btnSua.setForeground(Color.WHITE);
        btnXoa = new JButton("Xóa");
        btnXoa.setBackground(AppColor.ERROR);
        btnXoa.setForeground(Color.WHITE);
        btnXoaTrang = new JButton("Reset Form");
        btnXoaTrang.setBackground(AppColor.INFO);
        btnXoaTrang.setForeground(Color.WHITE);
        
        panelNut.add(btnThem);
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnXoaTrang);
        panelTrai.add(panelNut, BorderLayout.SOUTH);

        add(panelTrai, BorderLayout.WEST);

        JPanel panelPhai = new JPanel(new BorderLayout(5, 5));
        panelPhai.setOpaque(false);

        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTimKiem.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(15);
        panelTimKiem.add(txtTimKiem);

        panelTimKiem.add(new JLabel("Lọc chức vụ:"));
        cboLocLoaiNV = new JComboBox<>(new String[]{"Tất cả", "ADMIN", "STAFF"});
        panelTimKiem.add(cboLocLoaiNV);
        
        panelPhai.add(panelTimKiem, BorderLayout.NORTH);

        tableModelNhanVien = new DefaultTableModel(new String[]{"STT", "Ảnh", "Mã NV", "Họ Tên", "SĐT", "Chức vụ"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Icon.class : Object.class;
            }
        };
        tableNhanVien = new JTable(tableModelNhanVien);
        tableNhanVien.setRowHeight(65);
        
        panelPhai.add(new JScrollPane(tableNhanVien), BorderLayout.CENTER);
        add(panelPhai, BorderLayout.CENTER);
    }

    private void bindEvents() {
        btnThem.addActionListener(e -> handleThem());
        btnSua.addActionListener(e -> handleSua());
        btnXoa.addActionListener(e -> handleXoa());
        btnXoaTrang.addActionListener(e -> xoaTrangForm());

        txtTimKiem.addActionListener(e -> loadData());
        cboLocLoaiNV.addActionListener(e -> loadData());

        tableNhanVien.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableNhanVien.getSelectedRow();
                if (row >= 0) fillForm(row);
            }
        });

        txtAnhNV.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { taiAnhPreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { taiAnhPreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { taiAnhPreview(); }
        });
    }

    private void loadData() {
        if (swingworker != null && !swingworker.isDone()) {
            swingworker.cancel(true);
        }

        tableModelNhanVien.setRowCount(0);
        dsNhanVien.clear(); 
        
        NhanVienGetListCriteria criteria = new NhanVienGetListCriteria();
        criteria.setTuKhoa(txtTimKiem.getText().trim());
        String filter = (String) cboLocLoaiNV.getSelectedItem();
        if (filter != null && !filter.equals("Tất cả")) criteria.setLoai(LoaiNV.valueOf(filter));

        swingworker = new SwingWorker<Void, NhanVien>() {
            @Override
            protected Void doInBackground() {
                PaginatedResponse<NhanVien> response = NhanVienDAO.getInstance().getList(criteria);
                List<NhanVien> data = response.data();

                for (NhanVien nv : data) {
                    if (isCancelled()) return null;
                    ImageUtil.createIcon(nv.getAnh(), 50, 60);
                    publish(nv);
                }
                return null;
            }

            @Override
            protected void process(List<NhanVien> chunks) {
                if (isCancelled()) return;
                int stt = tableModelNhanVien.getRowCount() + 1;
                for (NhanVien nv : chunks) {
                    dsNhanVien.add(nv);
                    tableModelNhanVien.addRow(new Object[]{
                        stt++, ImageUtil.createIcon(nv.getAnh(), 50, 60), nv.getMa(), nv.getTen(), nv.getSdt(), nv.getLoai()
                    });
                }
            }
        };
        swingworker.execute();
    }

    private void handleThem() {
        if (!kiemTraDuLieu()) return;

        String sdt = txtSdtNV.getText().trim();
        if (NhanVienDAO.getInstance().getBySdt(sdt).isPresent()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại này đã được sử dụng bởi nhân viên khác!");
            return;
        }

        String ma = "NV" + (System.currentTimeMillis() % 10000);
        NhanVien nv = new NhanVien(ma, txtTenNV.getText().trim(), txtSdtNV.getText().trim(), new String(txtMatKhauNV.getPassword()), txtAnhNV.getText().trim(), (LoaiNV) cboLoaiNV.getSelectedItem());
        if (NhanVienDAO.getInstance().add(nv)) { 
            JOptionPane.showMessageDialog(this, "Thêm thành công!"); 
            loadData(); 
            xoaTrangForm(); 
        }
    }

    private void handleSua() {
        String ma = txtMaNV.getText();
        if (ma.isEmpty()) return;
        if (!kiemTraDuLieu()) return;

        String sdt = txtSdtNV.getText().trim();
        Optional<NhanVien> existing = NhanVienDAO.getInstance().getBySdt(sdt);
        if (existing.isPresent() && !existing.get().getMa().equals(ma)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại này đã được sử dụng bởi nhân viên khác!");
            return;
        }

        NhanVien nv = new NhanVien(ma, txtTenNV.getText().trim(), txtSdtNV.getText().trim(), new String(txtMatKhauNV.getPassword()), txtAnhNV.getText().trim(), (LoaiNV) cboLoaiNV.getSelectedItem());
        if (NhanVienDAO.getInstance().update(nv)) { 
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!"); 
            loadData(); 
        }
    }

    private void handleXoa() {
        int[] rows = tableNhanVien.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một nhân viên để xóa!");
            return;
        }

        String msg = rows.length == 1 
            ? "Bạn có chắc chắn muốn xóa nhân viên này?" 
            : "Bạn có chắc chắn muốn xóa " + rows.length + " nhân viên đã chọn?";

        if (JOptionPane.showConfirmDialog(this, msg, "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            List<String> listMaNV = new ArrayList<>();
            for (int r : rows) {
                listMaNV.add((String) tableModelNhanVien.getValueAt(r, 2));
            }

            if (NhanVienDAO.getInstance().deleteAllByMaNV(listMaNV)) {
                loadData();
                xoaTrangForm();
            } else {
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi xóa nhân viên!");
            }
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

    private void fillForm(int row) {
        if (row < 0 || row >= dsNhanVien.size()) return;
        NhanVien nv = dsNhanVien.get(row);
        txtMaNV.setText(nv.getMa());
        txtTenNV.setText(nv.getTen());
        txtSdtNV.setText(nv.getSdt());
        txtMatKhauNV.setText(nv.getMatKhau());
        cboLoaiNV.setSelectedItem(nv.getLoai());
        txtAnhNV.setText(nv.getAnh());
        taiAnhPreview();
    }

    private void xoaTrangForm() {
        txtMaNV.setText(""); txtTenNV.setText(""); txtSdtNV.setText(""); txtMatKhauNV.setText("");
        txtAnhNV.setText(""); lblAnhPreview.setIcon(null); lblAnhPreview.setText("Chưa có ảnh");
        tableNhanVien.clearSelection();
    }

    private void taiAnhPreview() {
        String path = txtAnhNV.getText().trim();
        if (path.isEmpty()) {
            lblAnhPreview.setIcon(null); 
            lblAnhPreview.setText("Chưa có ảnh");
        } else {
            lblAnhPreview.setText("Đang tải...");
            new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() {
                    return ImageUtil.createIcon(path, 200, 260);
                }

                @Override
                protected void done() {
                    try {
                        ImageIcon icon = get();
                        if (txtAnhNV.getText().trim().equals(path)) {
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

    private boolean kiemTraDuLieu() {
        String ten = txtTenNV.getText().trim();
        String sdt = txtSdtNV.getText().trim();
        String mk = new String(txtMatKhauNV.getPassword());
        
        if (ten.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Họ tên trống!"); 
            return false; 
        }
        if (!AppRegex.TEN.matcher(ten).matches()) { 
            JOptionPane.showMessageDialog(this, "Họ tên chỉ được chứa chữ cái và khoảng trắng!"); 
            return false; 
        }
        if (!AppRegex.PHONE.matcher(sdt).matches()) { 
            JOptionPane.showMessageDialog(this, "SĐT sai (10 số, đầu 03/08...)!"); 
            return false; 
        }
        if (!AppRegex.PASSWORD.matcher(mk).matches()) { 
            JOptionPane.showMessageDialog(this, "Mật khẩu yếu (8 ký tự, có hoa, thường, số, ký tự đặc biệt)!"); 
            return false; 
        }
        return true;
    }
}
