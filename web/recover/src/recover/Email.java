package recover;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

public class Email {

  private static final String emailUsername = "no-reply@parted.me";
  private static final String emailPassword = null; // redacted

  public static boolean sendRecoveryMail(String realName, String email, String urlKey) {

    String from = "HangoutsNET <no-reply@parted.me>";
    String to = realName + " <" + email + ">";

    Properties properties = System.getProperties();
    properties.setProperty("mail.smtp.host", "mail.parted.me");
    properties.setProperty("mail.smtp.port", "587");
    properties.setProperty("mail.smtp.auth", "true");
    properties.setProperty("mail.smtp.starttls.enable", "true");

    Session session = Session.getDefaultInstance(properties,
        new Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(emailUsername, emailPassword);
          }
    });

    try {
      MimeMessage message = new MimeMessage(session);

      message.setFrom(new InternetAddress(from));
      message.addRecipient(Message.RecipientType.TO,
          new InternetAddress(to));

      message.setSubject("HangoutsNET Account Recovery");

      message.setText(realName + ",\n\n"
          + "We've sent this message because a request to reset your password was submitted to us.\n\n"
          + "To change your password, please click on the following link: https://chat.parted.me/recover/" + urlKey + "/\n\n"
          + "Sincerely,\n"
          + "HangoutsNET Account Recovery System");

      System.out.println("Finished constructing message. Attempting send.");
      Transport.send(message);
      System.out.println("Message sent!");
      return true;
    } catch (MessagingException e) {
      e.printStackTrace();
    }
    System.out.println("Message not sent!");
    return false;
  }

}
