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

    public Optional<SanPham> getByMa(String ma) {
        String sql = "SELECT sp.*, l.ten AS tenLoai, l.moTa AS moTaLoai " +
                     "FROM SanPham sp " +
                     "LEFT JOIN LoaiSP l ON sp.maLoai = l.ma " +
                     "WHERE sp.ma = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SanPham sp = new SanPham(
                    rs.getString("ma"),
                    rs.getString("ten"),
                    rs.getString("moTa"),
                    rs.getString("anh"),
                    rs.getDouble("gia"),
                    rs.getInt("soLuong"),
                    new LoaiSP(rs.getString("maLoai"), rs.getString("tenLoai"), rs.getString("moTaLoai")),
                    TrangThaiSP.valueOf(rs.getString("trangThai"))
                );
                return Optional.of(sp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean add(SanPham sp) {
        String sql = "INSERT INTO SanPham (ma, ten, moTa, anh, gia, soLuong, maLoai, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sp.getMa());
            ps.setString(2, sp.getTen());
            ps.setString(3, sp.getMoTa());
            ps.setString(4, sp.getAnh());
            ps.setDouble(5, sp.getGia());
            ps.setInt(6, sp.getSoLuong());
            ps.setString(7, sp.getLoai().getMa());
            ps.setString(8, sp.getTrangThai().name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(SanPham sp) {
        String sql = "UPDATE SanPham SET ten = ?, moTa = ?, anh = ?, gia = ?, soLuong = ?, maLoai = ?, trangThai = ? WHERE ma = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sp.getTen());
            ps.setString(2, sp.getMoTa());
            ps.setString(3, sp.getAnh());
            ps.setDouble(4, sp.getGia());
            ps.setInt(5, sp.getSoLuong());
            ps.setString(6, sp.getLoai().getMa());
            ps.setString(7, sp.getTrangThai().name());
            ps.setString(8, sp.getMa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String ma) {
        String sql = "DELETE FROM SanPham WHERE ma = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
        return false;
    }
}