package main.java.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.java.connectDB.ConnectDB;
import main.java.entity.ChiTietHD;
import main.java.entity.HoaDon;
import main.java.entity.LoaiSP;
import main.java.entity.SanPham;

/**
 * @author: Trần Thanh Nhựt
 */
public class ChiTietHDDAO {
    private static ChiTietHDDAO instance = new ChiTietHDDAO();

    private ChiTietHDDAO() {
    }

    public static ChiTietHDDAO getInstance() {
        return instance;
    }

    public List<ChiTietHD> getByMaHD(String maHD) {
        List<ChiTietHD> result = new ArrayList<>();
        String sql = "SELECT ct.*, " +
                     "sp.ten AS tenSP, sp.moTa AS moTaSP, sp.anh AS anhSP, sp.gia AS giaSP, sp.soLuong AS soLuongSP, sp.trangThai AS trangThaiSP, " +
                     "l.ma AS maLoai, l.ten AS tenLoai, l.moTa AS moTaLoai " +
                     "FROM ChiTietHD ct " +
                     "JOIN SanPham sp ON ct.maSP = sp.ma " +
                     "LEFT JOIN LoaiSP l ON sp.maLoai = l.ma " +
                     "WHERE ct.maHD = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(rsToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean add(ChiTietHD ct) {
        String sql = "INSERT INTO ChiTietHD (maHD, maSP, soLuong, donGia, thanhTien) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ct.getHoaDon().getMa());
            ps.setString(2, ct.getSanPham().getMa());
            ps.setInt(3, ct.getSoLuong());
            ps.setDouble(4, ct.getDonGia());
            ps.setDouble(5, ct.getThanhTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAllByMaHD(List<String> listMaHD) {
        if (listMaHD == null || listMaHD.isEmpty()) return false;
        String placeholders = String.join(",", listMaHD.stream().map(id -> "?").toArray(String[]::new));
        String sql = "DELETE FROM ChiTietHD WHERE maHD IN (" + placeholders + ")";
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

    private ChiTietHD rsToEntity(ResultSet rs) throws SQLException {
        ChiTietHD ct = new ChiTietHD();
        ct.setHoaDon(new HoaDon(rs.getString("maHD")));
        
        SanPham sp = new SanPham(
            rs.getString("maSP"),
            rs.getString("tenSP"),
            rs.getString("moTaSP"),
            rs.getString("anhSP"),
            rs.getDouble("giaSP"),
            rs.getInt("soLuongSP"),
            new LoaiSP(rs.getString("maLoai"), rs.getString("tenLoai"), rs.getString("moTaLoai")),
            main.java.enumeration.TrangThaiSP.fromString(rs.getString("trangThaiSP"))
        );
        ct.setSanPham(sp);
        
        ct.setSoLuong(rs.getInt("soLuong"));
        ct.setDonGia(rs.getDouble("donGia"));
        ct.setThanhTien(rs.getDouble("thanhTien"));
        return ct;
    }
}
