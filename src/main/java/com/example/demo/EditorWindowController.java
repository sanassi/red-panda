package com.example.demo;

import com.example.demo.guiutils.FileUtils;
import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.NodeClass;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class EditorWindowController extends BorderPane {
    @FXML public MainMenuBar mainMenuBar;
    @FXML public MainTabPane mainTabPane;
    @FXML public MainTreeView<Node> mainTreeView;
    @FXML public MainToolBar mainToolBar;
    @FXML public MainConsole mainConsole;
    @FXML public VBox mainBox;
    @FXML public SearchBar searchBar;
    @FXML private Scene firstScene;
    /**
     * Button used to collapse the consoleArea
     */
    @FXML public Button collapseButton;
    @FXML public VBox consoleBox;
    @FXML public SplitPane horizontalPane;

    Project project;
    File chosenPath;

    public void setFirstScene(Scene scene) throws IOException {
        firstScene = scene;
        if (firstScene.getUserData() != null) {
            Pair<Action, Path> userData = (Pair<Action, Path>) firstScene.getUserData();
            switch (userData.getKey()) {
                case OPEN_FILE -> {
                    var newTab = mainTabPane.CreateTabWithCodeArea(String.valueOf(userData.getValue().getFileName()),
                            FileUtils.readFile(userData.getValue()));

                    newTab.setUserData(new NodeClass(userData.getValue(), Node.Types.FILE, null));
                    mainTabPane.AddTab(newTab);
                }
                case NEW_PROJECT -> {
                    loadProject(userData.getValue(), "Choose a new project");
                }
                case OPEN_PROJECT -> {
                    loadProject(userData.getValue(), "Open a project");
                }
                default -> {

                }
            }
        }
    }

    public EditorWindowController() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-window.fxml"));
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
        // Cannot make it work in the tabPane class
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);

        mainToolBar.setButtons(this);
        mainMenuBar.setMenus(this);
        mainTreeView.setMainTreeViewClickEvent(this);
        searchBar = new SearchBar();
        searchBar.setSearchBar(this);
        mainMenuBar.setHelpMenu(this);

        setCollapseButton();
        mainConsole.getProperties().put("hidden", false);

        onSearch();
    }

    /*
        Load image from path
        TODO: needs to be moved to a utils/ folder
     */
    @FXML
    public Image loadImage(String path) throws IOException {
        return new Image(Objects.requireNonNull(getClass()
                        .getResource(path))
                .openStream());
    }

    /**
     * Add a project folder chooser and a listener on the MenuItem "Open Project"
     * In the event listener Load the project using the path returned by the directoryChooser
     * Use task to perform long task in a separate thread (to prevent app from freezing).
     * Then get the result of the task.
     */
    @FXML
    public void loadProjectFromLoadMenu(MenuItem menuItem, String textToDisplay) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(textToDisplay);
        menuItem.setOnAction(event -> {
            chosenPath = directoryChooser.showDialog((Stage) mainMenuBar.getScene().getWindow());
            if (chosenPath != null) {
                final Task<Project> loadProjectTask = new Task<Project>() {
                    @Override
                    protected Project call() {
                        return ProjectServiceInstance.INSTANCE.load(Path.of(chosenPath.getAbsolutePath()));
                    }
                };

                loadProjectTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        project = loadProjectTask.getValue(); // result of computation
                        // update UI with result
                        mainTreeView.populateTreeView(EditorWindowController.this);
                        if (project.getFeature(Mandatory.Features.Git.ADD).isPresent())
                            mainMenuBar.setGitMenu(EditorWindowController.this);
                    }
                });

                Thread t = new Thread(loadProjectTask);
                t.setDaemon(true); // thread will not prevent application shutdown
                t.start();

                /*
                project = ProjectServiceInstance.INSTANCE.load(Path.of(chosenPath.getAbsolutePath()));
                Platform.runLater(() -> mainTreeView.populateTreeView(this));

                if (project.getFeature(Mandatory.Features.Git.ADD).isPresent())
                    mainMenuBar.setGitMenu(this);

                 */
            }
        });
    }

    public void loadProject(Path projectPath, String textToDisplay) {
        chosenPath = projectPath.toFile();

        if (projectPath != null) {
            final Task<Project> loadProjectTask = new Task<Project>() {
                @Override
                protected Project call() {
                    return ProjectServiceInstance.INSTANCE.load(projectPath);
                }
            };

            loadProjectTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    project = loadProjectTask.getValue(); // result of computation
                    // update UI with result
                    mainTreeView.populateTreeView(EditorWindowController.this);
                    if (project.getFeature(Mandatory.Features.Git.ADD).isPresent())
                        mainMenuBar.setGitMenu(EditorWindowController.this);
                }
            });

            Thread t = new Thread(loadProjectTask);
            t.setDaemon(true); // thread will not prevent application shutdown
            t.start();

                /*
                project = ProjectServiceInstance.INSTANCE.load(Path.of(chosenPath.getAbsolutePath()));
                Platform.runLater(() -> mainTreeView.populateTreeView(this));

                if (project.getFeature(Mandatory.Features.Git.ADD).isPresent())
                    mainMenuBar.setGitMenu(this);

                 */
        }
    }

    /**
     * Event callback for when CTRL-F is pressed.
     * Add the SearchBar at the start of the main VBOX.
     */
    @FXML
    public void onSearch() {
        // search for toto in codeArea, by using Ctrl-F shortcut

        mainTabPane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F,
                    KeyCombination.CONTROL_DOWN);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke) && !searchBar.isOn) {
                    searchBar.isOn = true;
                    mainBox.getChildren().add(0, searchBar);
                    Platform.runLater(() -> searchBar.requestFocus());
                    System.out.println("Key Pressed: " + keyComb);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
    }

    @FXML
    public void setCollapseButton() {
        collapseButton.setOnAction(e -> {
            Boolean consoleHidden = (Boolean) mainConsole.getProperties().get("hidden");
            if (consoleHidden) {
                var previousDividers = (double[]) horizontalPane.getProperties().get("previousDividers");
                horizontalPane.setDividerPosition(0, previousDividers[0]);
                consoleBox.getChildren().add(mainConsole);
            }
            else {
                consoleBox.getChildren().remove(consoleBox.getChildren().size() - 1);
                var previousDividers = horizontalPane.getDividerPositions();

                horizontalPane.getProperties().put("previousDividers", previousDividers);
                horizontalPane.setDividerPosition(0, 1);
            }

            mainConsole.getProperties().put("hidden", !consoleHidden);
        });
    }
}
