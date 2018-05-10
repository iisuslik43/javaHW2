package ru.iisuslik.ftpGui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {
  @Override
  public void start(Stage stage) {
    List<String> list = getParameters().getRaw();
    try {
      String host = list.get(0);
      int port = Integer.parseInt(list.get(1));
      new Controller(host, port, stage);
    } catch (Exception e) {
      Controller.showAlert("ERROR", "Incorrect args");
      System.exit(1);
    }
  }

  /**
   * @param args first argument should be server's host and second - server's port
   */
  public static void main(String[] args) {
    launch(args);
  }
}
