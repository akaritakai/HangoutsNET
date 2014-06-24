package znc;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

public class ZNCAdmin extends Thread {

  private PircBotX bot;

  public ZNCAdmin() {
    Configuration<PircBotX> config = new Configuration.Builder<>()
        .setName("ZNCAdmin")
        .setLogin("zncadmin")
        .setServerHostname("127.0.0.1")
        .setServerPort(1340)
        .setServerPassword(null) // redacted
        .setAutoReconnect(true)
        .buildConfiguration();
    bot = new PircBotX(config);
    this.start();
  }

  public void run() {
    try {
      bot.startBot();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void changePassword(final String username, final String newPassword) {
    controlPanel("Set Password", username, newPassword);
  }

  private void controlPanel(final String command, final String... args) {
    StringBuilder sb = new StringBuilder();
    sb.append(command);
    for(String arg : args) {
      sb.append(" " + arg);
    }
    bot.sendIRC().message("CHAT-controlpanel", sb.toString());
  }

}
