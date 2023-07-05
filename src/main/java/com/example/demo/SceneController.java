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
import javafx.stage.Stage;

import java.io.IOException;
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
            System.out.println(e);
        }
    }

    @FXML
    public void initialize() throws IOException {
        // dirty fix
        logoView = (ImageView) this.lookup("#logoView");
        newProjectButton = (Button) this.lookup("#newProjectButton");
        openProjectButton = (Button) this.lookup("#newProjectButton");
        openFileButton = (Button) this.lookup("#newProjectButton");

        logoView.setImage(new Image(getClass()
                .getResource("img/panda-logo.png")
                .openStream()));

        System.out.println("init");

        newProjectButton.setOnAction(e -> {
            ArrayList<String> poop = new ArrayList<>();
            poop.add("Hello from main scene");
            MainWindowController mainWindowController = new MainWindowController();

            Scene mainWindowScene = new Scene(mainWindowController);

            this.getScene().setUserData(poop);
            this.setSecondScene(mainWindowScene);
            openSecondScene(e);
            mainWindowController.setFirstScene(this.getScene());
        });
    }
}
