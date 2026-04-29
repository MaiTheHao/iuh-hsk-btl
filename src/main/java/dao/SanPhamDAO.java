package main.java.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import main.java.connectDB.ConnectDB;
import main.java.dto.PaginatedResponse;
import main.java.dto.SanPhamGetListCriteria;
import main.java.entity.LoaiSP;
import main.java.entity.SanPham;
import main.java.enumeration.SortDirection;
import main.java.enumeration.TrangThaiSP;

public class SanPhamDAO {
    private static SanPhamDAO instance = new SanPhamDAO();

    private SanPhamDAO() {
    }

    public static SanPhamDAO getInstance() {
        return instance;
    }

    public PaginatedResponse<SanPham> getList(SanPhamGetListCriteria criteria) {
        List<SanPham> result = new ArrayList<>();

        StringBuilder whereQuery = new StringBuilder();
        if (criteria.getMaLoai() != null && !criteria.getMaLoai().isEmpty()) {
            whereQuery.append("AND sp.maLoai = ? ");
        }
        if (criteria.getTuKhoa() != null && !criteria.getTuKhoa().isEmpty()) {
            whereQuery.append("AND (sp.ten LIKE ? OR sp.moTa LIKE ?) ");
        }
        if (criteria.getGiaTu() != null) {
            whereQuery.append("AND sp.gia >= ? ");
        }
        if (criteria.getGiaDen() != null) {
            whereQuery.append("AND sp.gia <= ? ");
        }

        long totalItems = 0;
        try (Connection conn = ConnectDB.getConnection()) {
            String countSql = "SELECT COUNT(*) FROM SanPham sp WHERE 1=1 " + whereQuery;
            try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                int pIndex = 1;
                if (criteria.getMaLoai() != null && !criteria.getMaLoai().isEmpty()) psCount.setString(pIndex++, criteria.getMaLoai());
                if (criteria.getTuKhoa() != null && !criteria.getTuKhoa().isEmpty()) {
                    psCount.setString(pIndex++, "%" + criteria.getTuKhoa() + "%");
                    psCount.setString(pIndex++, "%" + criteria.getTuKhoa() + "%");
                }
                if (criteria.getGiaTu() != null) psCount.setDouble(pIndex++, criteria.getGiaTu());
                if (criteria.getGiaDen() != null) psCount.setDouble(pIndex++, criteria.getGiaDen());
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
            if (criteria.getSapXepMa() != SortDirection.NONE) {
                orderBy.append("sp.ma ").append(criteria.getSapXepMa());
            }
            if (criteria.getSapXepGia() != SortDirection.NONE) {
                if (orderBy.length() > 0) orderBy.append(", ");
                orderBy.append("sp.gia ").append(criteria.getSapXepGia());
            }

            if (orderBy.length() > 0) {
                sql.append("ORDER BY ").append(orderBy).append(" ");
            } else if (criteria.isPaginate()) {
                sql.append("ORDER BY sp.ma ASC ");
            }

            if (criteria.isPaginate()) {
                sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            }

            try (PreparedStatement psData = conn.prepareStatement(sql.toString())) {
                int pIndex = 1;
                if (criteria.getMaLoai() != null && !criteria.getMaLoai().isEmpty()) psData.setString(pIndex++, criteria.getMaLoai());
                if (criteria.getGiaTu() != null) psData.setDouble(pIndex++, criteria.getGiaTu());
                if (criteria.getGiaDen() != null) psData.setDouble(pIndex++, criteria.getGiaDen());
                
                if (criteria.isPaginate()) {
                    psData.setInt(pIndex++, criteria.getOffset());
                    psData.setInt(pIndex++, criteria.getLimit());
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
                        TrangThaiSP.fromString(rs.getString("trangThai"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PaginatedResponse<>(result, criteria.getPage(), criteria.getLimit() != null ? criteria.getLimit() : result.size(), totalItems);
    }

    public Optional<SanPham> getByMa(String ma) {
        String sql = "SELECT sp.*, l.ten AS tenLoai, l.moTa AS moTaLoai FROM SanPham sp LEFT JOIN LoaiSP l ON sp.maLoai = l.ma WHERE sp.ma = ?";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new SanPham(
                    rs.getString("ma"),
                    rs.getString("ten"),
                    rs.getString("moTa"),
                    rs.getString("anh"),
                    rs.getDouble("gia"),
                    rs.getInt("soLuong"),
                    new LoaiSP(rs.getString("maLoai"), rs.getString("tenLoai"), rs.getString("moTaLoai")),
                    TrangThaiSP.fromString(rs.getString("trangThai"))
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean add(SanPham sp) {
        String sql = "INSERT INTO SanPham (ma, ten, moTa, anh, gia, soLuong, maLoai, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, sp.getMa());
            ps.setString(2, sp.getTen());
            ps.setString(3, sp.getMoTa());
            ps.setString(4, sp.getAnh());
            ps.setDouble(5, sp.getGia());
            ps.setInt(6, sp.getSoLuong());
            ps.setString(7, sp.getLoai().getMa());
            ps.setString(8, sp.getTrangThai().name());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(SanPham sp) {
        String sql = "UPDATE SanPham SET ten = ?, moTa = ?, anh = ?, gia = ?, soLuong = ?, maLoai = ?, trangThai = ? WHERE ma = ?";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, sp.getTen());
            ps.setString(2, sp.getMoTa());
            ps.setString(3, sp.getAnh());
            ps.setDouble(4, sp.getGia());
            ps.setInt(5, sp.getSoLuong());
            ps.setString(6, sp.getLoai().getMa());
            ps.setString(7, sp.getTrangThai().name());
            ps.setString(8, sp.getMa());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String ma) {
        String sql = "DELETE FROM SanPham WHERE ma = ?";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteAllByMaSP(List<String> listMaSP) {
        if (listMaSP == null || listMaSP.isEmpty()) return false;
        String placeholders = String.join(",", listMaSP.stream().map(id -> "?").toArray(String[]::new));
        String sql = "DELETE FROM SanPham WHERE ma IN (" + placeholders + ")";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            for (int i = 0; i < listMaSP.size(); i++) {
                ps.setString(i + 1, listMaSP.get(i));
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
