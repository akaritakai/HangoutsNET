package server;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import db.Database;
import recover.RecoverServlet;
import webirc.SaslServlet;
import znc.ZNCAdmin;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HttpServer {

  public static void main(String[] args) {
      try {
        final InetAddress localhost = InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
        final int port = 8080;

        final Database db = new Database();
        final ZNCAdmin zncAdmin = new ZNCAdmin();
        final Server server = new Server(new InetSocketAddress(localhost, port)); 

        final ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new SaslServlet(db)), "/sasl/*");
        context.addServlet(new ServletHolder(new RecoverServlet(db, zncAdmin)), "/recover/*");
        server.setHandler(context);

        server.start();
        server.join();

      } catch (Exception e) {
        e.printStackTrace();
      }
  }

}
