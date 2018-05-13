package ru.iisuslik.tictactoe;

import javafx.application.Application;
import javafx.stage.Stage;


/**
 * Special class that will start at the beginning
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        new Scenes(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
