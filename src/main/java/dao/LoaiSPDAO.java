package main.java.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import main.java.connectDB.ConnectDB;
import main.java.dto.LoaiSPGetListCriteria;
import main.java.dto.PaginatedResponse;
import main.java.entity.LoaiSP;
import main.java.enumeration.SortDirection;

public class LoaiSPDAO {
  private static LoaiSPDAO instance = new LoaiSPDAO();

  private LoaiSPDAO() {}

  public static LoaiSPDAO getInstance() {
    return instance;
  }

  public PaginatedResponse<LoaiSP> getList(LoaiSPGetListCriteria criteria) {
    List<LoaiSP> result = new ArrayList<>();
    
    long totalItems = 0;
    try (Connection conn = ConnectDB.getConnection()) {
      String countSql = "SELECT COUNT(*) FROM LoaiSP";
      try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
        ResultSet rsCount = psCount.executeQuery();
        if (rsCount.next()) totalItems = rsCount.getLong(1);
      }

      StringBuilder sql = new StringBuilder("SELECT * FROM LoaiSP ");

      StringBuilder orderBy = new StringBuilder();
      if (criteria.getSapXepMa() != SortDirection.NONE) {
        orderBy.append("ma ").append(criteria.getSapXepMa());
      }
      if (criteria.getSapXepTen() != SortDirection.NONE) {
        if (orderBy.length() > 0) orderBy.append(", ");
        orderBy.append("ten ").append(criteria.getSapXepTen());
      }

      if (orderBy.length() > 0) {
        sql.append("ORDER BY ").append(orderBy).append(" ");
      } else if (criteria.isPaginate()) {
        sql.append("ORDER BY ma ASC ");
      }

      if (criteria.isPaginate()) {
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
      }

      try (PreparedStatement psData = conn.prepareStatement(sql.toString())) {
        if (criteria.isPaginate()) {
          psData.setInt(1, criteria.getOffset());
          psData.setInt(2, criteria.getLimit());
        }

        ResultSet rs = psData.executeQuery();
        while (rs.next()) {
          result.add(new LoaiSP(
            rs.getString("ma"),
            rs.getString("ten"),
            rs.getString("moTa")
          ));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new PaginatedResponse<>(result, criteria.getPage(), criteria.getLimit() != null ? criteria.getLimit() : result.size(), totalItems);
  }

  public Optional<LoaiSP> getByMa(String ma) {
    String sql = "SELECT * FROM LoaiSP WHERE ma = ?";
    try (
      Connection conn = ConnectDB.getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
    ) {
      ps.setString(1, ma);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        return Optional.of(new LoaiSP(rs.getString("ma"), rs.getString("ten"), rs.getString("moTa")));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  public boolean add(LoaiSP loai) {
    return false;
  }

  public boolean update(LoaiSP loai) {
    return false;
  }

  public boolean delete(String ma) {
    return false;
  }
}
