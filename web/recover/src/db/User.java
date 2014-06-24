package db;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import znc.ZNCAdmin;

public class User {

  private String username;
  private String password;
  private String saslPassword;
  private String email;
  private String realName;

  public User(String username, String password, String saslPassword, String email, String realName) {
    this.username = username;
    this.password = password;
    this.saslPassword = saslPassword;
    this.email = email;
    this.realName = realName;
  }

  public String getUsername() {
    return username;
  }
  
  public String getSaslPassword() {
    return saslPassword;
  }
  
  public String getEmail() {
    return email;
  }

  public String getRealName() {
    return realName;
  }

  public boolean validatePassword(String candidate) {
    return BCrypt.checkpw(candidate, password);
  }

  public void updatePassword(Database db, ZNCAdmin admin, String password) {
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    this.password = hashedPassword;
    db.executeSql("UPDATE users SET password = ? WHERE username = ?",
			new SQLObject(hashedPassword),
			new SQLObject(username));
    admin.changePassword(getUsername(), password);
  }

  public static User getUserFromInfo(Database db, String info) {
    List<User> list = db.getUserQuery(
        "SELECT * FROM users WHERE username = ?",
        new SQLObject(info));
    return (list.isEmpty()) ? null : list.get(0);
  }

  public static User getUserFromKey(Database db, String key) {
    String[] columnNames = { "username" };
    List<List<String>> stringList = db.getStringRows(
        "SELECT username FROM recover_keys WHERE token = ?",
        columnNames,
        new SQLObject(key));
    if(stringList.isEmpty()) {
      return null;
    }
    List<String> row = stringList.get(0);
    if(row.isEmpty()) {
      return null;
    }
    List<User> userList = db.getUserQuery(
        "SELECT * FROM users WHERE username = ?",
        new SQLObject(row.get(0)));
    return (userList.isEmpty()) ? null : userList.get(0);
  }

}
