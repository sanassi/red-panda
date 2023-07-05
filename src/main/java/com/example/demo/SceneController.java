package com.example.demo;

import com.almasb.fxgl.core.collection.Array;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;


public class SceneController extends AnchorPane {
    @FXML public ImageView logoView;
    @FXML public Button newProjectButton;
    @FXML public Button openProjectButton;
    @FXML public Button openFileButton;

    @FXML private Scene secondScene;

    public void setSecondScene(Scene scene) {
        secondScene = scene;
    }

    public void openSecondScene(ActionEvent actionEvent) {
        try {
            Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            primaryStage.setScene(secondScene);
        } catch (Exception e) {
            System.out.println("fail");
        }
    }

    public SceneController() throws IOException {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("open-project-window.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            throw e;
        }
    }

    @FXML
    public void initialize() throws IOException {
        // dirty fix
        logoView = (ImageView) this.lookup("#logoView");
        newProjectButton = (Button) this.lookup("#newProjectButton");
        openProjectButton = (Button) this.lookup("#openProjectButton");
        openFileButton = (Button) this.lookup("#openFileButton");

        logoView.setImage(new Image(getClass()
                .getResource("img/panda-logo.png")
                .openStream()));

        System.out.println("init");

        openProjectButton.setOnAction(e -> {
            MainWindowController mainWindowController = new MainWindowController();
            Scene mainWindowScene = new Scene(mainWindowController);

            this.getScene().setUserData(new Pair<>(Action.NEW_PROJECT, openFolder()));
            this.setSecondScene(mainWindowScene);

            openSecondScene(e);
            try {
                mainWindowController.setFirstScene(this.getScene());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        openFileButton.setOnAction(e -> {
            MainWindowController mainWindowController = new MainWindowController();
            Scene mainWindowScene = new Scene(mainWindowController);

            this.getScene().setUserData(new Pair<>(Action.OPEN_FILE, openFile()));
            this.setSecondScene(mainWindowScene);

            openSecondScene(e);
            try {
                mainWindowController.setFirstScene(this.getScene());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        newProjectButton.setOnAction(e -> {
            MainWindowController mainWindowController = new MainWindowController();
            Scene mainWindowScene = new Scene(mainWindowController);

            this.getScene().setUserData(new Pair<>(Action.NEW_PROJECT, openFolder()));
            this.setSecondScene(mainWindowScene);

            openSecondScene(e);
            try {
                mainWindowController.setFirstScene(this.getScene());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @FXML
    public Path openFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        var chosen = directoryChooser.showDialog((Stage) this.getScene().getWindow());
        return chosen == null ? null : chosen.toPath();
    }

    @FXML Path openFile() {
        FileChooser fileChooser = new FileChooser();
        var chosen = fileChooser.showOpenDialog((Stage) this.getScene().getWindow());
        return chosen == null ? null : chosen.toPath();
    }
}
