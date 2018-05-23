package ru.iisuslik.pairs;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * Main class of application
 */
public class Main extends Application {
  @Override
  public void start(Stage primaryStage) {
    List<String> list = getParameters().getRaw();
    System.out.println(list);
    int N;
    try {
      N = Integer.parseInt(list.get(0));
      if (N % 2 != 0) {
        Controller.showAlert("Error", "Odd N");
        return;
      }
      if (N < 2) {
        Controller.showAlert("Error", "Too small N");
        return;
      }
      if (N > 16) {
        Controller.showAlert("Error", "Too big N");
        return;
      }
    } catch (Exception e) {
      Controller.showAlert("Error", "Unknown args");
      return;
    }
    new Controller(N, primaryStage);
  }

  /**
   * Starts Pairs game
   *
   * @param args first argument should be N - number of raws and cols
   */
  public static void main(String[] args) {
    launch(args);
  }


}
