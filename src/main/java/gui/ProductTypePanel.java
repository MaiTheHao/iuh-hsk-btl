package main.java.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import main.java.dao.LoaiSPDAO;
import main.java.entity.LoaiSP;
import main.java.util.AppColor;

public class ProductTypePanel extends JPanel {

    // # Khởi tạo biến thành phần
    private JTable tblTypes;
    private DefaultTableModel modelTypes;
    private JTextField txtMa, txtTen, txtMoTa;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // ## Data Members
    private List<LoaiSP> currentData = new ArrayList<>();

    // # Constructor
    public ProductTypePanel() {
        setupPanel();
        initComponents();
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
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setOpaque(false);

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createTitledBorder(" Thông tin loại sản phẩm "));

        formContainer.add(createFieldPanel("Mã loại:", txtMa = new JTextField()));
        txtMa.setEditable(false);
        txtMa.setBackground(new Color(240, 240, 240));

        formContainer.add(createFieldPanel("Tên loại:", txtTen = new JTextField()));
        formContainer.add(createFieldPanel("Mô tả:", txtMoTa = new JTextField()));
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

        modelTypes = new DefaultTableModel(new String[]{"Mã loại", "Tên loại", "Mô tả"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblTypes = new JTable(modelTypes);
        tblTypes.setRowHeight(30);
        tblTypes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblTypes.getSelectedRow();
                if (row >= 0) ProductTypePanel.this.fillForm(row);
            }
        });

        rightPanel.add(new JScrollPane(tblTypes), BorderLayout.CENTER);
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
    private void loadData() {
        modelTypes.setRowCount(0);
        currentData = LoaiSPDAO.getInstance().getList(null).data();
        for (LoaiSP l : currentData) {
            modelTypes.addRow(new Object[]{l.getMa(), l.getTen(), l.getMoTa()});
        }
    }

    // # Event Handlers
    private void handleAdd() {
        String ten = txtTen.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên loại không được để trống!");
            return;
        }

        String ma = "LSP" + (System.currentTimeMillis() % 1000);
        LoaiSP l = new LoaiSP(ma, ten, txtMoTa.getText().trim());
        if (LoaiSPDAO.getInstance().add(l)) {
            JOptionPane.showMessageDialog(this, "Thêm loại sản phẩm thành công!");
            loadData();
            clearForm();
        }
    }

    private void handleUpdate() {
        String ma = txtMa.getText();
        if (ma.isEmpty()) return;

        String ten = txtTen.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên loại không được để trống!");
            return;
        }

        LoaiSP l = new LoaiSP(ma, ten, txtMoTa.getText().trim());
        if (LoaiSPDAO.getInstance().update(l)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData();
        }
    }

    private void handleDelete() {
        int row = tblTypes.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một loại sản phẩm để xóa!");
            return;
        }

        String ma = (String) modelTypes.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa loại sản phẩm này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (LoaiSPDAO.getInstance().delete(ma)) {
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa loại sản phẩm này (có thể đang có sản phẩm thuộc loại này)!");
            }
        }
    }

    // # Utils
    private JPanel createFieldPanel(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        p.setOpaque(false);
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        p.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        return p;
    }

    private void fillForm(int row) {
        LoaiSP l = currentData.get(row);
        txtMa.setText(l.getMa());
        txtTen.setText(l.getTen());
        txtMoTa.setText(l.getMoTa());
    }

    private void clearForm() {
        txtMa.setText("");
        txtTen.setText("");
        txtMoTa.setText("");
        tblTypes.clearSelection();
    }
}
