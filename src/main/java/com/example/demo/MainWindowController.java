package com.example.demo;

import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainWindowController {
    @FXML
    public MainMenuBar mainMenuBar;
    @FXML
    public MainTabPane mainTabPane;
    @FXML
    public MainTreeView<Node> mainTreeView;
    @FXML
    public MainToolBar mainToolBar;
    @FXML
    public MainConsole mainConsole;
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

        onSearch();
    }

    /*
        Load image from path
        TODO: needs to be moved to a utils/ folder
     */
    @FXML
    public Image loadImage(String path) throws IOException {
        return new Image(getClass()
                .getResource(path)
                .openStream());
    }

    /*
        Add a project folder chooser and a listener on the MenuItem "Open Project"
        In the event listener Load the project using the path returned by the directoryChooser
     */
    @FXML
    public void addProjectFolderChooser(MenuItem menuItem, String textToDisplay) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(textToDisplay);
        menuItem.setOnAction(event -> {
            chosenPath = directoryChooser.showDialog((Stage) mainMenuBar.getScene().getWindow());
            if (chosenPath != null) {
                project = ProjectServiceInstance.INSTANCE.load(Path.of(chosenPath.getAbsolutePath()));
                Platform.runLater(() -> mainTreeView.populateTreeView(this));

                if (project.getFeature(Mandatory.Features.Git.ADD).isPresent())
                    mainMenuBar.setGitMenu(this);
            }
        });
    }

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
                    System.out.println("Key Pressed: " + keyComb);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
    }
}
