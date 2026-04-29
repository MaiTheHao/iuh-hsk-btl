package main.java.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import main.java.dao.HoaDonDAO;
import main.java.dao.ChiTietHDDAO;
import main.java.entity.HoaDon;
import main.java.entity.ChiTietHD;
import main.java.util.AppColor;

/**
 * GUIDE CHO DEVELOPER:
 * 1. Trang này quản lý Lịch sử hóa đơn.
 * 2. Cần kết hợp HoaDonDAO (lấy danh sách HD) và ChiTietHDDAO (lấy chi tiết khi click vào 1 HD).
 * 3. Luồng nghiệp vụ:
 *    - Xem danh sách hóa đơn ở bảng trên/trái.
 *    - Khi chọn 1 dòng, bảng dưới/phải sẽ load các món đã mua trong hóa đơn đó.
 *    - Chức năng CRUD: Chủ yếu là READ và DELETE (Hủy hóa đơn). Thường không cho phép SỬA hóa đơn đã xuất.
 * 
 * ## QUY TẮC LAYOUT (QUAN TRỌNG):
 * - NGHIÊM CẤM sử dụng GridBagLayout.
 * - Hãy sử dụng BorderLayout kết hợp JSplitPane để chia không gian hiển thị danh sách và chi tiết.
 * 
 * ## TÀI LIỆU THAM KHẢO:
 * - Hãy đọc kỹ file EmployeePanel.java để học cách xử lý bảng (Table) và 
 *   file SalePanel.java để học cách hiển thị giỏ hàng/chi tiết hóa đơn.
 * 
 * ## CÁCH DÙNG UTILS (Đọc kỹ các file này trong package main.java.util):
 * - AppColor: Hiển thị trạng thái hóa đơn (ví dụ: PAID là màu SUCCESS).
 * - AppContext: Lấy thông tin nhân viên để lọc hóa đơn do chính mình lập nếu cần.
 */
public class InvoicePanel extends JPanel {

    // # Khởi tạo biến thành phần
    private JTable tblInvoices, tblDetails;
    private DefaultTableModel modelInvoices, modelDetails;
    private JTextField txtSearch; // Tìm theo Mã HD hoặc SĐT Khách
    private JButton btnDelete, btnRefresh;

    // ## Data Members
    private List<HoaDon> currentInvoices = new ArrayList<>();

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
        /**
         * TODO: 
         * - Phía trên: Thanh tìm kiếm và các nút Refresh, Hủy hóa đơn.
         * - Phía dưới: Dùng JSplitPane (Vertical) để chia làm 2 phần:
         *   + Phần trên: Bảng tblInvoices (Mã HD, Ngày lập, Nhân viên, Khách hàng, Tổng tiền, VAT).
         *   + Phần dưới: Bảng tblDetails (Tên SP, Đơn giá, Số lượng, Thành tiền).
         */
        
        bindEvents();
    }

    private void bindEvents() {
        /**
         * GUIDE:
         * - tblInvoices.addMouseListener: Khi click dòng nào thì gọi loadDetails(maHD).
         * - btnDelete: Hủy hóa đơn được chọn (Cần hỏi xác nhận).
         */
    }

    // # Tương tác dữ liệu
    private void loadInvoices() {
        /**
         * GUIDE:
         * - Gọi HoaDonDAO.getInstance().getList(...)
         * - Đổ dữ liệu vào modelInvoices.
         */
    }

    private void loadDetails(String maHD) {
        /**
         * GUIDE:
         * - Gọi ChiTietHDDAO.getInstance().getListByMaHD(maHD).
         * - Xóa modelDetails cũ và đổ dữ liệu mới vào.
         */
    }

    // # Event Handlers
    private void handleDelete() {
        /**
         * GUIDE:
         * - Kiểm tra xem có dòng nào được chọn không.
         * - Gọi HoaDonDAO.getInstance().delete(maHD).
         * - Lưu ý: Khi xóa hóa đơn, cần cân nhắc việc hoàn lại số lượng tồn kho cho sản phẩm (nếu nghiệp vụ yêu cầu).
         */
    }

    // # Utils
    // Các hàm phụ trợ nếu cần
}
