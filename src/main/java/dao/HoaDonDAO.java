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
import main.java.enumeration.LoaiNV;
import main.java.enumeration.TrangThaiHD;

/**
 * @author: Trần Thanh Nhựt
 */
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
            whereQuery.append("AND (hd.ma LIKE ? OR hd.sdtKH LIKE ?) ");
        }
        if (criteria.getTuNgay() != null) {
            whereQuery.append("AND hd.ngayLap >= ? ");
        }
        if (criteria.getDenNgay() != null) {
            whereQuery.append("AND hd.ngayLap <= ? ");
        }
        if (criteria.getMaNhanVien() != null && !criteria.getMaNhanVien().isBlank()) {
            whereQuery.append("AND hd.maNV = ? ");
        }
        if (criteria.getSdtKhachHang() != null && !criteria.getSdtKhachHang().isBlank()) {
            whereQuery.append("AND hd.sdtKH = ? ");
        }
        if (criteria.getTrangThai() != null) {
            whereQuery.append("AND hd.trangThai = ? ");
        }

        long totalItems = 0;
        try (Connection conn = ConnectDB.getConnection()) {
            String countSql = "SELECT COUNT(*) FROM HoaDon hd WHERE 1=1 " + whereQuery;
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

            StringBuilder sql = new StringBuilder(
                "SELECT hd.*, " +
                "nv.ten AS nvTen, nv.sdt AS nvSdt, nv.matKhau AS nvMatKhau, nv.anh AS nvAnh, nv.loai AS nvLoai, " +
                "kh.ten AS khTen, kh.diem AS khDiem " +
                "FROM HoaDon hd " +
                "LEFT JOIN NhanVien nv ON hd.maNV = nv.ma " +
                "LEFT JOIN KhachHang kh ON hd.sdtKH = kh.sdt " +
                "WHERE 1=1 "
            );
            sql.append(whereQuery);
            sql.append("ORDER BY hd.ngayLap DESC ");

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
                    result.add(rsToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new PaginatedResponse<>(result, criteria.getPage(), criteria.getLimit() != null ? criteria.getLimit() : result.size(), totalItems);
    }

    public List<HoaDon> getAll() {
        List<HoaDon> result = new ArrayList<>();
        String sql = "SELECT hd.*, " +
                     "nv.ten AS nvTen, nv.sdt AS nvSdt, nv.matKhau AS nvMatKhau, nv.anh AS nvAnh, nv.loai AS nvLoai, " +
                     "kh.ten AS khTen, kh.diem AS khDiem " +
                     "FROM HoaDon hd " +
                     "LEFT JOIN NhanVien nv ON hd.maNV = nv.ma " +
                     "LEFT JOIN KhachHang kh ON hd.sdtKH = kh.sdt " +
                     "ORDER BY hd.ngayLap DESC";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(rsToEntity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Optional<HoaDon> getByMa(String ma) {
        String sql = "SELECT hd.*, " +
                     "nv.ten AS nvTen, nv.sdt AS nvSdt, nv.matKhau AS nvMatKhau, nv.anh AS nvAnh, nv.loai AS nvLoai, " +
                     "kh.ten AS khTen, kh.diem AS khDiem " +
                     "FROM HoaDon hd " +
                     "LEFT JOIN NhanVien nv ON hd.maNV = nv.ma " +
                     "LEFT JOIN KhachHang kh ON hd.sdtKH = kh.sdt " +
                     "WHERE hd.ma = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rsToEntity(rs));
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

    public boolean updateTrangThai(String ma, TrangThaiHD trangThai) {
        String sql = "UPDATE HoaDon SET trangThai = ? WHERE ma = ?";
        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, trangThai.name());
            ps.setString(2, ma);
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



    private HoaDon rsToEntity(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon();
        hd.setMa(rs.getString("ma"));
        
        String maNV = rs.getString("maNV");
        if (maNV != null) {
            hd.setNhanVien(new NhanVien(
                maNV,
                rs.getString("nvTen"),
                rs.getString("nvSdt"),
                rs.getString("nvMatKhau"),
                rs.getString("nvAnh"),
                LoaiNV.fromString(rs.getString("nvLoai"))
            ));
        }
        
        String sdtKH = rs.getString("sdtKH");
        if (sdtKH != null) {
            hd.setKhachHang(new KhachHang(
                sdtKH,
                rs.getString("khTen"),
                rs.getInt("khDiem")
            ));
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
