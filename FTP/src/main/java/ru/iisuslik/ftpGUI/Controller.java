package ru.iisuslik.ftpGUI;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import ru.iisuslik.ftp.FTPClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static ru.iisuslik.ftp.FTPClient.FTPFile;

/**
 * Class that implements UI
 */
public class Controller {

  private FTPClient client;
  private String curPath = "./";
  private TableView<FTPFile> table;
  private Stage primaryStage;

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
    this.primaryStage = primaryStage;
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
    Button update = initializeUpdateButton();
    splitPane.getItems().addAll(update, sp);

    initializeTable();

    sp.setContent(table);

    update();
    return new Scene(splitPane, 900, 900);
  }

  private void initializeTable() {
    table = new TableView<>();
    table.setEditable(true);
    TableColumn fileNameCol = new TableColumn("File Name");
    fileNameCol.setMinWidth(500);
    table.setMinHeight(500);
    fileNameCol.setCellValueFactory(
        new PropertyValueFactory<FTPFile, String>("name"));
    TableColumn isDirectoryCol = new TableColumn("Directory");
    isDirectoryCol.setMinWidth(100);
    isDirectoryCol.setCellValueFactory(
        new PropertyValueFactory<FTPFile, String>("isDirectory"));
    table.getColumns().addAll(fileNameCol, isDirectoryCol);
    table.setOnMouseClicked(mouseEvent -> {
      FTPFile file = table.getSelectionModel().getSelectedItem();
      if (file == null || mouseEvent.getClickCount() != 2) {
        return;
      }
      if (file.getIsDirectory()) {
        if (file.getName().equals("../")) {
          goBack();
          return;
        }
        curPath += file.getName() + "/";
        update();
      } else {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        File fileToSave = fileChooser.showSaveDialog(primaryStage);
        if (fileToSave != null) {
          try {
            client.getFile(curPath + file.getName(), fileToSave.getPath());
          } catch (IOException exception) {
            showAlert("ERROR", "Can't download file");
          }
        }
      }
    });

  }


  private Button initializeUpdateButton() {
    Button update = new Button();
    update.setPrefSize(900, 50);
    update.setText("Update");
    update.setOnAction(e -> update());
    return update;
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
    List<FTPFile> files = new ArrayList<>();
    files.add(new FTPFile("../", true));
    try {
      files.addAll(client.getList(curPath));
    } catch (IOException e) {
      showAlert("ERROR", "Can't get list of file in this directory");
      return;
    }
    table.setItems(FXCollections.observableList(files));
  }

  private void downloadFile(@NotNull String path) throws IOException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Image");
    File fileToSave = fileChooser.showSaveDialog(primaryStage);
    if (fileToSave != null) {
      client.getFile(path, fileToSave.getPath());
    }
  }
}