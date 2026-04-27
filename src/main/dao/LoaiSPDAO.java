package main.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import main.connectDB.ConnectDB;
import main.dto.LoaiSPGetListCriteria;
import main.dto.PaginatedResponse;
import main.entity.LoaiSP;
import main.enumeration.SortDirection;

public class LoaiSPDAO {
  private static LoaiSPDAO instance = new LoaiSPDAO();

  private LoaiSPDAO() {}

  public static LoaiSPDAO getInstance() {
    return instance;
  }

  public PaginatedResponse<LoaiSP> getList(LoaiSPGetListCriteria criteria) {
    List<LoaiSP> result = new ArrayList<>();
    
    Integer limit = criteria.limit();
    Integer page = criteria.page();
    int offset = (limit != null && limit > 0 && page != null && page > 0) ? (page - 1) * limit : 0;
    boolean isPaginate = (limit != null && limit > 0);

    long totalItems = 0;
    try (Connection conn = ConnectDB.getConnection()) {
      String countSql = "SELECT COUNT(*) FROM LoaiSP";
      try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
        ResultSet rsCount = psCount.executeQuery();
        if (rsCount.next()) totalItems = rsCount.getLong(1);
      }

      StringBuilder sql = new StringBuilder("SELECT * FROM LoaiSP ");

      StringBuilder orderBy = new StringBuilder();
      if (criteria.sapXepMa() != SortDirection.NONE) {
        orderBy.append("ma ").append(criteria.sapXepMa());
      }
      if (criteria.sapXepTen() != SortDirection.NONE) {
        if (orderBy.length() > 0) orderBy.append(", ");
        orderBy.append("ten ").append(criteria.sapXepTen());
      }

      if (orderBy.length() > 0) {
        sql.append("ORDER BY ").append(orderBy).append(" ");
      } else if (isPaginate) {
        sql.append("ORDER BY ma ASC ");
      }

      if (isPaginate) {
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
      }

      try (PreparedStatement psData = conn.prepareStatement(sql.toString())) {
        if (isPaginate) {
          psData.setInt(1, offset);
          psData.setInt(2, limit);
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

    return new PaginatedResponse<>(result, page != null ? page : 1, limit != null ? limit : result.size(), totalItems);
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
