package main.java.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import main.java.connectDB.ConnectDB;
import main.java.dto.NhanVienGetListCriteria;
import main.java.dto.PaginatedResponse;
import main.java.entity.NhanVien;
import main.java.enumeration.LoaiNV;
import main.java.enumeration.SortDirection;

public class NhanVienDAO {
    private static NhanVienDAO instance = new NhanVienDAO();

    private NhanVienDAO() {
    }

    public static NhanVienDAO getInstance() {
        return instance;
    }

    public PaginatedResponse<NhanVien> getList(NhanVienGetListCriteria criteria) {
        if (criteria == null) criteria = new NhanVienGetListCriteria();
        List<NhanVien> result = new ArrayList<>();

        StringBuilder whereQuery = new StringBuilder();
        if (criteria.getTuKhoa() != null && !criteria.getTuKhoa().isBlank()) {
            whereQuery.append("AND (ma LIKE ? OR ten LIKE ? OR sdt LIKE ?) ");
        }
        if (criteria.getLoai() != null) {
            whereQuery.append("AND loai = ? ");
        }

        long totalItems = 0;
        try (Connection conn = ConnectDB.getConnection()) {
            String countSql = "SELECT COUNT(*) FROM NhanVien WHERE 1=1 " + whereQuery;
            try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                int pIndex = 1;
                if (criteria.getTuKhoa() != null && !criteria.getTuKhoa().isBlank()) {
                    String pattern = "%" + criteria.getTuKhoa() + "%";
                    psCount.setString(pIndex++, pattern);
                    psCount.setString(pIndex++, pattern);
                    psCount.setString(pIndex++, pattern);
                }
                if (criteria.getLoai() != null) {
                    psCount.setString(pIndex++, criteria.getLoai().name());
                }
                ResultSet rsCount = psCount.executeQuery();
                if (rsCount.next()) totalItems = rsCount.getLong(1);
            }

            StringBuilder sql = new StringBuilder("SELECT * FROM NhanVien WHERE 1=1 ");
            sql.append(whereQuery);

            if (criteria.getSapXepTen() != SortDirection.NONE) {
                sql.append("ORDER BY ten ").append(criteria.getSapXepTen()).append(" ");
            } else if (criteria.isPaginate()) {
                sql.append("ORDER BY ma ASC ");
            }

            if (criteria.isPaginate()) {
                sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            }

            try (PreparedStatement psData = conn.prepareStatement(sql.toString())) {
                int pIndex = 1;
                if (criteria.getTuKhoa() != null && !criteria.getTuKhoa().isBlank()) {
                    String pattern = "%" + criteria.getTuKhoa() + "%";
                    psData.setString(pIndex++, pattern);
                    psData.setString(pIndex++, pattern);
                    psData.setString(pIndex++, pattern);
                }
                if (criteria.getLoai() != null) {
                    psData.setString(pIndex++, criteria.getLoai().name());
                }
                if (criteria.isPaginate()) {
                    psData.setInt(pIndex++, criteria.getOffset());
                    psData.setInt(pIndex++, criteria.getLimit());
                }

                ResultSet rs = psData.executeQuery();
                while (rs.next()) {
                    result.add(new NhanVien(
                        rs.getString("ma"),
                        rs.getString("ten"),
                        rs.getString("sdt"),
                        rs.getString("matKhau"),
                        rs.getString("anh"),
                        LoaiNV.fromString(rs.getString("loai"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PaginatedResponse<>(result, criteria.getPage(), criteria.getLimit() != null ? criteria.getLimit() : result.size(), totalItems);
    }

    public Optional<NhanVien> getBySdt(String sdt) {
        String sql = "SELECT * FROM NhanVien WHERE sdt = ?";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new NhanVien(
                    rs.getString("ma"),
                    rs.getString("ten"),
                    rs.getString("sdt"),
                    rs.getString("matKhau"),
                    rs.getString("anh"),
                    LoaiNV.fromString(rs.getString("loai"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<NhanVien> getByMa(String ma) {
        String sql = "SELECT * FROM NhanVien WHERE ma = ?";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new NhanVien(
                    rs.getString("ma"),
                    rs.getString("ten"),
                    rs.getString("sdt"),
                    rs.getString("matKhau"),
                    rs.getString("anh"),
                    LoaiNV.fromString(rs.getString("loai"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean add(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (ma, ten, sdt, matKhau, anh, loai) VALUES (?, ?, ?, ?, ?, ?)";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, nv.getMa());
            ps.setString(2, nv.getTen());
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getMatKhau());
            ps.setString(5, nv.getAnh());
            ps.setString(6, nv.getLoai().name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET ten = ?, sdt = ?, matKhau = ?, anh = ?, loai = ? WHERE ma = ?";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, nv.getTen());
            ps.setString(2, nv.getSdt());
            ps.setString(3, nv.getMatKhau());
            ps.setString(4, nv.getAnh());
            ps.setString(5, nv.getLoai().name());
            ps.setString(6, nv.getMa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String ma) {
        String sql = "DELETE FROM NhanVien WHERE ma = ?";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Optional<NhanVien> authenticate(String sdt, String matKhau) {
        String sql = "SELECT * FROM NhanVien WHERE sdt = ? AND matKhau = ?";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, sdt);
            ps.setString(2, matKhau);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new NhanVien(
                    rs.getString("ma"),
                    rs.getString("ten"),
                    rs.getString("sdt"),
                    rs.getString("matKhau"),
                    rs.getString("anh"),
                    LoaiNV.fromString(rs.getString("loai"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public boolean deleteAllByMaNV(List<String> listMaNV) {
        if (listMaNV == null || listMaNV.isEmpty()) return false;
        String placeholders = String.join(",", listMaNV.stream().map(id -> "?").toArray(String[]::new));
        String sql = "DELETE FROM NhanVien WHERE ma IN (" + placeholders + ")";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            for (int i = 0; i < listMaNV.size(); i++) {
                ps.setString(i + 1, listMaNV.get(i));
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
