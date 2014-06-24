package webirc;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.Database;
import db.User;

@SuppressWarnings("serial")
public class SaslServlet extends HttpServlet {

  private final Database db;

  public SaslServlet(final Database db) {
    super();
    this.db = db;
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    doPost(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) { 
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    if (username == null || username.isEmpty()) {
      try {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("text/plain");
        response.getWriter().write("");
        response.flushBuffer();
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (password == null || password.isEmpty()) {
      try {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("text/plain");
        response.getWriter().write("");
        response.flushBuffer();
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    User user = User.getUserFromInfo(db, username);
    if (user == null) {
      try {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/plain");
        response.getWriter().write("");
        response.flushBuffer();
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (!user.validatePassword(password)) {
      try {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/plain");
        response.getWriter().write("");
        response.flushBuffer();
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("text/plain");
      response.getWriter().write(user.getSaslPassword());
      response.flushBuffer();
      return;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
