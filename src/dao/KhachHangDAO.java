package dao;

import java.util.ArrayList;
import java.util.Optional;
import dto.KhachHangGetListCriteria;
import dto.PaginatedResponse;
import entity.KhachHang;

public class KhachHangDAO {
    private static KhachHangDAO instance = new KhachHangDAO();

    private KhachHangDAO() {
    }

    public static KhachHangDAO getInstance() {
        return instance;
    }

    /**
     * Lấy danh sách khách hàng có phân trang, lọc và sắp xếp
     * @param criteria Điều kiện lọc và phân trang
     * @return PaginatedResponse<KhachHang>
     */
    public PaginatedResponse<KhachHang> getList(KhachHangGetListCriteria criteria) {
        // TODO: HƯỚNG DẪN TRIỂN KHAI (Xóa sau khi hoàn thành):
        // 1. Tham khảo SanPhamDAO.java (Join bảng, lọc đa tiêu chí, phân trang).
        // 2. Tham khảo LoaiSPDAO.java (Thao tác cơ bản trên 1 bảng).
        // 3. Quy trình thực hiện:
        //    - Tính offset: (page - 1) * limit.
        //    - SELECT COUNT(*) FROM KhachHang WHERE 1=1 ... (nối keyword) để lấy totalItems.
        //    - SELECT * FROM KhachHang WHERE 1=1 ... (nối keyword)
        //    - Thêm ORDER BY (tên/điểm) và phân trang OFFSET ? ROWS FETCH NEXT ? ROWS ONLY.
        return new PaginatedResponse<>(new ArrayList<>(), 1, 10, 0);
    }

    /**
     * Tìm kiếm khách hàng theo số điện thoại
     * @param sdt Số điện thoại khách hàng cần tìm
     * @return Optional<KhachHang>
     */
    public Optional<KhachHang> getBySdt(String sdt) {
        // TODO: Thực hiện câu lệnh SQL SELECT * FROM KhachHang WHERE sdt = ?
        return Optional.empty();
    }

    /**
     * Thêm mới một khách hàng vào cơ sở dữ liệu
     * @param kh Đối tượng khách hàng cần thêm
     * @return boolean true nếu thêm thành công, false nếu thất bại
     */
    public boolean add(KhachHang kh) {
        // TODO: Thực hiện câu lệnh SQL INSERT INTO KhachHang (sdt, ten, diem) VALUES (?, ?, ?)
        return false;
    }

    /**
     * Cập nhật thông tin khách hàng đã tồn tại
     * @param kh Đối tượng khách hàng với thông tin mới (sdt không đổi)
     * @return boolean true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean update(KhachHang kh) {
        // TODO: Thực hiện câu lệnh SQL UPDATE KhachHang SET ten = ?, diem = ? WHERE sdt = ?
        return false;
    }

    /**
     * Xóa khách hàng khỏi cơ sở dữ liệu theo số điện thoại
     * @param sdt Số điện thoại khách hàng cần xóa
     * @return boolean true nếu xóa thành công, false nếu thất bại
     */
    public boolean delete(String sdt) {
        // TODO: Thực hiện câu lệnh SQL DELETE FROM KhachHang WHERE sdt = ?
        // Lưu ý: Cần kiểm tra ràng buộc khóa ngoại (ví dụ: khách hàng đã có trong hóa đơn thì không được xóa)
        return false;
    }
}
