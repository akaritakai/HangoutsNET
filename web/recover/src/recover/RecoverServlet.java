package recover;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.Database;
import db.User;
import znc.ZNCAdmin;

import org.apache.commons.io.FileUtils;

@SuppressWarnings("serial")
public class RecoverServlet extends HttpServlet {

  private Database db;
  private ZNCAdmin admin;
  private RecoverKeyDb keyDb;

  public RecoverServlet(Database db, ZNCAdmin admin) {
    super();
    this.db = db;
    this.admin = admin;
    this.keyDb = new RecoverKeyDb(db);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) {  
    String key = request.getPathInfo();
    if(key == null || (key = key.replace("/", "")).isEmpty()){
      /* Path does not contain key */
      try {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");
        response.getWriter().write(
          FileUtils.readFileToString(
            new File("/opt/web/recover/emailrequest.html"),
            Charset.forName("UTF-8")));  
        response.flushBuffer();
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    else {
      /* Path contains key */
      User user = keyDb.getUser(key);
      if (user == null) {
        /* Key did not match a user */
        try {
          response.setStatus(HttpServletResponse.SC_OK);
          response.setContentType("text/html");
          response.getWriter().write(
            FileUtils.readFileToString(
              new File("/opt/web/recover/badkey.html"),
              Charset.forName("UTF-8")));
          response.flushBuffer();
          return;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      else {
        /* Key matched a user */
        try {
          response.setStatus(HttpServletResponse.SC_OK);
          response.setContentType("text/html");
          response.getWriter().write(
            FileUtils.readFileToString(
              new File("/opt/web/recover/passwordchange.html"),
              Charset.forName("UTF-8")));
          response.flushBuffer();
          return;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    String key = request.getPathInfo();
    if (key == null || (key = key.replace("/", "")).isEmpty()){
      /* Path should not contain a key */
      String info = request.getParameter("info");
      if (info == null || info.isEmpty()) {
        /* No info provided for generating key */
        try {
          response.setStatus(HttpServletResponse.SC_OK);
          response.setContentType("text/plain");
          response.getWriter().write("bad_info");
          response.flushBuffer();
          return;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      else {
        /* Info provided for generating key */
        User user = User.getUserFromInfo(db, info);
        if (user == null) {
          /* User didn't exist */
          try {
            System.out.println("User provided information for a user that didn't exist");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            response.getWriter().write("bad_info");
            response.flushBuffer();
            return;
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        else {
          /* User did exist */
          key = keyDb.getKey(user);
          if(Email.sendRecoveryMail(user.getRealName(), user.getEmail(), key)) {
            /* Mail sent */
            try {
              System.out.println("Mail sent OK!");
              response.setStatus(HttpServletResponse.SC_OK);
              response.setContentType("text/plain");
              response.getWriter().write("email_sent");
              response.flushBuffer();
              return;
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          else {
            /* Mail failure */
            try {
              response.setStatus(HttpServletResponse.SC_OK);
              response.setContentType("text/plain");
              response.getWriter().write("email_fail");
              response.flushBuffer();
              return;
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
    else {
      /* Path should contain a key */
      User user = keyDb.getUser(key);
      if (user == null) {
        /* Key not in database */
        try {
          response.setStatus(HttpServletResponse.SC_OK);
          response.setContentType("text/plain");
          response.getWriter().write("bad_key");
          response.flushBuffer();
          return;
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        /* Key in database */
        String newPass = request.getParameter("newpass");
        if (newPass == null || newPass.isEmpty()) {
          /* Password not specified */
          try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            response.getWriter().write("no_pass");
            response.flushBuffer();
            return;
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        else {
          /* Password specified */
          user.updatePassword(db, admin, newPass);
          keyDb.removeKey(key); // Don't allow the key to be used again
          try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            response.getWriter().write("pw_changed");
            response.flushBuffer();
            return;
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

}
