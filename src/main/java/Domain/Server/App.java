package Domain.Server;

import Domain.Server.Controlador.*;
import io.javalin.Javalin;

public class App {
  public static void main(String[] args) {
    new Server().start();

  }
  
}

