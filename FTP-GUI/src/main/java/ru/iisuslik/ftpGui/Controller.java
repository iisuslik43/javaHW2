package ru.iisuslik.ftpGui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ru.iisuslik.ftp.FTPClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.iisuslik.ftp.FTPClient.FTPFile;

/**
 * Class that implements UI
 */
public class Controller {

  private FTPClient client;
  private GridPane root;
  private ArrayList<Button> buttons = new ArrayList<>();
  private String curPath = "./";

  /**
   * Shows alert that waits that user will press OK
   *
   * @param title   alert title
   * @param content alert content
   */
  public static void showAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setContentText(content);
    alert.setHeaderText(null);
    alert.showAndWait();
  }

  /**
   * Creates controller and starts UI for FTP
   *
   * @param host         server's host
   * @param port         server's port
   * @param primaryStage stage where all panes should be put on
   */
  public Controller(String host, int port, Stage primaryStage) {
    try {
      client = new FTPClient(host, port);
    } catch (IOException e) {
      showAlert("ERROR", "Can't connect to server");
      System.exit(1);
    }
    primaryStage.setTitle("FTP");
    primaryStage.setScene(initializeScene());
    primaryStage.show();
  }

  private Scene initializeScene() {
    SplitPane splitPane = new SplitPane();
    splitPane.setOrientation(Orientation.VERTICAL);
    ScrollPane sp = new ScrollPane();
    sp.setFitToWidth(true);
    root = new GridPane();
    sp.setContent(root);
    Button update = initializeUpdateButton();
    splitPane.getItems().addAll(update, sp);
    initializeBackButton();
    update();
    root.setPadding(new Insets(20));
    root.setHgap(300);
    root.setVgap(10);
    return new Scene(splitPane, 900, 900);
  }

  private void customizeButton(Button button) {
    button.setPrefSize(400, 100);
    button.setStyle("-fx-font: 30 arial; -fx-base: #b6e7c9;");
  }

  private Button initializeUpdateButton() {
    Button update = new Button();
    update.setPrefSize(900, 100);
    update.setStyle("-fx-font: 15 arial; -fx-base: #b6e7c9;");
    update.setText("Update");
    update.setOnAction(e -> update());
    root.add(update, 1, 0);
    return update;
  }

  private void initializeBackButton() {
    Button back = new Button();
    customizeButton(back);
    back.setText("/..");
    back.setOnAction(e -> goBack());
    root.add(back, 0, 0);
  }

  private void goBack() {
    if (!curPath.equals("./")) {
      String[] path = curPath.split("/");
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < path.length - 1; i++) {
        sb.append(path[i]).append("/");
      }
      curPath = sb.toString();
      update();
    }
  }

  private void update() {
    ArrayList<Button> newButtons = new ArrayList<>();
    List<FTPFile> files;
    try {
      files = client.getList(curPath);
    } catch (IOException e) {
      showAlert("ERROR", "Can't get list of file in this directory");
      return;
    }
    for (FTPFile file : files) {
      Button b = new Button();
      customizeButton(b);
      if (file.isDirectory) {
        b.setText("/" + file.name);
        b.setOnAction(e -> {
          curPath += file.name + "/";
          update();
        });
      } else {
        b.setText(file.name);
        b.setOnAction(e -> {
          try {
            client.getFile(curPath + file.name);
          } catch (IOException exception) {
            showAlert("ERROR", "Can't download file");
          }
        });
      }
      newButtons.add(b);
    }

    for (Button button : buttons) {
      root.getChildren().remove(button);
    }
    for (int i = 0; i < newButtons.size(); i++) {
      root.add(newButtons.get(i), 0, i + 1);
    }
    buttons = newButtons;
  }
}
