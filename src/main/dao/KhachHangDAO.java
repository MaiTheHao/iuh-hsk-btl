package main.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import main.connectDB.ConnectDB;
import main.dto.KhachHangGetListCriteria;
import main.dto.PaginatedResponse;
import main.entity.KhachHang;
import main.enumeration.SortDirection;

public class KhachHangDAO {
    private static KhachHangDAO instance = new KhachHangDAO();

    private KhachHangDAO() {
    }

    public static KhachHangDAO getInstance() {
        return instance;
    }

    public PaginatedResponse<KhachHang> getList(KhachHangGetListCriteria criteria) {
        
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

    public Optional<KhachHang> getBySdt(String sdt) {
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

    public boolean add(KhachHang kh) {
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

    public boolean update(KhachHang kh) {
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

    public boolean delete(String sdt) {
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