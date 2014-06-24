package db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.joda.time.DateTime;

public class SQLObject {

  private Object object;

  public SQLObject(Boolean bool) {
    this.object = bool;
  }

  public SQLObject(DateTime dateTime) {
    this.object = dateTime;
  }

  public SQLObject(String string) {
    this.object = string;
  }

  public void prepareInto(PreparedStatement stmt, int n) throws SQLException {
    if (object instanceof Boolean) {
      stmt.setBoolean(n, (Boolean) object);
    }
    else if (object instanceof DateTime) {
      stmt.setDate(n, new Date(((DateTime) object).toDate().getTime()));
    }
    else if (object instanceof String) {
      stmt.setString(n, (String) object);
    }
    else throw new SQLException("Data type could not be prepared into statement.");
  }

}
