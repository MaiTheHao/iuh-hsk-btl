package main.java.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import main.java.dao.SanPhamDAO;
import main.java.dao.LoaiSPDAO;
import main.java.entity.SanPham;
import main.java.entity.LoaiSP;
import main.java.util.AppColor;
import main.java.util.ImageUtil;
import main.java.util.AppRegex;

/**
 * GUIDE CHO DEVELOPER:
 * 1. Trang này dùng để quản lý Sản phẩm (CRUD).
 * 2. Cần kết hợp SanPhamDAO để lấy dữ liệu và LoaiSPDAO để load danh mục vào ComboBox.
 * 3. Tuân thủ cấu hình layout và style từ EmployeePanel.
 * 
 * ## QUY TẮC LAYOUT (QUAN TRỌNG):
 * - NGHIÊM CẤM sử dụng GridBagLayout (Vì nó quá phức tạp và khó bảo trì).
 * - Hãy dùng kết hợp BorderLayout, BoxLayout và GridLayout để chia khung.
 * 
 * ## TÀI LIỆU THAM KHẢO:
 * - Hãy đọc kỹ file EmployeePanel.java để học cách tổ chức layout chuẩn 
 *   và cách xử lý các sự kiện Thêm/Sửa/Xóa/Làm mới một cách chuẩn chỉ.
 * 
 * ## CÁCH DÙNG UTILS (Đọc kỹ các file này trong package main.java.util):
 * - AppColor: Dùng để set Background cho các nút (SUCCESS cho Thêm, WARN cho Cập nhật, ERROR cho Xóa).
 * - AppRegex: Dùng để validate dữ liệu nhập vào (ví dụ: số lượng, đơn giá).
 * - ImageUtil: Dùng hàm createIcon(path, w, h) để nạp ảnh từ URL/Path vào JLabel hoặc Table.
 * - AppContext: Dùng để lấy thông tin nhân viên đang đăng nhập nếu cần ghi log.
 */
public class ProductPanel extends JPanel {

    // # Khởi tạo biến thành phần
    private JTable tblProducts;
    private DefaultTableModel modelProducts;
    private JTextField txtMa, txtTen, txtGia, txtSoLuong, txtAnh, txtSearch;
    private JComboBox<LoaiSP> cbLoai; // Load từ LoaiSPDAO
    private JLabel lblImagePreview;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    // ## Data Members
    private List<SanPham> currentData = new ArrayList<>();

    // # Constructor
    public ProductPanel() {
        setupPanel();
        initComponents();
        loadCategories(); // Cần viết hàm load dữ liệu cho ComboBox Loại SP
        loadData();       // Load danh sách sản phẩm lên table
    }

    // # Giao diện
    private void setupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
    }

    private void initComponents() {
        /**
         * TODO: 
         * - Thiết kế Layout giống EmployeePanel: Left (Form), Right (Table).
         * - Form gồm: Mã (disable), Tên, Loại (ComboBox), Giá, Số lượng, URL Ảnh.
         * - Right gồm: Search bar và Table hiển thị danh sách.
         */
        
        // Code mẫu gợi ý cấu trúc:
        // JPanel leftPanel = createLeftForm();
        // JPanel rightPanel = createRightTable();
        // add(leftPanel, BorderLayout.WEST);
        // add(rightPanel, BorderLayout.CENTER);

        bindEvents();
    }

    private void bindEvents() {
        /**
         * TODO: Gán sự kiện cho các nút bấm
         * - btnAdd -> handleAdd()
         * - btnUpdate -> handleUpdate()
         * - btnDelete -> handleDelete()
         * - btnClear -> clearForm()
         */
    }

    // # Tương tác dữ liệu
    private void loadCategories() {
        /**
         * GUIDE: 
         * - Dùng LoaiSPDAO.getInstance().getList(...) để lấy tất cả loại SP.
         * - Xóa item cũ trong cbLoai và add item mới.
         */
    }

    private void loadData() {
        /**
         * GUIDE:
         * - Dùng SanPhamDAO.getInstance().getList(...) kết hợp criteria từ txtSearch.
         * - Update modelProducts.
         */
    }

    // # Event Handlers
    private void handleAdd() {
        /**
         * GUIDE:
         * - Validate form (dùng validateForm()).
         * - Tạo object SanPham mới.
         * - Gọi SanPhamDAO.getInstance().add(sp).
         */
    }

    private void handleUpdate() {
        // Tương tự handleAdd nhưng gọi update(sp)
    }

    private void handleDelete() {
        // Hỗ trợ xóa một hoặc nhiều (xem EmployeePanel.handleDelete)
    }

    // # Utils
    private boolean validateForm() {
        /**
         * GUIDE:
         * - Kiểm tra rỗng.
         * - Kiểm tra định dạng giá (phải là số > 0).
         * - Kiểm tra số lượng (phải là số >= 0).
         */
        return true;
    }

    private void clearForm() {
        // Reset các field về rỗng
    }

    private void updateImagePreview() {
        /**
         * GUIDE:
         * - Lấy path từ txtAnh.
         * - Dùng ImageUtil để tạo Icon và set cho lblImagePreview.
         */
    }
}
