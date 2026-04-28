package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import main.connectDB.ConnectDB;
import main.dto.NhanVienGetListCriteria;
import main.dto.PaginatedResponse;
import main.entity.NhanVien;
import main.enumeration.LoaiNV;

public class NhanVienDAO {
    private static NhanVienDAO instance = new NhanVienDAO();

    public static NhanVienDAO getInstance() {
        return instance;
    }

 
    public List<NhanVien> getList(String tuKhoa) {
        List<NhanVien> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM NhanVien WHERE 1=1 ");
        
        if (tuKhoa != null && !tuKhoa.isEmpty()) {
            sql.append("AND (ma LIKE ? OR ten LIKE ? OR sdt LIKE ?) ");
        }

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            if (tuKhoa != null && !tuKhoa.isEmpty()) {
                String kw = "%" + tuKhoa + "%";
                ps.setString(1, kw);
                ps.setString(2, kw);
                ps.setString(3, kw);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new NhanVien(
                    rs.getString("ma"),
                    rs.getString("ten"),
                    rs.getString("sdt"),
                    rs.getString("matKhau"),
                    LoaiNV.valueOf(rs.getString("loai")) 
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public Optional<NhanVien> getByMa(String ma) {
        String sql = "SELECT * FROM NhanVien WHERE ma = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new NhanVien(
                    rs.getString("ma"),
                    rs.getString("ten"),
                    rs.getString("sdt"),
                    rs.getString("matKhau"),
                    LoaiNV.valueOf(rs.getString("loai"))
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public boolean add(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (ma, ten, sdt, matKhau, loai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nv.getMa());
            ps.setString(2, nv.getTen());
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getMatKhau());
            ps.setString(5, nv.getLoai().name()); 
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET ten = ?, sdt = ?, matKhau = ?, loai = ? WHERE ma = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nv.getTen());
            ps.setString(2, nv.getSdt());
            ps.setString(3, nv.getMatKhau());
            ps.setString(4, nv.getLoai().name());
            ps.setString(5, nv.getMa());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

  
    public boolean delete(String ma) {
        String sql = "DELETE FROM NhanVien WHERE ma = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        return false;
    }
}