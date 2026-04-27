package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import connectDB.ConnectDB;
import dto.KhachHangGetListCriteria;
import dto.PaginatedResponse;
import entity.KhachHang;
import enumeration.SortDirection;

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
        
        List<KhachHang> result = new ArrayList<>();
        Integer limit = criteria.limit();
        Integer page = criteria.page();
        int offset = (limit != null && limit > 0 && page != null && page > 0) ? (page - 1) * limit : 0;
        boolean isPaginate = (limit != null && limit > 0);

        StringBuilder whereQuery = new StringBuilder();
        if (criteria.tuKhoa() != null && !criteria.tuKhoa().isEmpty()) {
            whereQuery.append("AND (ten LIKE ? OR sdt LIKE ?) ");
        }

        long totalItems = 0;
        try (Connection conn = ConnectDB.getConnection()) {
            String countSql = "SELECT COUNT(*) FROM KhachHang WHERE 1=1 " + whereQuery;
            try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                int pIndex = 1;
                if (criteria.tuKhoa() != null && !criteria.tuKhoa().isEmpty()) {
                    psCount.setString(pIndex++, "%" + criteria.tuKhoa() + "%");
                    psCount.setString(pIndex++, "%" + criteria.tuKhoa() + "%");
                }
                ResultSet rsCount = psCount.executeQuery();
                if (rsCount.next()) totalItems = rsCount.getLong(1);
            }

            StringBuilder sql = new StringBuilder("SELECT * FROM KhachHang WHERE 1=1 ");
            sql.append(whereQuery);

            StringBuilder orderBy = new StringBuilder();
            if (criteria.sapXepTen() != SortDirection.NONE) {
                orderBy.append("ten ").append(criteria.sapXepTen());
            }
            if (criteria.sapXepDiem() != SortDirection.NONE) {
                if (orderBy.length() > 0) orderBy.append(", ");
                orderBy.append("diem ").append(criteria.sapXepDiem());
            }

            if (orderBy.length() > 0) {
                sql.append("ORDER BY ").append(orderBy).append(" ");
            } else if (isPaginate) {
                sql.append("ORDER BY ten ASC ");
            }

            if (isPaginate) {
                sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            }

            try (PreparedStatement psData = conn.prepareStatement(sql.toString())) {
                int pIndex = 1;
                if (criteria.tuKhoa() != null && !criteria.tuKhoa().isEmpty()) {
                    psData.setString(pIndex++, "%" + criteria.tuKhoa() + "%");
                    psData.setString(pIndex++, "%" + criteria.tuKhoa() + "%");
                }
                
                if (isPaginate) {
                    psData.setInt(pIndex++, offset);
                    psData.setInt(pIndex++, limit);
                }

                ResultSet rs = psData.executeQuery();
                while (rs.next()) {
                    result.add(new KhachHang(
                        rs.getString("sdt"),
                        rs.getString("ten"),
                        rs.getInt("diem")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PaginatedResponse<>(result, page != null ? page : 1, limit != null ? limit : result.size(), totalItems);
    }

    /**
     * Tìm kiếm khách hàng theo số điện thoại
     * @param sdt Số điện thoại khách hàng cần tìm
     * @return Optional<KhachHang>
     */
    public Optional<KhachHang> getBySdt(String sdt) {
        // TODO: Thực hiện câu lệnh SQL SELECT * FROM KhachHang WHERE sdt = ?
        String sql = "SELECT * FROM KhachHang WHERE sdt = ?";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new KhachHang(rs.getString("sdt"), rs.getString("ten"), rs.getInt("diem")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Thêm mới một khách hàng vào cơ sở dữ liệu
     * @param kh Đối tượng khách hàng cần thêm
     * @return boolean true nếu thêm thành công, false nếu thất bại
     */
    public boolean add(KhachHang kh) {
        // TODO: Thực hiện câu lệnh SQL INSERT INTO KhachHang (sdt, ten, diem) VALUES (?, ?, ?)
        String sql = "INSERT INTO KhachHang (sdt, ten, diem) VALUES (?, ?, ?)";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, kh.getSdt());
            ps.setString(2, kh.getTen());
            ps.setInt(3, kh.getDiem());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật thông tin khách hàng đã tồn tại
     * @param kh Đối tượng khách hàng với thông tin mới (sdt không đổi)
     * @return boolean true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean update(KhachHang kh) {
        // TODO: Thực hiện câu lệnh SQL UPDATE KhachHang SET ten = ?, diem = ? WHERE sdt = ?
        String sql = "UPDATE KhachHang SET ten = ?, diem = ? WHERE sdt = ?";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, kh.getTen());
            ps.setInt(2, kh.getDiem());
            ps.setString(3, kh.getSdt());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        String sql = "DELETE FROM KhachHang WHERE sdt = ?";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, sdt);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}