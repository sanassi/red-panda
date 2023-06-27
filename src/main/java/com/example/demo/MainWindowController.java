package com.example.demo;

import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;

public class MainWindowController {
    @FXML
    public MainMenuBar mainMenuBar;
    @FXML
    public MainTabPane mainTabPane;
    @FXML
    public TextArea consoleTextArea;
    @FXML
    public MainTreeView<Node> mainTreeView;

    Project project;
    File chosenPath;
    @FXML
    public void initialize() {
        populateMenuBar();
        setMainTreeViewClickEvent();
    }

    /*
        Add the menus to the mainMenuBar
        Adds listeners to menu items (such as Project Open)
     */
    @FXML
    public void populateMenuBar() {
        Menu fileMenu = new Menu("File");
        MenuItem openProjectItem = new MenuItem("Open project");
        addProjectFolderChooser(openProjectItem, "Select project");
        MenuItem closeItem = new MenuItem("Close");

        fileMenu.getItems().addAll(openProjectItem, closeItem);

        mainMenuBar.getMenus().add(fileMenu);
    }

    /*
        Add a project chooser and a listener on the MenuItem "Open Project"
        In the event listener load the project using the path returned by th directoryChooser
     */
    @FXML
    public void addProjectFolderChooser(MenuItem menuItem, String textToDisplay) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(textToDisplay);
        menuItem.setOnAction(event -> {
            chosenPath = directoryChooser.showDialog((Stage) mainMenuBar.getScene().getWindow());
            if (chosenPath != null) {
                project = ProjectServiceInstance.INSTANCE.load(Path.of(chosenPath.getAbsolutePath()));
                populateTreeView();
            }
        });
    }

    /*
        Build the tree view of the project architecture.
     */
    @FXML
    public void populateTreeView() {
        mainTreeView.setShowRoot(true);
        var root = new TreeItem<>(project.getRootNode());
        mainTreeView.setRoot(root);

        for (Node node : project.getRootNode().getChildren()) {
            if (node.isFolder())
                recurseOnProjectNodes(node, root);
            else
                root.getChildren().add(new TreeItem<>(node));
        }
    }

    /*
        Recurse on the nodes of the project to build the tree.
     */
    @FXML
    public void recurseOnProjectNodes(Node n, TreeItem<Node> parent) {
        TreeItem<Node> cur = new TreeItem<>(n);
        for (Node child : n.getChildren()) {
            if (child.isFolder()) {
                recurseOnProjectNodes(child, cur);
            }
            else
                cur.getChildren().add(new TreeItem<>(child));
        }

        parent.getChildren().add(cur);
    }

    @FXML
    public void setMainTreeViewClickEvent() {
        mainTreeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue != null && newValue != oldValue){
                        System.out.println(newValue.getValue().getPath().getFileName());
                    }
                });
    }
}
