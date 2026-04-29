package main.java.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import main.java.dao.HoaDonDAO;
import main.java.dao.ChiTietHDDAO;
import main.java.entity.HoaDon;
import main.java.entity.ChiTietHD;
import main.java.util.AppColor;

public class InvoicePanel extends JPanel {

    // # Khởi tạo biến thành phần
    private JTable tblInvoices, tblDetails;
    private DefaultTableModel modelInvoices, modelDetails;
    private JTextField txtSearch;
    private JButton btnDelete, btnRefresh;

    // ## Data Members
    private List<HoaDon> currentInvoices = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // # Constructor
    public InvoicePanel() {
        setupPanel();
        initComponents();
        loadInvoices();
    }

    // # Giao diện
    private void setupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
    }

    private void initComponents() {
        // --- THANH CÔNG CỤ (TÌM KIẾM) ---
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        toolBar.setOpaque(false);
        toolBar.add(new JLabel("Tìm kiếm (Mã HD/SĐT Khách):"));
        txtSearch = new JTextField(20);
        txtSearch.addActionListener(e -> loadInvoices());
        toolBar.add(txtSearch);

        btnRefresh = new JButton("Làm mới");
        btnRefresh.setBackground(AppColor.INFO);
        btnRefresh.setForeground(Color.WHITE);
        toolBar.add(btnRefresh);

        btnDelete = new JButton("Hủy hóa đơn");
        btnDelete.setBackground(AppColor.ERROR);
        btnDelete.setForeground(Color.WHITE);
        toolBar.add(btnDelete);

        add(toolBar, BorderLayout.NORTH);

        // --- NỘI DUNG CHÍNH (SPLIT PANE) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.5);

        // Bảng hóa đơn
        modelInvoices = new DefaultTableModel(new String[]{"Mã hóa đơn", "Ngày lập", "Nhân viên", "Khách hàng", "Tổng tiền", "VAT"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblInvoices = new JTable(modelInvoices);
        tblInvoices.setRowHeight(30);
        tblInvoices.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblInvoices.getSelectedRow();
                if (row >= 0) InvoicePanel.this.loadDetails(currentInvoices.get(row).getMa());
            }
        });
        JPanel pnlInvoices = new JPanel(new BorderLayout());
        pnlInvoices.setBorder(BorderFactory.createTitledBorder(" Danh sách hóa đơn "));
        pnlInvoices.add(new JScrollPane(tblInvoices), BorderLayout.CENTER);
        splitPane.setTopComponent(pnlInvoices);

        // Bảng chi tiết
        modelDetails = new DefaultTableModel(new String[]{"STT", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblDetails = new JTable(modelDetails);
        tblDetails.setRowHeight(30);
        JPanel pnlDetails = new JPanel(new BorderLayout());
        pnlDetails.setBorder(BorderFactory.createTitledBorder(" Chi tiết hóa đơn "));
        pnlDetails.add(new JScrollPane(tblDetails), BorderLayout.CENTER);
        splitPane.setBottomComponent(pnlDetails);

        add(splitPane, BorderLayout.CENTER);

        bindEvents();
    }

    private void bindEvents() {
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadInvoices();
            modelDetails.setRowCount(0);
        });
        btnDelete.addActionListener(e -> handleDelete());
    }

    // # Tương tác dữ liệu
    private void loadInvoices() {
        modelInvoices.setRowCount(0);
        main.java.dto.HoaDonGetListCriteria criteria = new main.java.dto.HoaDonGetListCriteria();
        criteria.setTuKhoa(txtSearch.getText().trim());
        
        currentInvoices = HoaDonDAO.getInstance().getList(criteria).data();
        
        for (HoaDon hd : currentInvoices) {
            modelInvoices.addRow(new Object[]{
                hd.getMa(),
                hd.getNgayLap().format(formatter),
                hd.getNhanVien() != null ? hd.getNhanVien().getTen() : "N/A",
                hd.getKhachHang() != null ? hd.getKhachHang().getTen() : "Khách vãng lai",
                String.format("%,.0f", hd.getTongTien()),
                String.format("%,.0f", hd.getVat())
            });
        }
    }

    private void loadDetails(String maHD) {
        modelDetails.setRowCount(0);
        List<ChiTietHD> details = ChiTietHDDAO.getInstance().getByMaHD(maHD);
        int stt = 1;
        for (ChiTietHD ct : details) {
            modelDetails.addRow(new Object[]{
                stt++,
                ct.getSanPham().getTen(),
                String.format("%,.0f", ct.getDonGia()),
                ct.getSoLuong(),
                String.format("%,.0f", ct.getThanhTien())
            });
        }
    }

    // # Event Handlers
    private void handleDelete() {
        int row = tblInvoices.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để hủy!");
            return;
        }

        String ma = (String) modelInvoices.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Hủy hóa đơn " + ma + "? Chức năng này không thể hoàn tác!", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            // Lưu ý: Logic DAO delete invoice có thể cần xử lý hoàn trả tồn kho nếu nghiệp vụ yêu cầu
            if (HoaDonDAO.getInstance().delete(ma)) {
                JOptionPane.showMessageDialog(this, "Đã hủy hóa đơn thành công!");
                loadInvoices();
                modelDetails.setRowCount(0);
            }
        }
    }
}
