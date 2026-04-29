package main.java.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import main.java.dao.LoaiSPDAO;
import main.java.entity.LoaiSP;
import main.java.util.AppColor;

/**
 * GUIDE CHO DEVELOPER:
 * 1. Trang này dùng để quản lý Loại sản phẩm (CRUD thuần).
 * 2. Cấu trúc đơn giản hơn ProductPanel vì ít trường dữ liệu hơn.
 * 3. Mục tiêu: Giúp người dùng phân loại sản phẩm dễ dàng.
 * 
 * ## QUY TẮC LAYOUT (QUAN TRỌNG):
 * - NGHIÊM CẤM sử dụng GridBagLayout.
 * - Khuyến khích dùng BorderLayout cho khung chính và BoxLayout cho các form nhập liệu.
 * 
 * ## TÀI LIỆU THAM KHẢO:
 * - Hãy đọc kỹ file EmployeePanel.java để học cách tổ chức layout và binding dữ liệu 
 *   lên bảng một cách chuyên nghiệp.
 * 
 * ## CÁCH DÙNG UTILS (Đọc kỹ các file này trong package main.java.util):
 * - AppColor: Đồng bộ màu sắc các nút chức năng (SUCCESS, WARN, ERROR, INFO).
 * - AppRegex: Validate tên loại (không chứa ký tự đặc biệt).
 * - AppContext: Kiểm tra quyền hạn nếu cần ẩn/hiện nút xóa.
 */
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
        /**
         * TODO:
         * - Thiết kế Layout split: Form bên trái (nhỏ), Table bên phải (lớn).
         * - Form: Mã (ReadOnly), Tên loại, Mô tả.
         */
        bindEvents();
    }

    private void bindEvents() {
        // Tương tự các trang CRUD khác
    }

    // # Tương tác dữ liệu
    private void loadData() {
        /**
         * GUIDE:
         * - Dùng LoaiSPDAO.getInstance().getList(...) để lấy dữ liệu.
         * - Hiển thị lên tblTypes.
         */
    }

    // # Event Handlers
    private void handleAdd() {
        /**
         * GUIDE:
         * - Lấy dữ liệu từ form.
         * - Gọi LoaiSPDAO.getInstance().add(newLoai).
         */
    }

    private void handleUpdate() {
        // Gọi LoaiSPDAO.getInstance().update(existingLoai)
    }

    private void handleDelete() {
        // Gọi LoaiSPDAO.getInstance().delete(ma)
    }

    // # Utils
    private void clearForm() {
        // Reset fields
    }
}
