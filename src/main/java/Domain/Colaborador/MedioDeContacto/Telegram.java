package Domain.Colaborador.MedioDeContacto;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class Telegram{

  public Telegram() {

  }
/*
  private static final String BOT_TOKEN = "7523722721:AAG3Mzr2XnBhvbAu2YgUg2o7Oit1RHX1y24";
  private static final String CHAT_ID = "5475107169";


  public void enviarMensaje(String contacto,String cuerpo) {
    try {
      String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "application/json");

      // Constructing the JSON input string
      String jsonInputString = "{\"chat_id\":\"" + CHAT_ID + "\", \"text\":\"" + cuerpo + "\"}";

      // Sending the request
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }

      int responseCode = connection.getResponseCode();
      System.out.println("Response Code: " + responseCode);

      connection.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
*/
}