package main.java.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import main.java.dao.KhachHangDAO;
import main.java.entity.KhachHang;
import main.java.util.AppRegex;
import main.java.util.AppColor;
import main.java.dto.KhachHangGetListCriteria;
import main.java.dto.PaginatedResponse;

/**
 * @author: Trần Thanh Nhựt
 */
public class QLKhachHangPanel extends JPanel {

    private JTable tableKhachHang;
    private DefaultTableModel tableModelKhachHang;
    private JTextField txtSdt, txtTen, txtDiem, txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnXoaTrang;
    private List<KhachHang> dsKhachHang = new ArrayList<>();

    public QLKhachHangPanel() {
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
            BorderFactory.createTitledBorder(" Thông tin khách hàng "),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        containerForm.add(taoRowNhapLieu("Số điện thoại:", txtSdt = new JTextField()));
        containerForm.add(taoRowNhapLieu("Họ tên:", txtTen = new JTextField()));
        containerForm.add(taoRowNhapLieu("Điểm tích lũy:", txtDiem = new JTextField()));
        txtDiem.setText("0");
        
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
        panelTimKiem.add(new JLabel("Tìm kiếm (Tên/SĐT):"));
        txtTimKiem = new JTextField(20);
        panelTimKiem.add(txtTimKiem);
        panelPhai.add(panelTimKiem, BorderLayout.NORTH);

        tableModelKhachHang = new DefaultTableModel(new String[]{"STT", "Số điện thoại", "Họ Tên", "Điểm"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableKhachHang = new JTable(tableModelKhachHang);
        tableKhachHang.setRowHeight(30);
        
        panelPhai.add(new JScrollPane(tableKhachHang), BorderLayout.CENTER);
        add(panelPhai, BorderLayout.CENTER);
    }

    private void bindEvents() {
        btnThem.addActionListener(e -> handleThem());
        btnSua.addActionListener(e -> handleSua());
        btnXoa.addActionListener(e -> handleXoa());
        btnXoaTrang.addActionListener(e -> xoaTrangForm());
        txtTimKiem.addActionListener(e -> loadData());

        tableKhachHang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableKhachHang.getSelectedRow();
                if (row >= 0) fillForm(row);
            }
        });
    }

    private void loadData() {
        tableModelKhachHang.setRowCount(0);
        dsKhachHang.clear();
        KhachHangGetListCriteria criteria = new KhachHangGetListCriteria();
        criteria.setTuKhoa(txtTimKiem.getText().trim());
        
        PaginatedResponse<KhachHang> response = KhachHangDAO.getInstance().getList(criteria);
        dsKhachHang = response.data();
        int stt = 1;
        for (KhachHang kh : dsKhachHang) {
            tableModelKhachHang.addRow(new Object[]{stt++, kh.getSdt(), kh.getTen(), kh.getDiem()});
        }
    }

    private void handleThem() {
        if (!kiemTraDuLieu()) return;
        String sdt = txtSdt.getText().trim();
        if (KhachHangDAO.getInstance().getBySdt(sdt).isPresent()) {
            JOptionPane.showMessageDialog(this, "SĐT này đã tồn tại!");
            return;
        }

        KhachHang kh = new KhachHang(sdt, txtTen.getText().trim(), Integer.parseInt(txtDiem.getText().trim()));
        if (KhachHangDAO.getInstance().add(kh)) {
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            loadData();
            xoaTrangForm();
        }
    }

    private void handleSua() {
        String sdt = txtSdt.getText().trim();
        if (sdt.isEmpty()) return;
        if (!kiemTraDuLieu()) return;

        KhachHang kh = new KhachHang(sdt, txtTen.getText().trim(), Integer.parseInt(txtDiem.getText().trim()));
        if (KhachHangDAO.getInstance().update(kh)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData();
        }
    }

    private void handleXoa() {
        int[] rows = tableKhachHang.getSelectedRows();
        if (rows.length == 0) return;
        if (JOptionPane.showConfirmDialog(this, "Xóa các khách hàng đã chọn?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            List<String> listSdt = new ArrayList<>();
            for (int r : rows) listSdt.add((String) tableModelKhachHang.getValueAt(r, 1));
            try {
                if (KhachHangDAO.getInstance().deleteAllBySdtKH(listSdt)) {
                    loadData();
                    xoaTrangForm();
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                }
            } catch (java.sql.SQLException e) {
                if (e.getMessage().contains("REFERENCE constraint")) {
                    JOptionPane.showMessageDialog(this, "Không thể xóa khách hàng này vì đã có hóa đơn liên quan trong hệ thống!", "Lỗi ràng buộc dữ liệu", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa dữ liệu: " + e.getMessage());
                }
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
        if (row < 0 || row >= dsKhachHang.size()) return;
        KhachHang kh = dsKhachHang.get(row);
        txtSdt.setText(kh.getSdt());
        txtTen.setText(kh.getTen());
        txtDiem.setText(String.valueOf(kh.getDiem()));
        txtSdt.setEditable(false);
    }

    private void xoaTrangForm() {
        txtSdt.setText(""); txtTen.setText(""); txtDiem.setText("0"); txtSdt.setEditable(true);
        tableKhachHang.clearSelection();
    }

    private boolean kiemTraDuLieu() {
        String sdt = txtSdt.getText().trim();
        String ten = txtTen.getText().trim();
        String diem = txtDiem.getText().trim();
        
        if (sdt.isEmpty() || ten.isEmpty()) { JOptionPane.showMessageDialog(this, "Không được để trống!"); return false; }
        if (!AppRegex.PHONE.matcher(sdt).matches()) { JOptionPane.showMessageDialog(this, "SĐT không hợp lệ!"); return false; }
        try { Integer.parseInt(diem); } catch (Exception e) { JOptionPane.showMessageDialog(this, "Điểm phải là số!"); return false; }
        return true;
    }
}
