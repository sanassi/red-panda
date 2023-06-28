package com.example.demo;

import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Path;

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
    public BorderPane mainConsole;
    Project project;
    File chosenPath;

    @FXML
    public void initialize() throws IOException {
        // Cannot make it work in the tabPane class
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);

        mainToolBar.setButtons(this);
        mainMenuBar.setMenus(this);
        mainTreeView.setMainTreeViewClickEvent(this);
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
                mainTreeView.populateTreeView(this);
            }
        });
    }
}
