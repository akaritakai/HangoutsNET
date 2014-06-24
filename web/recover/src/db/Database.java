package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Database {

  static {
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private HikariDataSource dataSource = null;

  public Database() {
    HikariConfig config = new HikariConfig();
    config.setMaximumPoolSize(100);
    config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
    config.addDataSourceProperty("serverName", "127.0.0.1");
    config.addDataSourceProperty("port", "3306");
    config.addDataSourceProperty("databaseName", "hangouts");
    config.addDataSourceProperty("user", "hangouts");
    config.addDataSourceProperty("password", null); // redacted
    this.dataSource = new HikariDataSource(config);
  }

  private Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  public void executeSql(String sql, SQLObject... args) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
      conn = getConnection();
      stmt = conn.prepareStatement(sql);
      for (int i = 0; i < args.length; i++) {
        args[i].prepareInto(stmt, i + 1);
      }
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) { } // ignore
        stmt = null;
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) { } // ignore
        conn = null;
      }
    }
  }

  public int numRowsFromQuery(String sql, SQLObject... args) {
    int numRows = 0;
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      stmt = conn.prepareStatement(sql);
      for (int i = 0; i < args.length; i++) {
        args[i].prepareInto(stmt, i + 1);
      }
      rs = stmt.executeQuery();
      while (rs.next()) {
        numRows++;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) { } // ignore
        rs = null;
      }
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) { } // ignore
        stmt = null;
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) { } // ignore
        conn = null;
      }
    }
    return numRows;
  }

  public List<User> getUserQuery(String sql, SQLObject... args) {
    List<User> list = new LinkedList<>();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      stmt = conn.prepareStatement(sql);
      for (int i = 0; i < args.length; i++) {
        args[i].prepareInto(stmt, i + 1);
      }
      rs = stmt.executeQuery();
      while (rs.next()) {
        list.add(new User(
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("sasl_password"),
            rs.getString("email"),
            rs.getString("real_name")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) { } // ignore
        rs = null;
      }
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) { } // ignore
        stmt = null;
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) { } // ignore
        conn = null;
      }
    }
    return list;
  }

  public List<List<String>> getStringRows(String sql, String[] columnNames, SQLObject... sqlObject) {
    List<List<String>> list = new LinkedList<>();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      stmt = conn.prepareStatement(sql);
      for (int i = 0; i < sqlObject.length; i++) {
        sqlObject[i].prepareInto(stmt, i + 1);
      }
      rs = stmt.executeQuery();
      while (rs.next()) {
        List<String> row = new LinkedList<>();
        for(String column : columnNames) {
          row.add(rs.getString(column));
        }
        list.add(row);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) { } // ignore
        rs = null;
      }
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) { } // ignore
        stmt = null;
      }
      if (conn != null) {
        try {
          conn.close();
        } catch (SQLException e) { } // ignore
        conn = null;
      }
    }
    return list;
  }

}
