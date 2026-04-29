package main.java.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.java.connectDB.ConnectDB;
import main.java.entity.ChiTietHD;
import main.java.entity.HoaDon;
import main.java.entity.SanPham;

public class ChiTietHDDAO {
    private static ChiTietHDDAO instance = new ChiTietHDDAO();

    private ChiTietHDDAO() {
    }

    public static ChiTietHDDAO getInstance() {
        return instance;
    }

    public List<ChiTietHD> getByMaHD(String maHD) {
        List<ChiTietHD> result = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHD WHERE maHD = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToEntity(rs));
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

    private ChiTietHD mapResultSetToEntity(ResultSet rs) throws SQLException {
        ChiTietHD ct = new ChiTietHD();
        ct.setHoaDon(new HoaDon(rs.getString("maHD")));
        ct.setSanPham(new SanPham(rs.getString("maSP")));
        ct.setSoLuong(rs.getInt("soLuong"));
        ct.setDonGia(rs.getDouble("donGia"));
        ct.setThanhTien(rs.getDouble("thanhTien"));
        return ct;
    }
}
