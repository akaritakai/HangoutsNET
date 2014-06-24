package recover;

import db.Database;
import db.SQLObject;
import db.User;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;

public class RecoverKeyDb extends Thread {

  private Database db;

  public RecoverKeyDb(Database db) {
    this.db = db;
    this.start();
  }

  public void run() {
    final long ONE_MINUTE = 60000;
    while(true) {
      /* remove entries older than one day */
      db.executeSql("DELETE FROM recover_keys WHERE creation_date < UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 DAY))");
      try {
        Thread.sleep(ONE_MINUTE);
      } catch (InterruptedException e) { } // Ignore
    }
  }

  public String getKey(User user) {
    String key = null;
    do {
      String key = RandomStringUtils.random(24,
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        + "abcdefghijklmnopqrstuvwxyz"
        + "0123456789");
    } while (getUser(key) != null); /* suggested by https://github.com/TylorF
                                     * but honestly if it collides, go buy a lotto ticket */

    /* associate key with user */
    db.executeSql(
      "INSERT INTO `recover_keys` (`username`, `token`, `creation_date`)"
        + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE"
        + " `token`=?",
      new SQLObject(user.getUsername()),
      new SQLObject(key),
      new SQLObject(new DateTime()),
      new SQLObject(key));

    return key;
  }

  public User getUser(String key) {
    return User.getUserFromKey(db, key);
  }

  public void removeKey(String key) {
    db.executeSql("DELETE FROM recover_keys WHERE token = ?", new SQLObject(key));
  }

}
