package Domain.Server;

import java.net.URI;
import java.net.URISyntaxException;

public class DatabaseConfig {
  public static void configure() {
    try {
      String databaseUrl = System.getenv("DATABASE_URL");
      if (databaseUrl != null) {
        URI dbUri = new URI(databaseUrl);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

        System.setProperty("hibernate.connection.url", dbUrl);
        System.setProperty("hibernate.connection.username", username);
        System.setProperty("hibernate.connection.password", password);
      }
    } catch (URISyntaxException e) {
      throw new RuntimeException("Error parsing DATABASE_URL", e);
    }
  }
}
