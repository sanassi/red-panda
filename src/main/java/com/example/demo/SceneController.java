package com.example.demo;

import com.example.demo.guiutils.FileUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
    @FXML private Scene codeEditorScene;

    /**
     * Path of the project configuration
     * folder.
     * Now just contains the history of previously
     * opened projects.
     */
    @FXML public Path projectConfigPath;

    /**
     * Map to store the path and name of the projects that
     * were previously opened.
     */
    @FXML public Map<String, String> projects;
    @FXML public Label projectSearchLabel;

    @FXML public CustomTextField projectFieldSearch;

    @FXML
    Image searchIcon = new Image(Objects.requireNonNull(getClass()
                    .getResource("img/search.png"))
            .openStream());

    /**
     * Keep track of the second scene of the program,
     * the actual code editor.
     * @param codeEditorScene the scene of the code editor
     */
    public void setCodeEditorScene(Scene codeEditorScene) {
        this.codeEditorScene = codeEditorScene;
    }

    /**
     * Open the second scene, using the actionEvent to get the
     * primaryStage of the application.
     * @param actionEvent event used to get the window of the current scene,
     *                    to retrieve the primaryStage of the application
     */
    public void openCodeEditorScene(ActionEvent actionEvent) {
        try {
            Stage primaryStage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            codeEditorScene.getStylesheets().add(getClass().getResource("styles/main.css").toExternalForm());
            primaryStage.setScene(codeEditorScene);
        } catch (Exception e) {
            System.out.println("fail");
        }
    }

    public SceneController() throws IOException {
        super();
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("open-project-window.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        this.getStylesheets().add(getClass()
                .getResource("styles/open-project-window.css").toExternalForm());

        projectConfigPath = Path.of(".panda");
        projects = new HashMap<>();

        try {
            FileUtils.CreateDirectory(projectConfigPath);

        } catch (Exception e) {
            System.err.println("[INFO] create project configuration folder failed.");
        }

        File projectHistoryFile = new File(projectConfigPath.toAbsolutePath().resolve("projectHistoryFile").toString());
        System.out.println(projectHistoryFile.createNewFile());

        try {
            loader.load();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Set the main components of the scene,
     * and load the projectHistory (the list of previous projects)
     * to display in the scene.
     * @throws IOException
     */
    @FXML
    public void initialize() throws IOException {
        // dirty fix
        logoView = (ImageView) this.lookup("#logoView");
        logoView.setImage(new Image(Objects.requireNonNull(getClass()
                        .getResource("img/panda-logo.png"))
                .openStream()));

        projectSearchLabel.setGraphic(new ImageView(searchIcon));

        readProjectHistory();
        populateDisplay();
        setFindProjectInRecent();
        setButtons();
    }

    /**
     * Set and switch between the scenes of the application.
     * Given the action that was taken in the OpenProjectMenu,
     * store in the userData of the currentScene the action and the path of the project / file that was opened
     * The open the codeEditorScene.
     * @param action
     * @param e
     * @throws IOException
     */
    public void setScenes(Action action, ActionEvent e) throws IOException {
        MainWindowController codeEditorWindowController = new MainWindowController();
        Scene codeEditorScene = new Scene(codeEditorWindowController);

        Pair<Action, Path> data;
        switch (action) {
            case OPEN_PROJECT -> {
                data = new Pair<>(Action.OPEN_PROJECT, openFolder());
                FileUtils.writeToFile(projectConfigPath.resolve("projectHistoryFile"),
                        FileUtils.readFile(projectConfigPath.resolve("projectHistoryFile")) + "\n" +
                                data.getValue().getFileName().toString() + " " + data.getValue().toString());
            }
            case NEW_PROJECT -> {
                data = new Pair<>(Action.NEW_PROJECT, openFolder());
                FileUtils.writeToFile(projectConfigPath.resolve("projectHistoryFile"),
                        FileUtils.readFile(projectConfigPath.resolve("projectHistoryFile")) + "\n" +
                                data.getValue().getFileName().toString() + " " + data.getValue().toString());
            }
            case OPEN_FILE -> data = new Pair<>(Action.OPEN_FILE, openFile());
            default -> throw new IllegalStateException("Unexpected value: " + action);
        }

        this.getScene().setUserData(data);
        this.setCodeEditorScene(codeEditorScene);

        openCodeEditorScene(e);

        try {
            codeEditorWindowController.setFirstScene(this.getScene());
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
        });

        openFileButton.setOnAction(e -> {
            try {
                setScenes(Action.OPEN_FILE, e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        newProjectButton.setOnAction(e -> {
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
                this.setCodeEditorScene(mainWindowScene);
                openCodeEditorScene(e);
                try {
                    mainWindowController.setFirstScene(this.getScene());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            projectDisplay.getChildren().add(button);
        }
    }

    public void readProjectHistory() throws IOException {
        String content = FileUtils.readFile(projectConfigPath.resolve("projectHistoryFile"));
        String[] split = content.split("\n");
        projects = new HashMap<>();
        for (String s : split) {
            if (s.length() == 0)
                continue;

            String[] splitSpace = s.split(" ");
            projects.put(splitSpace[1], splitSpace[0]);
        }
    }

    /**
     * Utility functions to open a Director/File chooser from this window.
     */
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
