package main.java.gui;

import javax.swing.*;
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

public class ProductPanel extends JPanel {

    // # Khởi tạo biến thành phần
    private JTable tblProducts;
    private DefaultTableModel modelProducts;
    private JTextField txtMa, txtTen, txtGia, txtSoLuong, txtAnh, txtSearch, txtMoTa;
    private JComboBox<LoaiSP> cbLoai;
    private JComboBox<String> cbFilterLoai;
    private JLabel lblImagePreview;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // ## Data Members
    private List<SanPham> currentData = new ArrayList<>();
    private List<LoaiSP> categories = new ArrayList<>();

    // # Constructor
    public ProductPanel() {
        setupPanel();
        initComponents();
        loadCategories();
        loadData();
    }

    // # Giao diện
    private void setupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
    }

    private void initComponents() {
        // --- PHẦN TRÁI: FORM NHẬP LIỆU ---
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setPreferredSize(new Dimension(400, 0));
        leftPanel.setOpaque(false);

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createTitledBorder(" Thông tin sản phẩm "));

        formContainer.add(createFieldPanel("Mã sản phẩm:", txtMa = new JTextField()));
        txtMa.setEditable(false);
        txtMa.setBackground(new Color(240, 240, 240));

        formContainer.add(createFieldPanel("Tên sản phẩm:", txtTen = new JTextField()));
        
        // Loại sản phẩm
        JPanel pLoai = new JPanel(new BorderLayout(5, 5));
        pLoai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pLoai.setOpaque(false);
        pLoai.add(new JLabel("Loại sản phẩm:"), BorderLayout.NORTH);
        cbLoai = new JComboBox<>();
        pLoai.add(cbLoai, BorderLayout.CENTER);
        formContainer.add(pLoai);
        formContainer.add(Box.createVerticalStrut(10));

        formContainer.add(createFieldPanel("Giá bán:", txtGia = new JTextField()));
        formContainer.add(createFieldPanel("Số lượng tồn:", txtSoLuong = new JTextField()));
        formContainer.add(createFieldPanel("Mô tả:", txtMoTa = new JTextField()));
        formContainer.add(createFieldPanel("URL Ảnh:", txtAnh = new JTextField()));

        // Event update image preview
        txtAnh.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateImagePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateImagePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateImagePreview(); }
        });

        // Preview
        lblImagePreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblImagePreview.setPreferredSize(new Dimension(150, 150));
        lblImagePreview.setMaximumSize(new Dimension(150, 150));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblImagePreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.add(lblImagePreview);
        formContainer.add(Box.createVerticalGlue());

        leftPanel.add(formContainer, BorderLayout.CENTER);

        // Nút bấm
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

        // Tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(15);
        txtSearch.addActionListener(e -> loadData());
        searchPanel.add(txtSearch);

        searchPanel.add(new JLabel("Lọc loại:"));
        cbFilterLoai = new JComboBox<>();
        cbFilterLoai.addActionListener(e -> loadData());
        searchPanel.add(cbFilterLoai);
        
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        modelProducts = new DefaultTableModel(new String[]{"Ảnh", "Mã SP", "Tên sản phẩm", "Loại", "Giá", "Tồn kho"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Icon.class : Object.class;
            }
        };
        tblProducts = new JTable(modelProducts);
        tblProducts.setRowHeight(80);
        tblProducts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblProducts.getSelectedRow();
                if (row >= 0) ProductPanel.this.fillForm(row);
            }
        });

        rightPanel.add(new JScrollPane(tblProducts), BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        bindEvents();
    }

    private void bindEvents() {
        btnAdd.addActionListener(e -> handleAdd());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());
    }

    // # Tương tác dữ liệu
    private void loadCategories() {
        categories = LoaiSPDAO.getInstance().getList(null).data();
        cbLoai.removeAllItems();
        cbFilterLoai.removeAllItems();
        cbFilterLoai.addItem("Tất cả");
        for (LoaiSP l : categories) {
            cbLoai.addItem(l);
            cbFilterLoai.addItem(l.getTen());
        }
    }

    private void loadData() {
        modelProducts.setRowCount(0);
        SanPhamGetListCriteria criteria = new SanPhamGetListCriteria();
        criteria.setTuKhoa(txtSearch.getText().trim());
        
        String filter = (String) cbFilterLoai.getSelectedItem();
        if (filter != null && !filter.equals("Tất cả")) {
            for (LoaiSP l : categories) {
                if (l.getTen().equals(filter)) {
                    criteria.setMaLoai(l.getMa());
                    break;
                }
            }
        }

        currentData = SanPhamDAO.getInstance().getList(criteria).data();
        for (SanPham sp : currentData) {
            modelProducts.addRow(new Object[]{
                ImageUtil.createIcon(sp.getAnh(), 70, 70),
                sp.getMa(),
                sp.getTen(),
                sp.getLoai().getTen(),
                String.format("%,.0f", sp.getGia()),
                sp.getSoLuong()
            });
        }
    }

    // # Event Handlers
    private void handleAdd() {
        if (!validateForm()) return;

        String ma = "SP" + (System.currentTimeMillis() % 10000);
        SanPham sp = new SanPham(
            ma, 
            txtTen.getText().trim(), 
            txtMoTa.getText().trim(),
            txtAnh.getText().trim(), 
            Double.parseDouble(txtGia.getText().trim()), 
            Integer.parseInt(txtSoLuong.getText().trim()), 
            (LoaiSP) cbLoai.getSelectedItem(),
            TrangThaiSP.ACTIVE
        );

        if (SanPhamDAO.getInstance().add(sp)) {
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
            loadData();
            clearForm();
        }
    }

    private void handleUpdate() {
        String ma = txtMa.getText();
        if (ma.isEmpty()) return;
        if (!validateForm()) return;

        SanPham sp = new SanPham(
            ma, 
            txtTen.getText().trim(), 
            txtMoTa.getText().trim(),
            txtAnh.getText().trim(), 
            Double.parseDouble(txtGia.getText().trim()), 
            Integer.parseInt(txtSoLuong.getText().trim()), 
            (LoaiSP) cbLoai.getSelectedItem(),
            TrangThaiSP.ACTIVE
        );

        if (SanPhamDAO.getInstance().update(sp)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData();
        }
    }

    private void handleDelete() {
        int row = tblProducts.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!");
            return;
        }

        String ma = (String) modelProducts.getValueAt(row, 1);
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sản phẩm này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (SanPhamDAO.getInstance().delete(ma)) {
                loadData();
                clearForm();
            }
        }
    }

    // # Utils
    private boolean validateForm() {
        String ten = txtTen.getText().trim();
        String giaStr = txtGia.getText().trim();
        String slStr = txtSoLuong.getText().trim();

        if (ten.isEmpty()) { JOptionPane.showMessageDialog(this, "Tên không được để trống!"); return false; }
        try {
            double gia = Double.parseDouble(giaStr);
            if (gia < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá phải là số dương!");
            return false;
        }
        try {
            int sl = Integer.parseInt(slStr);
            if (sl < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên dương!");
            return false;
        }
        return true;
    }

    private void fillForm(int row) {
        SanPham sp = currentData.get(row);
        txtMa.setText(sp.getMa());
        txtTen.setText(sp.getTen());
        txtMoTa.setText(sp.getMoTa());
        txtGia.setText(String.valueOf(sp.getGia().intValue()));
        txtSoLuong.setText(String.valueOf(sp.getSoLuong()));
        txtAnh.setText(sp.getAnh());
        cbLoai.setSelectedItem(sp.getLoai());
        updateImagePreview();
    }

    private void clearForm() {
        txtMa.setText(""); txtTen.setText(""); txtGia.setText(""); txtSoLuong.setText("");
        txtAnh.setText(""); txtMoTa.setText(""); lblImagePreview.setIcon(null); lblImagePreview.setText("Chưa có ảnh");
        tblProducts.clearSelection();
    }

    private void updateImagePreview() {
        String path = txtAnh.getText().trim();
        if (path.isEmpty()) {
            lblImagePreview.setIcon(null); lblImagePreview.setText("Chưa có ảnh");
        } else {
            ImageIcon icon = ImageUtil.createIcon(path, 150, 150);
            if (icon != null) { lblImagePreview.setIcon(icon); lblImagePreview.setText(""); }
            else { lblImagePreview.setIcon(null); lblImagePreview.setText("Lỗi nạp ảnh"); }
        }
    }

    private JPanel createFieldPanel(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        p.setOpaque(false);
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        p.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        return p;
    }
}
