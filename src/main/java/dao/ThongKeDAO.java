package main.java.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import main.java.connectDB.ConnectDB;
import main.java.dto.DoanhThuNgayDTO;
import main.java.dto.ThongKeTongQuanDTO;
import main.java.dto.TopSanPhamDTO;

public class ThongKeDAO {
    private static ThongKeDAO instance = new ThongKeDAO();

    private ThongKeDAO() {}

    public static ThongKeDAO getInstance() {
        return instance;
    }

    /**
     * Lấy dữ liệu tổng quan cho Dashboard
     */
    public ThongKeTongQuanDTO getTongQuan() {
        String sql = "{call sp_GetTongQuan}";
        try (Connection conn = ConnectDB.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            if (rs.next()) {
                return new ThongKeTongQuanDTO(
                    rs.getDouble("DoanhThuHomNay"),
                    rs.getInt("SoHoaDonHomNay"),
                    rs.getInt("SoSPCanNhap"),
                    rs.getDouble("DoanhThuThangNay")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ThongKeTongQuanDTO(0, 0, 0, 0);
    }

    /**
     * Lấy doanh thu theo ngày trong khoảng thời gian
     */
    public List<DoanhThuNgayDTO> getDoanhThuTheoNgay(LocalDateTime start, LocalDateTime end) {
        List<DoanhThuNgayDTO> result = new ArrayList<>();
        String sql = "{call sp_GetDoanhThuTheoNgay(?, ?)}";
        try (Connection conn = ConnectDB.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setTimestamp(1, Timestamp.valueOf(start));
            cs.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    result.add(new DoanhThuNgayDTO(
                        rs.getDate("Ngay").toLocalDate(),
                        rs.getInt("SoHoaDon"),
                        rs.getDouble("DoanhThu")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy top sản phẩm bán chạy
     */
    public List<TopSanPhamDTO> getTopSanPhamBanChay(LocalDateTime start, LocalDateTime end, int limit) {
        List<TopSanPhamDTO> result = new ArrayList<>();
        String sql = "{call sp_GetTopSanPhamBanChay(?, ?, ?)}";
        try (Connection conn = ConnectDB.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setTimestamp(1, Timestamp.valueOf(start));
            cs.setTimestamp(2, Timestamp.valueOf(end));
            cs.setInt(3, limit);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    result.add(new TopSanPhamDTO(
                        rs.getString("MaSP"),
                        rs.getString("TenSP"),
                        rs.getInt("SoLuongDaBan"),
                        rs.getDouble("TongTien")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
