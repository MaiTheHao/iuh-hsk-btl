package main.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import main.connectDB.ConnectDB;
import main.dto.PaginatedResponse;
import main.dto.SanPhamGetListCriteria;
import main.entity.LoaiSP;
import main.entity.SanPham;
import main.enumeration.SortDirection;
import main.enumeration.TrangThaiSP;

public class SanPhamDAO {
    private static SanPhamDAO instance = new SanPhamDAO();

    private SanPhamDAO() {
    }

    public static SanPhamDAO getInstance() {
        return instance;
    }

    public PaginatedResponse<SanPham> getList(SanPhamGetListCriteria criteria) {
        List<SanPham> result = new ArrayList<>();
        Integer limit = criteria.limit();
        Integer page = criteria.page();
        int offset = (limit != null && limit > 0 && page != null && page > 0) ? (page - 1) * limit : 0;
        boolean isPaginate = (limit != null && limit > 0);

        StringBuilder whereQuery = new StringBuilder();
        if (criteria.maLoai() != null && !criteria.maLoai().isEmpty()) {
            whereQuery.append("AND sp.maLoai = ? ");
        }
        if (criteria.giaTu() != null) {
            whereQuery.append("AND sp.gia >= ? ");
        }
        if (criteria.giaDen() != null) {
            whereQuery.append("AND sp.gia <= ? ");
        }

        long totalItems = 0;
        try (Connection conn = ConnectDB.getConnection()) {
            String countSql = "SELECT COUNT(*) FROM SanPham sp WHERE 1=1 " + whereQuery;
            try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                int pIndex = 1;
                if (criteria.maLoai() != null && !criteria.maLoai().isEmpty()) psCount.setString(pIndex++, criteria.maLoai());
                if (criteria.giaTu() != null) psCount.setDouble(pIndex++, criteria.giaTu());
                if (criteria.giaDen() != null) psCount.setDouble(pIndex++, criteria.giaDen());
                ResultSet rsCount = psCount.executeQuery();
                if (rsCount.next()) totalItems = rsCount.getLong(1);
            }

            StringBuilder sql = new StringBuilder(
                "SELECT sp.*, l.ten AS tenLoai, l.moTa AS moTaLoai " +
                "FROM SanPham sp " +
                "LEFT JOIN LoaiSP l ON sp.maLoai = l.ma " +
                "WHERE 1=1 "
            );
            sql.append(whereQuery);

            StringBuilder orderBy = new StringBuilder();
            if (criteria.sapXepMa() != SortDirection.NONE) {
                orderBy.append("sp.ma ").append(criteria.sapXepMa());
            }
            if (criteria.sapXepGia() != SortDirection.NONE) {
                if (orderBy.length() > 0) orderBy.append(", ");
                orderBy.append("sp.gia ").append(criteria.sapXepGia());
            }

            if (orderBy.length() > 0) {
                sql.append("ORDER BY ").append(orderBy).append(" ");
            } else if (isPaginate) {
                sql.append("ORDER BY sp.ma ASC ");
            }

            if (isPaginate) {
                sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            }

            try (PreparedStatement psData = conn.prepareStatement(sql.toString())) {
                int pIndex = 1;
                if (criteria.maLoai() != null && !criteria.maLoai().isEmpty()) psData.setString(pIndex++, criteria.maLoai());
                if (criteria.giaTu() != null) psData.setDouble(pIndex++, criteria.giaTu());
                if (criteria.giaDen() != null) psData.setDouble(pIndex++, criteria.giaDen());
                
                if (isPaginate) {
                    psData.setInt(pIndex++, offset);
                    psData.setInt(pIndex++, limit);
                }

                ResultSet rs = psData.executeQuery();
                while (rs.next()) {
                    result.add(new SanPham(
                        rs.getString("ma"),
                        rs.getString("ten"),
                        rs.getString("moTa"),
                        rs.getString("anh"),
                        rs.getDouble("gia"),
                        rs.getInt("soLuong"),
                        new LoaiSP(rs.getString("maLoai"), rs.getString("tenLoai"), rs.getString("moTaLoai")),
                        TrangThaiSP.valueOf(rs.getString("trangThai"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PaginatedResponse<>(result, page != null ? page : 1, limit != null ? limit : result.size(), totalItems);
    }

    /**
     * Tìm kiếm sản phẩm theo mã
     * @param ma Mã sản phẩm cần tìm
     * @return Optional<SanPham>
     */
    public Optional<SanPham> getByMa(String ma) {
        // TODO: Thực hiện câu lệnh SQL SELECT * FROM SanPham sp LEFT JOIN LoaiSP l ON sp.maLoai = l.ma WHERE sp.ma = ?
        // Trả về Optional chứa đối tượng SanPham nếu tìm thấy, ngược lại trả về Optional.empty()
        return Optional.empty();
    }

    /**
     * Thêm mới một sản phẩm vào cơ sở dữ liệu
     * @param sp Đối tượng sản phẩm cần thêm
     * @return boolean true nếu thêm thành công, false nếu thất bại
     */
    public boolean add(SanPham sp) {
        // TODO: Thực hiện câu lệnh SQL INSERT INTO SanPham (...) VALUES (...)
        // Lưu ý: Kiểm tra tính toàn vẹn dữ liệu (mã loại sản phẩm phải tồn tại)
        return false;
    }

    /**
     * Cập nhật thông tin sản phẩm đã tồn tại
     * @param sp Đối tượng sản phẩm với thông tin mới (ma không đổi)
     * @return boolean true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean update(SanPham sp) {
        // TODO: Thực hiện câu lệnh SQL UPDATE SanPham SET ... WHERE ma = ?
        return false;
    }

    /**
     * Xóa sản phẩm khỏi cơ sở dữ liệu theo mã
     * @param ma Mã sản phẩm cần xóa
     * @return boolean true nếu xóa thành công, false nếu thất bại
     */
    public boolean delete(String ma) {
        // TODO: Thực hiện câu lệnh SQL DELETE FROM SanPham WHERE ma = ?
        // Lưu ý: Cần kiểm tra ràng buộc khóa ngoại (ví dụ: sản phẩm đã có trong hóa đơn thì không được xóa)
        return false;
    }
}
