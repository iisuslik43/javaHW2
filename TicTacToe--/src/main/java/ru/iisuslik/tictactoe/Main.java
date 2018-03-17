package ru.iisuslik.tictactoe;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static ru.iisuslik.tictactoe.GameLogic.SIZE;


public class Main extends Application {

    private Text statisticsText;
    private Bot bot;
    private Stage primaryStage;
    private Scene gameScene;
    private Scene mainMenuScene;
    private GameLogic game = new GameLogic();
    private Button[][] buttons = new Button[GameLogic.SIZE][SIZE];
    private String statistics = "";

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

    /**
     * Put current player's cell to i j cell in field.
     *
     * @param botTurn It must be true if bot doing this turn
     */
    public void pressCell(int i, int j, boolean botTurn) {
        if (game.canPlay(i, j)) {
            game.play(i, j);
            reDraw(buttons[i][j], game.getCell(i, j));
            buttons[i][j].setDisable(true);
            if (!botTurn) {
                if (bot != null)
                    bot.takeTurn(game, this);
                if (game.isThisTheEnd()) {
                    disableAll();
                    writeStatistics();
                }
            }
        }
    }

    private void writeStatistics() {
        if (game.getGameResult() == GameLogic.GameResult.DRAW) {
            if (bot != null) statistics += "Draw with " + bot.getName();
            else statistics += "Draw";
        } else if (game.getGameResult() == GameLogic.GameResult.X_WINS) {
            if (bot != null) statistics += "You win " + bot.getName();
            else statistics += "X win";
        } else {
            if (bot != null) statistics += "You lose " + bot.getName();
            else statistics += "O win";
        }
        statistics += '\n';
        statisticsText.setText(statistics);
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
        for (int i = 0; i < GameLogic.SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                final int iButton = i, jButton = j;
                buttons[i][j] = new Button();
                Button curButton = buttons[i][j];
                root.add(curButton, i, j);
                reDraw(curButton, game.getCell(i, j));
                curButton.setOnAction(e -> pressCell(iButton, jButton, false));
                customizeGameButton(curButton);
            }
        }
        Button mainMenu = new Button();
        root.add(mainMenu, SIZE + 3, 0);
        mainMenu.setPrefSize(300, 200);
        mainMenu.setText("Main Menu");
        mainMenu.setOnAction(e -> primaryStage.setScene(mainMenuScene));
        gameScene = new Scene(root, 900, 900);
    }

    private void customizeMenuButton(Button b, int pos, GridPane root) {
        b.setPrefSize(400, 200);
        root.add(b, 1, pos);
    }

    private void initializeMainMenuScene() {
        GridPane root = new GridPane();
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
        root.setPadding(new Insets(20));
        root.setHgap(25);
        root.setVgap(15);
        statisticsText = new Text();
        root.add(statisticsText, 1, 3);
        mainMenuScene = new Scene(root, 900, 900);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeGameScene();
        initializeMainMenuScene();
        primaryStage.setTitle("TicTacToe--");
        String iconPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "icon.png";
        primaryStage.getIcons().add(new Image("file:" + iconPath));
        primaryStage.setScene(mainMenuScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
