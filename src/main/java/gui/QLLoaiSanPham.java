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

/**
 * @author: Nguyễn Lương Triều Vỹ
 */
public class QLLoaiSanPham extends JPanel {

    private JTable tableLoaiSP;
    private DefaultTableModel tableModelLoaiSP;
    private JTextField txtMaLoai, txtTenLoai, txtMoTaLoai;
    private JButton btnThem, btnSua, btnXoa, btnXoaTrang;
    private List<LoaiSP> dsLoaiSP = new ArrayList<>();

    public QLLoaiSanPham() {
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
            BorderFactory.createTitledBorder(" Thông tin loại sản phẩm "),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        containerForm.add(taoRowNhapLieu("Mã loại:", txtMaLoai = new JTextField()));
        txtMaLoai.setEditable(false);
        txtMaLoai.setBackground(new Color(240, 240, 240));

        containerForm.add(taoRowNhapLieu("Tên loại:", txtTenLoai = new JTextField()));
        containerForm.add(taoRowNhapLieu("Mô tả:", txtMoTaLoai = new JTextField()));
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

        tableModelLoaiSP = new DefaultTableModel(new String[]{"Mã loại", "Tên loại", "Mô tả"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableLoaiSP = new JTable(tableModelLoaiSP);
        tableLoaiSP.setRowHeight(30);

        panelPhai.add(new JScrollPane(tableLoaiSP), BorderLayout.CENTER);
        add(panelPhai, BorderLayout.CENTER);
    }

    private void bindEvents() {
        btnThem.addActionListener(e -> handleThem());
        btnSua.addActionListener(e -> handleSua());
        btnXoa.addActionListener(e -> handleXoa());
        btnXoaTrang.addActionListener(e -> xoaTrangForm());

        tableLoaiSP.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableLoaiSP.getSelectedRow();
                if (row >= 0) fillForm(row);
            }
        });
    }

    private void loadData() {
        tableModelLoaiSP.setRowCount(0);
        dsLoaiSP = LoaiSPDAO.getInstance().getList(null).data();
        for (LoaiSP l : dsLoaiSP) {
            tableModelLoaiSP.addRow(new Object[]{l.getMa(), l.getTen(), l.getMoTa()});
        }
    }

    private void handleThem() {
        String ten = txtTenLoai.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên loại không được để trống!");
            return;
        }

        String ma = "LSP" + (System.currentTimeMillis() % 1000);
        LoaiSP l = new LoaiSP(ma, ten, txtMoTaLoai.getText().trim());
        if (LoaiSPDAO.getInstance().add(l)) {
            JOptionPane.showMessageDialog(this, "Thêm loại sản phẩm thành công!");
            loadData();
            xoaTrangForm();
        }
    }

    private void handleSua() {
        String ma = txtMaLoai.getText();
        if (ma.isEmpty()) return;

        String ten = txtTenLoai.getText().trim();
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên loại không được để trống!");
            return;
        }

        LoaiSP l = new LoaiSP(ma, ten, txtMoTaLoai.getText().trim());
        if (LoaiSPDAO.getInstance().update(l)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData();
        }
    }

    private void handleXoa() {
        int row = tableLoaiSP.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một loại sản phẩm để xóa!");
            return;
        }

        String ma = (String) tableModelLoaiSP.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa loại sản phẩm này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (LoaiSPDAO.getInstance().delete(ma)) {
                loadData();
                xoaTrangForm();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa loại sản phẩm này (có thể đang có sản phẩm thuộc loại này)!");
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
        if (row < 0 || row >= dsLoaiSP.size()) return;
        LoaiSP l = dsLoaiSP.get(row);
        txtMaLoai.setText(l.getMa());
        txtTenLoai.setText(l.getTen());
        txtMoTaLoai.setText(l.getMoTa());
    }

    private void xoaTrangForm() {
        txtMaLoai.setText("");
        txtTenLoai.setText("");
        txtMoTaLoai.setText("");
        tableLoaiSP.clearSelection();
    }
}
