package main.java.gui;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
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
import main.java.dto.PaginatedResponse;

public class SalePanel extends JPanel {

    // # Khởi tạo biến thành phần
    private JTable tblProducts, tblCart;
    private DefaultTableModel modelProducts, modelCart;
    private JTextField txtSearch, txtSdtKH, txtTenKH;
    private JComboBox<String> cbLoai;
    private JLabel lblTotal, lblVat, lblFinalTotal;
    
    // ## Data Members
    private List<SanPham> currentProducts = new ArrayList<>();
    private List<ChiTietHD> cartItems = new ArrayList<>();
    private KhachHang selectedKH = null;

    // # Constructor
    public SalePanel() {
        setupPanel();
        initComponents();
        loadCategories();
        loadProducts();
    }

    // # Giao diện

    /**
     * Cấu hình cơ bản cho Panel
     */
    private void setupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Khởi tạo các thành phần giao diện
     */
    private void initComponents() {
        initLeftPanel();
        initRightPanel();
    }

    /**
     * Khởi tạo phần bên trái: Danh sách sản phẩm
     */
    private void initLeftPanel() {
        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.setPreferredSize(new Dimension(800, 0));
        
        // Tìm kiếm và lọc
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        top.add(new JLabel("Tìm kiếm:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtSearch = new JTextField();
        txtSearch.addActionListener(e -> loadProducts());
        top.add(txtSearch, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        top.add(new JLabel("Loại:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.5;
        cbLoai = new JComboBox<>(new String[]{"Tất cả"});
        cbLoai.addActionListener(e -> loadProducts());
        top.add(cbLoai, gbc);

        left.add(top, BorderLayout.NORTH);

        // Danh sách sản phẩm
        String[] cols = {"Ảnh", "Mã", "Tên", "Giá", "Tồn kho"};
        modelProducts = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Icon.class : Object.class;
            }
        };
        tblProducts = new JTable(modelProducts);
        tblProducts.setRowHeight(80);
        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblProducts.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProducts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    addToCart();
                }
            }
        });
        
        left.add(new JScrollPane(tblProducts), BorderLayout.CENTER);
        add(left, BorderLayout.WEST);
    }

    /**
     * Khởi tạo phần bên phải: Giỏ hàng và Thanh toán
     */
    private void initRightPanel() {
        JPanel right = new JPanel(new BorderLayout(5, 5));
        
        // Thông tin khách hàng
        JPanel khPanel = new JPanel(new GridBagLayout());
        khPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        khPanel.add(new JLabel("Số điện thoại:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtSdtKH = new JTextField();
        khPanel.add(txtSdtKH, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        JButton btnFindKH = new JButton("Tìm");
        btnFindKH.addActionListener(e -> findCustomer());
        khPanel.add(btnFindKH, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        khPanel.add(new JLabel("Tên khách hàng:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtTenKH = new JTextField();
        txtTenKH.setEditable(false);
        khPanel.add(txtTenKH, gbc);

        right.add(khPanel, BorderLayout.NORTH);

        // Giỏ hàng
        String[] cols = {"Sản phẩm", "Đơn giá", "SL", "Thành tiền", "Xóa"};
        modelCart = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        tblCart = new JTable(modelCart);
        tblCart.setRowHeight(30);
        modelCart.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2) {
                updateCartQuantity(e.getFirstRow());
            }
        });

        tblCart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = tblCart.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / tblCart.getRowHeight();

                if (row < tblCart.getRowCount() && row >= 0 && column == 4) {
                    cartItems.remove(row);
                    refreshCartTable();
                }
            }
        });

        tblCart.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setForeground(Color.RED);
                setFont(c.getFont().deriveFont(Font.BOLD));
                return c;
            }
        });

        right.add(new JScrollPane(tblCart), BorderLayout.CENTER);

        // Thanh toán
        JPanel payment = new JPanel(new GridLayout(5, 2, 5, 5));
        payment.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        payment.add(new JLabel("Tổng cộng:"));
        lblTotal = new JLabel("0.0", JLabel.RIGHT);
        payment.add(lblTotal);

        payment.add(new JLabel("VAT (10%):"));
        lblVat = new JLabel("0.0", JLabel.RIGHT);
        payment.add(lblVat);

        payment.add(new JLabel("Tổng thanh toán:"));
        lblFinalTotal = new JLabel("0.0", JLabel.RIGHT);
        lblFinalTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFinalTotal.setForeground(AppColor.ERROR);
        payment.add(lblFinalTotal);

        JButton btnClear = new JButton("Hủy đơn");
        btnClear.addActionListener(e -> clearCart());
        payment.add(btnClear);

        JButton btnPay = new JButton("THANH TOÁN");
        btnPay.setBackground(AppColor.SUCCESS);
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPay.addActionListener(e -> checkout());
        payment.add(btnPay);

        JButton btnPrint = new JButton("IN HÓA ĐƠN");
        btnPrint.setBackground(AppColor.INFO);
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPrint.addActionListener(e -> handlePrint());
        payment.add(btnPrint);

        JButton btnExport = new JButton("XUẤT PDF");
        btnExport.setBackground(AppColor.WARN);
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExport.addActionListener(e -> handleExportPDF());
        payment.add(btnExport);

        right.add(payment, BorderLayout.SOUTH);
        add(right, BorderLayout.CENTER);
    }

    // # Tương tác dữ liệu

    /**
     * Tải danh sách loại sản phẩm lên ComboBox
     */
    private void loadCategories() {
        List<LoaiSP> list = LoaiSPDAO.getInstance().getList(new LoaiSPGetListCriteria()).data();
        for (LoaiSP l : list) {
            cbLoai.addItem(l.getTen());
        }
    }

    /**
     * Tải danh sách sản phẩm lên bảng (hỗ trợ tìm kiếm và lọc)
     */
    private void loadProducts() {
        modelProducts.setRowCount(0);
        SanPhamGetListCriteria criteria = new SanPhamGetListCriteria();
        String selectedLoai = (String) cbLoai.getSelectedItem();
        
        if (selectedLoai != null && !selectedLoai.equals("Tất cả")) {
            List<LoaiSP> loais = LoaiSPDAO.getInstance().getList(new LoaiSPGetListCriteria()).data();
            for (LoaiSP l : loais) {
                if (l.getTen().equals(selectedLoai)) {
                    criteria.setMaLoai(l.getMa());
                    break;
                }
            }
        }

        PaginatedResponse<SanPham> resp = SanPhamDAO.getInstance().getList(criteria);
        currentProducts = resp.data();
        
        final String search = txtSearch.getText().toLowerCase().trim();
        
        new SwingWorker<Void, Object[]>() {
            @Override
            protected Void doInBackground() {
                for (SanPham sp : currentProducts) {
                    boolean matchName = sp.getTen().toLowerCase().contains(search);
                    boolean matchMa = sp.getMa().toLowerCase().contains(search);
                    
                    if (search.isEmpty() || matchName || matchMa) {
                        ImageIcon icon = ImageUtil.createIcon(sp.getAnh(), 70, 70);
                        publish(new Object[]{
                            icon, 
                            sp.getMa(), 
                            sp.getTen(), 
                            String.format("%,.0f", sp.getGia()), 
                            sp.getSoLuong()
                        });
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Object[]> chunks) {
                for (Object[] row : chunks) {
                    modelProducts.addRow(row);
                }
            }
        }.execute();
    }

    // ## Event Handlers & Business Logic

    /**
     * Tìm kiếm khách hàng theo số điện thoại
     */
    private void findCustomer() {
        String sdt = txtSdtKH.getText().trim();
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại khách hàng!");
            return;
        }
        if (!AppRegex.PHONE.matcher(sdt).matches()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ!");
            return;
        }
        Optional<KhachHang> kh = KhachHangDAO.getInstance().getBySdt(sdt);
        if (kh.isPresent()) {
            selectedKH = kh.get();
            txtTenKH.setText(selectedKH.getTen());
        } else {
            int opt = JOptionPane.showConfirmDialog(this, "Không tìm thấy khách hàng. Thêm mới?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                String ten = JOptionPane.showInputDialog(this, "Nhập tên khách hàng:");
                if (ten != null && !ten.trim().isEmpty()) {
                    KhachHang newKH = new KhachHang(sdt, ten, 0);
                    if (KhachHangDAO.getInstance().add(newKH)) {
                        selectedKH = newKH;
                        txtTenKH.setText(selectedKH.getTen());
                    }
                }
            }
        }
    }

    /**
     * Thêm sản phẩm được chọn vào giỏ hàng
     */
    private void addToCart() {
        int row = tblProducts.getSelectedRow();
        if (row < 0) return;
        
        String ma = (String) modelProducts.getValueAt(row, 1);
        SanPham sp = currentProducts.stream().filter(s -> s.getMa().equals(ma)).findFirst().orElse(null);
        if (sp == null || sp.getSoLuong() <= 0) {
            JOptionPane.showMessageDialog(this, "Sản phẩm đã hết hàng!");
            return;
        }

        for (ChiTietHD ct : cartItems) {
            if (ct.getSanPham().getMa().equals(ma)) {
                if (ct.getSoLuong() + 1 > sp.getSoLuong()) {
                    JOptionPane.showMessageDialog(this, "Không đủ số lượng tồn kho!");
                    return;
                }
                ct.setSoLuong(ct.getSoLuong() + 1);
                ct.setThanhTien(ct.getSoLuong() * ct.getDonGia());
                refreshCartTable();
                return;
            }
        }

        ChiTietHD newCt = new ChiTietHD(null, sp, 1, sp.getGia(), sp.getGia());
        cartItems.add(newCt);
        refreshCartTable();
    }

    /**
     * Cập nhật số lượng của một mục trong giỏ hàng
     */
    private void updateCartQuantity(int row) {
        if (row < 0 || row >= cartItems.size()) return;
        ChiTietHD ct = cartItems.get(row);
        try {
            int newQty = Integer.parseInt(modelCart.getValueAt(row, 2).toString());
            if (newQty <= 0) {
                cartItems.remove(row);
            } else {
                SanPham sp = SanPhamDAO.getInstance().getByMa(ct.getSanPham().getMa()).orElse(null);
                if (sp != null && newQty > sp.getSoLuong()) {
                    JOptionPane.showMessageDialog(this, "Không đủ số lượng tồn kho! (Tối đa: " + sp.getSoLuong() + ")");
                    newQty = sp.getSoLuong();
                }
                ct.setSoLuong(newQty);
                ct.setThanhTien(newQty * ct.getDonGia());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên!");
        }
        refreshCartTable();
    }

    /**
     * Làm mới bảng giỏ hàng và tính toán lại tổng tiền
     */
    private void refreshCartTable() {
        modelCart.setRowCount(0);
        double total = 0;
        for (ChiTietHD ct : cartItems) {
            modelCart.addRow(new Object[]{
                ct.getSanPham().getTen(),
                String.format("%,.0f", ct.getDonGia()),
                ct.getSoLuong(),
                String.format("%,.0f", ct.getThanhTien()),
                "Xóa"
            });
            total += ct.getThanhTien();
        }
        
        double vat = total * 0.1;
        double finalTotal = total + vat;
        
        lblTotal.setText(String.format("%,.0f VND", total));
        lblVat.setText(String.format("%,.0f VND", vat));
        lblFinalTotal.setText(String.format("%,.0f VND", finalTotal));
    }

    /**
     * Hủy đơn hàng hiện tại
     */
    private void clearCart() {
        cartItems.clear();
        selectedKH = null;
        txtSdtKH.setText("");
        txtTenKH.setText("");
        refreshCartTable();
    }

    /**
     * Thực hiện thanh toán và lưu hóa đơn
     */
    private void checkout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
            return;
        }

        NhanVien currentNV = AppContext.getInstance().getCurrentUser();
        if (currentNV == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy thông tin nhân viên đăng nhập!");
            return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String khId = (selectedKH != null) ? selectedKH.getSdt() : "VL";
        String maHD = "HD-" + timestamp + "-" + currentNV.getMa() + "-" + khId;

        double total = cartItems.stream().mapToDouble(ChiTietHD::getThanhTien).sum();
        double vat = total * 0.1;

        HoaDon hd = new HoaDon(maHD, currentNV, selectedKH, LocalDateTime.now(), total + vat, vat, TrangThaiHD.PAID);

        if (HoaDonDAO.getInstance().add(hd)) {
            for (ChiTietHD ct : cartItems) {
                ct.setHoaDon(hd);
                ChiTietHDDAO.getInstance().add(ct);
                SanPham sp = ct.getSanPham();
                sp.setSoLuong(sp.getSoLuong() - ct.getSoLuong());
                SanPhamDAO.getInstance().update(sp);
            }
            
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!\nMã hóa đơn: " + maHD);
            clearCart();
            loadProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu hóa đơn!");
        }
    }

    // # Utils

    /**
     * Xử lý in hóa đơn (Mockup)
     */
    private void handlePrint() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm sản phẩm vào giỏ hàng trước khi in!");
            return;
        }
        JOptionPane.showMessageDialog(this, "Đang chuẩn bị kết nối máy in...\nChức năng xuất bản cứng đang được khởi tạo.");
    }

    /**
     * Xử lý xuất hóa đơn PDF (Mockup)
     */
    private void handleExportPDF() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm sản phẩm vào giỏ hàng trước khi xuất!");
            return;
        }
        JOptionPane.showMessageDialog(this, "Đang khởi tạo tệp PDF...\nĐường dẫn mặc định: ./exports/receipt.pdf");
    }
}
