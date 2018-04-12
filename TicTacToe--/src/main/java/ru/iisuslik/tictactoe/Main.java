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

    @Override
    public void start(Stage primaryStage) {
        new Scenes(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
