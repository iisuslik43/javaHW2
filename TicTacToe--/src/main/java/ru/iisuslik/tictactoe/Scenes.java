package ru.iisuslik.tictactoe;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static ru.iisuslik.tictactoe.GameLogic.SIZE;

/**
 * Class that works with UI
 */
public class Scenes {

    private Bot bot;
    private Stage primaryStage;
    private Scene gameScene;
    private Scene mainMenuScene;
    private GameLogic game = new GameLogic();
    private Button[][] buttons = new Button[GameLogic.SIZE][SIZE];
    private ArrayList<String> hotSeatStatistics = new ArrayList<>();
    private ArrayList<String> simpleBotStatistics = new ArrayList<>();
    private ArrayList<String> hardBotStatistics = new ArrayList<>();


    /**
     * Make new Scenes
     *
     * @param primaryStage Main stage
     */
    public Scenes(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeMainMenuScene();
        initializeGameScene();
        primaryStage.setTitle("TicTacToe--");
        String iconPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "icon.png";
        primaryStage.getIcons().add(new Image("file:" + iconPath));
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    private void reDraw(Button button, GameLogic.Cell cell) {
        switch (cell) {
            case EMPTY:
                button.setText("");
                break;
            case O:
                button.setText("O");
                break;
            case X:
                button.setText("X");
                break;
        }
    }

    private void reDrawAll() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                reDraw(buttons[i][j], game.getCell(i, j));
                if (game.getCell(i, j) != GameLogic.Cell.EMPTY) {
                    buttons[i][j].setDisable(true);
                }
            }
        }
    }

    /**
     * Put current player's cell to i j cell in field.
     */
    private void pressCell(int i, int j) {
        if (game.canPlay(i, j)) {
            game.play(i, j);
            reDrawAll();
            if (bot != null) {
                bot.takeTurn(game);
                reDrawAll();
            }
            if (game.isThisTheEnd()) {
                disableAll();
                writeStatistics();
                showAlert("Game Over", game.getGameResult().toString());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void writeStatistics() {
        if (bot != null) {
            if(bot instanceof HardBot) {
                hardBotStatistics.add(game.getGameResult().toString());
            } else {
                simpleBotStatistics.add(game.getGameResult().toString());
            }
        } else {
            hotSeatStatistics.add(game.getGameResult().toString());
        }
    }

    private void disableAll() {
        for (int i = 0; i < GameLogic.SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                buttons[i][j].setDisable(true);
            }
        }
    }

    private void startNewGame(@Nullable Bot bot) {
        this.bot = bot;
        initializeGameScene();
        primaryStage.setScene(gameScene);
        game.startNewGame();
        for (int i = 0; i < GameLogic.SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                reDraw(buttons[i][j], GameLogic.Cell.EMPTY);
                buttons[i][j].setDisable(false);
            }
        }
    }

    private void customizeGameButton(Button button) {
        button.setPrefSize(200, 200);
        button.setStyle("-fx-font: 60 arial; -fx-base: #b6e7c9;");
    }

    private void initializeGameScene() {
        GridPane root = new GridPane();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                final int iButton = i, jButton = j;
                buttons[i][j] = new Button();
                Button curButton = buttons[i][j];
                root.add(curButton, i, j);
                reDraw(curButton, game.getCell(i, j));
                curButton.setOnAction(e -> pressCell(iButton, jButton));
                customizeGameButton(curButton);
            }
        }
        Button mainMenu = new Button();
        root.add(mainMenu, SIZE + 3, 0);
        mainMenu.setPrefSize(300, 200);
        mainMenu.setText("Main Menu");
        mainMenu.setOnAction(e -> {
            initializeMainMenuScene();
            primaryStage.setScene(mainMenuScene);
        });
        gameScene = new Scene(root, mainMenuScene.getWidth(), mainMenuScene.getHeight());
    }

    private void customizeMenuButton(Button b, int pos, GridPane root) {
        b.setPrefSize(400, 200);
        root.add(b, 1, pos);
    }

    private void customizeStatButton(Button b, int pos, GridPane root) {
        b.setPrefSize(200, 200);
        root.add(b, 2, pos);
    }

    private void initializeGameButtons(GridPane root) {
        Button hotSeat = new Button();
        Button simpleBot = new Button();
        Button hardBot = new Button();
        customizeMenuButton(hotSeat, 0, root);
        customizeMenuButton(simpleBot, 1, root);
        customizeMenuButton(hardBot, 2, root);
        hotSeat.setOnAction(e -> startNewGame(null));
        hotSeat.setText("Hot Seat");
        simpleBot.setOnAction(e -> startNewGame(new SimpleBot()));
        simpleBot.setText("Game with easy bot");
        hardBot.setOnAction(e -> startNewGame(new HardBot()));
        hardBot.setText("Game with hard bot");
    }

    private void initializeStatisticsButtons(GridPane root) {
        Button hotSeatStats = new Button();
        Button simpleBotStats = new Button();
        Button hardBotStats = new Button();
        customizeStatButton(hotSeatStats, 0, root);
        customizeStatButton(simpleBotStats, 1, root);
        customizeStatButton(hardBotStats, 2, root);
        hotSeatStats.setOnAction(e -> showAlert("Hot Seat Results",
            hotSeatStatistics.stream().collect(Collectors.joining("\n"))));
        hotSeatStats.setText("Stats");

        simpleBotStats.setOnAction(e -> showAlert("Simple Bot Results",
            simpleBotStatistics.stream().collect(Collectors.joining("\n"))));
        simpleBotStats.setText("Stats");

        hardBotStats.setOnAction(e -> showAlert("Hard Bot Results",
            hardBotStatistics.stream().collect(Collectors.joining("\n"))));
        hardBotStats.setText("Stats");
    }

    private void initializeMainMenuScene() {
        GridPane root = new GridPane();
        initializeGameButtons(root);
        initializeStatisticsButtons(root);
        root.setPadding(new Insets(20));
        root.setHgap(25);
        root.setVgap(15);
        if (gameScene == null) {
            mainMenuScene = new Scene(root, 900, 900);
        } else {
            mainMenuScene = new Scene(root, gameScene.getWidth(), gameScene.getHeight());
        }

    }
}
