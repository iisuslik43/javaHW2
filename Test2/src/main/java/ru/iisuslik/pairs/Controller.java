package ru.iisuslik.pairs;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


/**
 * Class that works UI and connects it with game logic
 */
public class Controller {

  private Scene scene;
  private GameLogic game;
  private Button[][] buttons;

  /**
   * Creates new controller
   *
   * @param N field size
   * @param primaryStage primary stage
   */
  public Controller(int N, Stage primaryStage) {
    game = new GameLogic(N);
    initializeGame();
    primaryStage.setTitle("Pairs");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void initializeGame() {
    GridPane root = new GridPane();
    initializeButtons(root);
    root.setPadding(new Insets(20));
    root.setHgap(25);
    root.setVgap(15);
    scene = new Scene(root, 900, 900);
  }

  private void customizeButton(Button button) {
    button.setPrefSize(200, 200);
    button.setStyle("-fx-font: 60 arial; -fx-base: #b6e7c9;");
  }

  private void press(int i, int j) {
    reDraw();
    buttons[i][j].setText(String.valueOf(game.getCell(i, j)));
    buttons[i][j].setDisable(true);
    game.open(i, j);
    if(game.gameOver()) {
      showAlert("Game Over", "Bye");
      System.exit(0);
    }
  }

  /**
   * Shows alert that waits that user will press OK
   *
   * @param title alert title
   * @param content alert content
   */
  public static void showAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setContentText(content);
    alert.setHeaderText(null);
    alert.showAndWait();
  }

  private void reDraw() {
    int N = game.getN();
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        Button b = buttons[i][j];
        if(game.isPressed(i, j)) {
          b.setText(String.valueOf(game.getCell(i, j)));
          b.setDisable(true);
        } else {
          b.setText("");
          b.setDisable(false);
        }
      }
    }
  }

  private void initializeButtons(GridPane root) {
    int N = game.getN();
    buttons = new Button[N][N];
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        int i1 = i;
        int j1 = j;
        buttons[i][j] = new Button();
        Button b = buttons[i][j];
        customizeButton(b);
        b.setOnAction(e -> press(i1, j1));
        root.add(b, i, j);
      }
    }
  }
}
