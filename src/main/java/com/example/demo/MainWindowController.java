package com.example.demo;

import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

import java.io.*;
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
    public ToolBar mainToolBar;
    Project project;
    File chosenPath;

    @FXML
    public void initialize() throws IOException {
        // Cannot make it work in the tabPane class
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);

        mainToolBar.getItems().add(createSaveFileButton());
        mainToolBar.getItems().add(createNewFileButton());
        populateMenuBar();
        setMainTreeViewClickEvent();
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
        Write the content of the file stored in the tab
        in its own file.
     */
    @FXML
    public void saveTab(Tab tab) {
        if (tab.getUserData() != null) {
            Node node = (Node) tab.getUserData();
            try {
                CodeArea codeArea = (CodeArea) tab.getContent();
                writeToFile(node.getPath(), codeArea.getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
        Creates a button to save the current file.
        Button created dynamically since its makes it easier to add events and images.
        However, this file is starting to become really long ...
        TODO: refactor functions that create buttons
     */
    @FXML
    public Button createSaveFileButton() throws IOException {
        Image saveIcon = loadImage("save-button.png");

        Button saveButton = new Button();
        saveButton.setGraphic(new ImageView(saveIcon));

        saveButton.setOnAction(event -> {
            saveTab(getActiveTab());
        });

        return saveButton;
    }

    /*
        Create a new file
        TODO: problem, where is this file stored:
            - if its in the current project, we need to reload the
              project (to add the new project node (which means change the backend)),
              and the modify the tree view.
            - if its somewhere else, we still need to create a node for this
              new file.
     */
    @FXML
    public Button createNewFileButton() throws IOException {
        Image saveIcon = loadImage("new-file.png");

        Button newFileButton = new Button();
        newFileButton.setGraphic(new ImageView(saveIcon));

        newFileButton.setOnAction(event -> {

        });

        return newFileButton;
    }

    /*
        Get the active tab from the tab pane.
     */
    @FXML
    public Tab getActiveTab() {
        return mainTabPane.getSelectionModel().getSelectedItem();
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
                populateTreeView();
            }
        });
    }

    /*
        Build the tree view of the project architecture.
        Calls recurseOnProjectNodes to traverse the project files and folders.
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

    /*
        Add an event listener to the mainTreeView.
        Opens a new tab when a menuItem is clicked in the file tree.
        Store the project node inside the tab for later use (in the userData field).
     */
    @FXML
    public void setMainTreeViewClickEvent() {
        mainTreeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue != null && newValue != oldValue){
                        Node node = newValue.getValue();
                        try {
                            if (node.isFile()) {
                                String content = readFile(node.getPath());
                                Tab newTab = mainTabPane.CreateTabWithCodeArea(node.
                                        getPath().getFileName().toString(), content);

                                newTab.setUserData(node);
                                mainTabPane.AddTab(newTab);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    /*
        Utility functions to read and write to files.
        TODO: move to utils folder
     */
    @FXML
    public String readFile(Path path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));

        StringBuilder res = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null)
            res.append(line).append("\n");

        reader.close();

        return res.toString();
    }

    @FXML
    public void writeToFile(Path path, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()));
        writer.write(content);

        writer.close();
    }
}
