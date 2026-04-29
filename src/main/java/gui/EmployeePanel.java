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

public class EmployeePanel extends JPanel {

    // # Khởi tạo biến thành phần
    private JTable tblEmployees;
    private DefaultTableModel modelEmployees;
    private JTextField txtMa, txtTen, txtSdt, txtAnh, txtSearch;
    private JPasswordField txtMatKhau;
    private JComboBox<LoaiNV> cbLoai;
    private JComboBox<String> cbFilterLoai;
    private JLabel lblImagePreview;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // ## Data Members
    private List<NhanVien> currentData = new ArrayList<>();

    // # Constructor
    public EmployeePanel() {
        setupPanel();
        initComponents();
        loadData();
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
        // --- PHẦN TRÁI: FORM NHẬP LIỆU ---
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setPreferredSize(new Dimension(400, 0));
        leftPanel.setOpaque(false);

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createTitledBorder(" Thông tin nhân viên "));

        // Các trường nhập liệu
        formContainer.add(createFieldPanel("Mã nhân viên:", txtMa = new JTextField()));
        txtMa.setEditable(false);
        txtMa.setBackground(new Color(240, 240, 240));

        formContainer.add(createFieldPanel("Họ tên:", txtTen = new JTextField()));
        formContainer.add(createFieldPanel("Số điện thoại:", txtSdt = new JTextField()));
        formContainer.add(createFieldPanel("Mật khẩu:", txtMatKhau = new JPasswordField()));
        
        // Chức vụ
        JPanel pLoai = new JPanel(new BorderLayout(5, 5));
        pLoai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pLoai.setOpaque(false);
        pLoai.add(new JLabel("Chức vụ:"), BorderLayout.NORTH);
        cbLoai = new JComboBox<>(LoaiNV.values());
        pLoai.add(cbLoai, BorderLayout.CENTER);
        formContainer.add(pLoai);
        formContainer.add(Box.createVerticalStrut(10));

        // URL Ảnh
        formContainer.add(createFieldPanel("URL Ảnh đại diện:", txtAnh = new JTextField()));
        txtAnh.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateImagePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateImagePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateImagePreview(); }
        });

        // Xem trước ảnh
        lblImagePreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblImagePreview.setPreferredSize(new Dimension(200, 260));
        lblImagePreview.setMaximumSize(new Dimension(200, 260));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblImagePreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.add(lblImagePreview);
        formContainer.add(Box.createVerticalGlue());

        leftPanel.add(formContainer, BorderLayout.CENTER);

        // Các nút điều khiển
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.setOpaque(false);
        btnAdd = new JButton("Thêm mới");
        btnAdd.setBackground(AppColor.SUCCESS);
        btnAdd.setForeground(Color.WHITE);
        btnUpdate = new JButton("Cập nhật");
        btnUpdate.setBackground(AppColor.WARN);
        btnUpdate.setForeground(Color.WHITE);
        btnDelete = new JButton("Xóa");
        btnDelete.setBackground(AppColor.ERROR);
        btnDelete.setForeground(Color.WHITE);
        btnClear = new JButton("Làm mới");
        btnClear.setBackground(AppColor.INFO);
        btnClear.setForeground(Color.WHITE);
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        // --- PHẦN PHẢI: DANH SÁCH ---
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setOpaque(false);

        // Thanh tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(15);
        txtSearch.addActionListener(e -> loadData());
        searchPanel.add(txtSearch);

        searchPanel.add(new JLabel("Lọc chức vụ:"));
        cbFilterLoai = new JComboBox<>(new String[]{"Tất cả", "ADMIN", "STAFF"});
        cbFilterLoai.addActionListener(e -> loadData());
        searchPanel.add(cbFilterLoai);
        
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        // Bảng danh sách
        modelEmployees = new DefaultTableModel(new String[]{"STT", "Ảnh", "Mã NV", "Họ Tên", "SĐT", "Chức vụ"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Icon.class : Object.class;
            }
        };
        tblEmployees = new JTable(modelEmployees);
        tblEmployees.setRowHeight(65);
        tblEmployees.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblEmployees.getSelectedRow();
                if (row >= 0) fillForm(row);
            }
        });
        
        rightPanel.add(new JScrollPane(tblEmployees), BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        // Đăng ký sự kiện
        bindEvents();
    }

    /**
     * Đăng ký sự kiện cho các nút bấm
     */
    private void bindEvents() {
        btnAdd.addActionListener(e -> handleAdd());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());
    }

    // # Tương tác dữ liệu

    /**
     * Tải dữ liệu từ database lên bảng
     */
    private void loadData() {
        modelEmployees.setRowCount(0);
        NhanVienGetListCriteria criteria = new NhanVienGetListCriteria();
        criteria.setTuKhoa(txtSearch.getText().trim());
        String filter = (String) cbFilterLoai.getSelectedItem();
        if (filter != null && !filter.equals("Tất cả")) criteria.setLoai(LoaiNV.valueOf(filter));

        PaginatedResponse<NhanVien> response = NhanVienDAO.getInstance().getList(criteria);
        currentData = response.data();

        int stt = 1;
        for (NhanVien nv : currentData) {
            modelEmployees.addRow(new Object[]{
                stt++, ImageUtil.createIcon(nv.getAnh(), 50, 60), nv.getMa(), nv.getTen(), nv.getSdt(), nv.getLoai()
            });
        }
    }

    // ## Event Handlers

    /**
     * Xử lý thêm mới nhân viên
     */
    private void handleAdd() {
        if (!validateForm()) return;

        String sdt = txtSdt.getText().trim();
        if (NhanVienDAO.getInstance().getBySdt(sdt).isPresent()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại này đã được sử dụng bởi nhân viên khác!");
            return;
        }

        String ma = "NV" + (System.currentTimeMillis() % 10000);
        NhanVien nv = new NhanVien(ma, txtTen.getText().trim(), txtSdt.getText().trim(), new String(txtMatKhau.getPassword()), txtAnh.getText().trim(), (LoaiNV) cbLoai.getSelectedItem());
        if (NhanVienDAO.getInstance().add(nv)) { 
            JOptionPane.showMessageDialog(this, "Thêm thành công!"); 
            loadData(); 
            clearForm(); 
        }
    }

    /**
     * Xử lý cập nhật thông tin nhân viên
     */
    private void handleUpdate() {
        String ma = txtMa.getText();
        if (ma.isEmpty()) return;
        if (!validateForm()) return;

        String sdt = txtSdt.getText().trim();
        Optional<NhanVien> existing = NhanVienDAO.getInstance().getBySdt(sdt);
        if (existing.isPresent() && !existing.get().getMa().equals(ma)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại này đã được sử dụng bởi nhân viên khác!");
            return;
        }

        NhanVien nv = new NhanVien(ma, txtTen.getText().trim(), txtSdt.getText().trim(), new String(txtMatKhau.getPassword()), txtAnh.getText().trim(), (LoaiNV) cbLoai.getSelectedItem());
        if (NhanVienDAO.getInstance().update(nv)) { 
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!"); 
            loadData(); 
        }
    }

    /**
     * Xử lý xóa nhân viên (hỗ trợ xóa nhiều)
     */
    private void handleDelete() {
        int[] selectedRows = tblEmployees.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một nhân viên để xóa!");
            return;
        }

        String message = selectedRows.length == 1 
            ? "Bạn có chắc chắn muốn xóa nhân viên này?" 
            : "Bạn có chắc chắn muốn xóa " + selectedRows.length + " nhân viên đã chọn?";

        if (JOptionPane.showConfirmDialog(this, message, "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            List<String> listMaNV = new ArrayList<>();
            for (int row : selectedRows) {
                listMaNV.add((String) modelEmployees.getValueAt(row, 2));
            }

            if (NhanVienDAO.getInstance().deleteAllByMaNV(listMaNV)) {
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi xóa nhân viên!");
            }
        }
    }

    // # Utils

    /**
     * Tạo một Panel chứa Label và TextField
     */
    private JPanel createFieldPanel(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        p.setOpaque(false);
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        p.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        return p;
    }

    /**
     * Đổ dữ liệu từ bảng lên form
     */
    private void fillForm(int row) {
        NhanVien nv = currentData.get(row);
        txtMa.setText(nv.getMa());
        txtTen.setText(nv.getTen());
        txtSdt.setText(nv.getSdt());
        txtMatKhau.setText(nv.getMatKhau());
        cbLoai.setSelectedItem(nv.getLoai());
        txtAnh.setText(nv.getAnh());
        updateImagePreview();
    }

    /**
     * Làm trống form nhập liệu
     */
    private void clearForm() {
        txtMa.setText(""); txtTen.setText(""); txtSdt.setText(""); txtMatKhau.setText("");
        txtAnh.setText(""); lblImagePreview.setIcon(null); lblImagePreview.setText("Chưa có ảnh");
        tblEmployees.clearSelection();
    }

    /**
     * Cập nhật ảnh xem trước khi URL thay đổi
     */
    private void updateImagePreview() {
        String path = txtAnh.getText().trim();
        if (path.isEmpty()) {
            lblImagePreview.setIcon(null); lblImagePreview.setText("Chưa có ảnh");
        } else {
            ImageIcon icon = ImageUtil.createIcon(path, 200, 260);
            if (icon != null) { 
                lblImagePreview.setIcon(icon); 
                lblImagePreview.setText(""); 
            } else { 
                lblImagePreview.setIcon(null); 
                lblImagePreview.setText("Lỗi nạp ảnh"); 
            }
        }
    }

    /**
     * Kiểm tra dữ liệu nhập vào
     */
    private boolean validateForm() {
        String ten = txtTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String mk = new String(txtMatKhau.getPassword());
        
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

