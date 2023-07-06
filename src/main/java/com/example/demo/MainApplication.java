package com.example.demo;

import com.example.demo.guiutils.FileUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneController sceneController = new SceneController();

        Scene scene = new Scene(sceneController);
        scene.getStylesheets().add(getClass().getResource("styles/main.css").toExternalForm());
        stage.setTitle("RedPanda");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
