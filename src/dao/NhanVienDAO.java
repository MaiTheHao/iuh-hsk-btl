package dao;

import java.util.ArrayList;
import java.util.Optional;
import dto.NhanVienGetListCriteria;
import dto.PaginatedResponse;
import entity.NhanVien;

public class NhanVienDAO {
    private static NhanVienDAO instance = new NhanVienDAO();

    private NhanVienDAO() {
    }

    public static NhanVienDAO getInstance() {
        return instance;
    }

    /**
     * Lấy danh sách nhân viên có phân trang, lọc và sắp xếp
     * @param criteria Điều kiện lọc và phân trang
     * @return PaginatedResponse<NhanVien>
     */
    public PaginatedResponse<NhanVien> getList(NhanVienGetListCriteria criteria) {
        // TODO: HƯỚNG DẪN TRIỂN KHAI (Xóa sau khi hoàn thành):
        // 1. Tham khảo SanPhamDAO.java (Join bảng, lọc đa tiêu chí, phân trang).
        // 2. Tham khảo LoaiSPDAO.java (Thao tác cơ bản trên 1 bảng).
        // 3. Quy trình thực hiện:
        //    - Tính offset: (page - 1) * limit.
        //    - SELECT COUNT(*) FROM NhanVien WHERE 1=1 ... (nối keyword, loại NV) để lấy totalItems.
        //    - SELECT * FROM NhanVien WHERE 1=1 ... (nối keyword, loại NV)
        //    - Thêm ORDER BY và phân trang OFFSET ? ROWS FETCH NEXT ? ROWS ONLY.
        return new PaginatedResponse<>(new ArrayList<>(), 1, 10, 0);
    }

    /**
     * Tìm kiếm nhân viên theo mã
     * @param ma Mã nhân viên cần tìm
     * @return Optional<NhanVien>
     */
    public Optional<NhanVien> getByMa(String ma) {
        // TODO: Thực hiện câu lệnh SQL SELECT * FROM NhanVien WHERE ma = ?
        return Optional.empty();
    }

    /**
     * Thêm mới một nhân viên vào cơ sở dữ liệu
     * @param nv Đối tượng nhân viên cần thêm
     * @return boolean true nếu thêm thành công, false nếu thất bại
     */
    public boolean add(NhanVien nv) {
        // TODO: Thực hiện câu lệnh SQL INSERT INTO NhanVien (ma, ten, sdt, matKhau, loai) VALUES (?, ?, ?, ?, ?)
        return false;
    }

    /**
     * Cập nhật thông tin nhân viên đã tồn tại
     * @param nv Đối tượng nhân viên với thông tin mới (ma không đổi)
     * @return boolean true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean update(NhanVien nv) {
        // TODO: Thực hiện câu lệnh SQL UPDATE NhanVien SET ten = ?, sdt = ?, matKhau = ?, loai = ? WHERE ma = ?
        return false;
    }

    /**
     * Xóa nhân viên khỏi cơ sở dữ liệu theo mã
     * @param ma Mã nhân viên cần xóa
     * @return boolean true nếu xóa thành công, false nếu thất bại
     */
    public boolean delete(String ma) {
        // TODO: Thực hiện câu lệnh SQL DELETE FROM NhanVien WHERE ma = ?
        // Lưu ý: Cần kiểm tra ràng buộc (ví dụ: nhân viên đã lập hóa đơn thì không được xóa)
        return false;
    }
}
