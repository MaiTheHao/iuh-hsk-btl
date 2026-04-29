package main.java.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import main.java.connectDB.ConnectDB;
import main.java.dto.HoaDonGetListCriteria;
import main.java.dto.PaginatedResponse;
import main.java.entity.HoaDon;
import main.java.entity.KhachHang;
import main.java.entity.NhanVien;
import main.java.enumeration.TrangThaiHD;

public class HoaDonDAO {
    private static HoaDonDAO instance = new HoaDonDAO();

    private HoaDonDAO() {
    }

    public static HoaDonDAO getInstance() {
        return instance;
    }

    public PaginatedResponse<HoaDon> getList(HoaDonGetListCriteria criteria) {
        if (criteria == null) criteria = new HoaDonGetListCriteria();
        List<HoaDon> result = new ArrayList<>();
        StringBuilder whereQuery = new StringBuilder();

        if (criteria.getTuKhoa() != null && !criteria.getTuKhoa().isBlank()) {
            whereQuery.append("AND (ma LIKE ? OR sdtKH LIKE ?) ");
        }
        if (criteria.getTuNgay() != null) {
            whereQuery.append("AND ngayLap >= ? ");
        }
        if (criteria.getDenNgay() != null) {
            whereQuery.append("AND ngayLap <= ? ");
        }
        if (criteria.getMaNhanVien() != null && !criteria.getMaNhanVien().isBlank()) {
            whereQuery.append("AND maNV = ? ");
        }
        if (criteria.getSdtKhachHang() != null && !criteria.getSdtKhachHang().isBlank()) {
            whereQuery.append("AND sdtKH = ? ");
        }
        if (criteria.getTrangThai() != null) {
            whereQuery.append("AND trangThai = ? ");
        }

        long totalItems = 0;
        try (Connection conn = ConnectDB.getConnection()) {
            String countSql = "SELECT COUNT(*) FROM HoaDon WHERE 1=1 " + whereQuery;
            try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                int pIndex = 1;
                if (criteria.getTuKhoa() != null && !criteria.getTuKhoa().isBlank()) {
                    String pattern = "%" + criteria.getTuKhoa() + "%";
                    psCount.setString(pIndex++, pattern);
                    psCount.setString(pIndex++, pattern);
                }
                if (criteria.getTuNgay() != null) psCount.setTimestamp(pIndex++, Timestamp.valueOf(criteria.getTuNgay()));
                if (criteria.getDenNgay() != null) psCount.setTimestamp(pIndex++, Timestamp.valueOf(criteria.getDenNgay()));
                if (criteria.getMaNhanVien() != null && !criteria.getMaNhanVien().isBlank()) psCount.setString(pIndex++, criteria.getMaNhanVien());
                if (criteria.getSdtKhachHang() != null && !criteria.getSdtKhachHang().isBlank()) psCount.setString(pIndex++, criteria.getSdtKhachHang());
                if (criteria.getTrangThai() != null) psCount.setString(pIndex++, criteria.getTrangThai().name());

                ResultSet rsCount = psCount.executeQuery();
                if (rsCount.next()) totalItems = rsCount.getLong(1);
            }

            StringBuilder sql = new StringBuilder("SELECT * FROM HoaDon WHERE 1=1 ");
            sql.append(whereQuery);
            sql.append("ORDER BY ngayLap DESC ");

            if (criteria.isPaginate()) {
                sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            }

            try (PreparedStatement psData = conn.prepareStatement(sql.toString())) {
                int pIndex = 1;
                if (criteria.getTuKhoa() != null && !criteria.getTuKhoa().isBlank()) {
                    String pattern = "%" + criteria.getTuKhoa() + "%";
                    psData.setString(pIndex++, pattern);
                    psData.setString(pIndex++, pattern);
                }
                if (criteria.getTuNgay() != null) psData.setTimestamp(pIndex++, Timestamp.valueOf(criteria.getTuNgay()));
                if (criteria.getDenNgay() != null) psData.setTimestamp(pIndex++, Timestamp.valueOf(criteria.getDenNgay()));
                if (criteria.getMaNhanVien() != null && !criteria.getMaNhanVien().isBlank()) psData.setString(pIndex++, criteria.getMaNhanVien());
                if (criteria.getSdtKhachHang() != null && !criteria.getSdtKhachHang().isBlank()) psData.setString(pIndex++, criteria.getSdtKhachHang());
                if (criteria.getTrangThai() != null) psData.setString(pIndex++, criteria.getTrangThai().name());

                if (criteria.isPaginate()) {
                    psData.setInt(pIndex++, criteria.getOffset());
                    psData.setInt(pIndex++, criteria.getLimit());
                }

                ResultSet rs = psData.executeQuery();
                while (rs.next()) {
                    result.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PaginatedResponse<>(result, criteria.getPage(), criteria.getLimit() != null ? criteria.getLimit() : result.size(), totalItems);
    }

    public List<HoaDon> getAll() {
        List<HoaDon> result = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon ORDER BY ngayLap DESC";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Optional<HoaDon> getByMa(String ma) {
        String sql = "SELECT * FROM HoaDon WHERE ma = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean add(HoaDon hd) {
        String sql = "INSERT INTO HoaDon (ma, maNV, sdtKH, ngayLap, tongTien, vat, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hd.getMa());
            ps.setString(2, hd.getNhanVien() != null ? hd.getNhanVien().getMa() : null);
            ps.setString(3, hd.getKhachHang() != null ? hd.getKhachHang().getSdt() : null);
            ps.setTimestamp(4, Timestamp.valueOf(hd.getNgayLap() != null ? hd.getNgayLap() : LocalDateTime.now()));
            ps.setDouble(5, hd.getTongTien());
            ps.setDouble(6, hd.getVat());
            ps.setString(7, hd.getTrangThai() != null ? hd.getTrangThai().name() : TrangThaiHD.PAID.name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String ma) {
        String sql = "DELETE FROM HoaDon WHERE ma = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAllByMaHD(List<String> listMaHD) {
        if (listMaHD == null || listMaHD.isEmpty()) return false;
        String placeholders = String.join(",", listMaHD.stream().map(id -> "?").toArray(String[]::new));
        String sql = "DELETE FROM HoaDon WHERE ma IN (" + placeholders + ")";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            for (int i = 0; i < listMaHD.size(); i++) {
                ps.setString(i + 1, listMaHD.get(i));
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private HoaDon mapResultSetToEntity(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon();
        hd.setMa(rs.getString("ma"));
        
        String maNV = rs.getString("maNV");
        if (maNV != null) {
            hd.setNhanVien(new NhanVien(maNV));
        }
        
        String sdtKH = rs.getString("sdtKH");
        if (sdtKH != null) {
            hd.setKhachHang(new KhachHang(sdtKH));
        }
        
        Timestamp ts = rs.getTimestamp("ngayLap");
        if (ts != null) {
            hd.setNgayLap(ts.toLocalDateTime());
        }
        
        hd.setTongTien(rs.getDouble("tongTien"));
        hd.setVat(rs.getDouble("vat"));
        hd.setTrangThai(TrangThaiHD.fromString(rs.getString("trangThai")));
        
        return hd;
    }
}
