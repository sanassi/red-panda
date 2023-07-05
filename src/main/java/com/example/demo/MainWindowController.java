package com.example.demo;

import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class MainWindowController {
    @FXML public MainMenuBar mainMenuBar;
    @FXML public MainTabPane mainTabPane;
    @FXML public MainTreeView<Node> mainTreeView;
    @FXML public MainToolBar mainToolBar;
    @FXML public MainConsole mainConsole;
    @FXML public VBox mainBox;
    @FXML public SearchBar searchBar;

    Project project;
    File chosenPath;

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
                        mainTreeView.populateTreeView(MainWindowController.this);
                        if (project.getFeature(Mandatory.Features.Git.ADD).isPresent())
                            mainMenuBar.setGitMenu(MainWindowController.this);
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
}
