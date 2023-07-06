package com.example.demo;

import com.almasb.fxgl.core.collection.Array;
import com.example.demo.guiutils.FileUtils;
import com.fasterxml.jackson.databind.util.JSONPObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.extern.jackson.Jacksonized;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Long.MAX_VALUE;


public class SceneController extends AnchorPane {
    @FXML public ImageView logoView;
    @FXML public Button newProjectButton;
    @FXML public Button openProjectButton;
    @FXML public Button openFileButton;
    @FXML public VBox projectDisplay;
    @FXML private Scene secondScene;
    @FXML public Path projectConfigPath;
    @FXML public Map<String, String> projects;
    @FXML public Label projectSearchLabel;

    @FXML public CustomTextField projectFieldSearch;

    @FXML
    Image searchIcon = new Image(Objects.requireNonNull(getClass()
                    .getResource("img/search.png"))
            .openStream());

    public void setSecondScene(Scene scene) {
        secondScene = scene;
    }

    public void openSecondScene(ActionEvent actionEvent) {
        try {
            Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            secondScene.getStylesheets().add(getClass().getResource("styles/main.css").toExternalForm());
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

        this.getStylesheets().add(getClass().getResource("styles/open-project-window.css").toExternalForm());

        projectConfigPath = Path.of(".panda");
        projects = new HashMap<>();

        try {
            FileUtils.CreateDirectory(projectConfigPath);

        } catch (Exception e) {
            System.err.println("[INFO] create project configuration folder failed.");
        }

        File config = new File(projectConfigPath.toAbsolutePath().resolve("config").toString());
        System.out.println(config.createNewFile());

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
        logoView.setImage(new Image(getClass()
                .getResource("img/panda-logo.png")
                .openStream()));

        projectSearchLabel.setGraphic(new ImageView(searchIcon));

        readConfig();
        populateDisplay();
        setFindProjectInRecent();
        setButtons();
    }

    // tried to refacto the following functions ..
    public void setScenes(Action action, ActionEvent e) throws IOException {
        MainWindowController mainWindowController = new MainWindowController();
        Scene mainWindowScene = new Scene(mainWindowController);

        Pair<Action, Path> data;
        switch (action) {
            case OPEN_PROJECT -> {
                data = new Pair<>(Action.OPEN_PROJECT, openFolder());
                FileUtils.writeToFile(projectConfigPath.resolve("config"),
                        FileUtils.readFile(projectConfigPath.resolve("config")) + "\n" +
                                data.getValue().getFileName().toString() + " " + data.getValue().toString());
            }
            case NEW_PROJECT -> {
                data = new Pair<>(Action.NEW_PROJECT, openFolder());
                FileUtils.writeToFile(projectConfigPath.resolve("config"),
                        FileUtils.readFile(projectConfigPath.resolve("config")) + "\n" +
                                data.getValue().getFileName().toString() + " " + data.getValue().toString());
            }
            case OPEN_FILE -> data = new Pair<>(Action.OPEN_FILE, openFile());
            default -> throw new IllegalStateException("Unexpected value: " + action);
        }

        this.getScene().setUserData(data);
        this.setSecondScene(mainWindowScene);

        openSecondScene(e);

        try {
            mainWindowController.setFirstScene(this.getScene());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    public void setButtons() {
        newProjectButton = (Button) this.lookup("#newProjectButton");
        openProjectButton = (Button) this.lookup("#openProjectButton");
        openFileButton = (Button) this.lookup("#openFileButton");

        System.out.println("init");

        openProjectButton.setOnAction(e -> {
            try {
                setScenes(Action.OPEN_PROJECT, e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            /*
            MainWindowController mainWindowController = new MainWindowController();
            Scene mainWindowScene = new Scene(mainWindowController);

            var data = new Pair<>(Action.OPEN_PROJECT, openFolder());

            this.getScene().setUserData(data);
            this.setSecondScene(mainWindowScene);

            openSecondScene(e);
            try {
                FileUtils.writeToFile(projectConfigPath.resolve("config"),
                        FileUtils.readFile(projectConfigPath.resolve("config")) + "\n" +
                                data.getValue().getFileName().toString() + " " + data.getValue().toString());
                mainWindowController.setFirstScene(this.getScene());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
             */
        });

        openFileButton.setOnAction(e -> {
            /*
            MainWindowController mainWindowController = new MainWindowController();
            Scene mainWindowScene = new Scene(mainWindowController);

            var data = new Pair<>(Action.OPEN_FILE, openFile());

            this.getScene().setUserData(data);
            this.setSecondScene(mainWindowScene);

            openSecondScene(e);
            try {
                mainWindowController.setFirstScene(this.getScene());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
             */
            try {
                setScenes(Action.OPEN_FILE, e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        newProjectButton.setOnAction(e -> {
            /*
            MainWindowController mainWindowController = new MainWindowController();
            Scene mainWindowScene = new Scene(mainWindowController);

            var data = new Pair<>(Action.NEW_PROJECT, openFolder());

            this.getScene().setUserData(data);
            this.setSecondScene(mainWindowScene);

            openSecondScene(e);
            try {
                FileUtils.writeToFile(projectConfigPath.resolve("config"),
                        FileUtils.readFile(projectConfigPath.resolve("config")) + "\n" +
                                data.getValue().getFileName().toString() + " " + data.getValue().toString());
                mainWindowController.setFirstScene(this.getScene());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
             */
            try {
                setScenes(Action.NEW_PROJECT, e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @FXML
    public void setFindProjectInRecent() {
        projectFieldSearch.setOnAction(e -> {
            String projectName = projectFieldSearch.getText();
            if (Objects.equals(projectName, ""))
                return;

            for (var element : projectDisplay.getChildren()) {
                Button button = (Button) element;

                if (button.getProperties().get("projectName").equals(projectName)) {
                    button.requestFocus();
                    return;
                }
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

    @FXML
    public void populateDisplay() {
        for (var name : projects.keySet()) {
            Button button = new Button(projects.get(name));
            button.setAlignment(Pos.BASELINE_LEFT);
            button.setPrefWidth(MAX_VALUE);

            button.getProperties().put("path", name);
            button.getProperties().put("projectName", projects.get(name));

            button.setOnAction(e -> {
                MainWindowController mainWindowController = new MainWindowController();
                Scene mainWindowScene = new Scene(mainWindowController);
                var data = new Pair<>(Action.OPEN_PROJECT, Path.of(name));

                this.getScene().setUserData(data);
                this.setSecondScene(mainWindowScene);
                openSecondScene(e);
                try {
                    mainWindowController.setFirstScene(this.getScene());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            projectDisplay.getChildren().add(button);
        }
    }

    public void readConfig() throws IOException {
        String content = FileUtils.readFile(projectConfigPath.resolve("config"));
        String[] split = content.split("\n");
        projects = new HashMap<>();
        for (String s : split) {
            if (s.length() == 0)
                continue;

            String[] splitSpace = s.split(" ");
            System.out.println(splitSpace[0] + " <-> " + splitSpace[1]);
            projects.put(splitSpace[1], splitSpace[0]);
        }
    }
}
