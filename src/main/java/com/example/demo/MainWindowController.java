package com.example.demo;

import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
                if (project.getFeature(Mandatory.Features.Git.ADD).isPresent())
                    mainMenuBar.setGitMenu(this);
            }
        });
    }
    @FXML
    public void addGitFeatures(MenuItem menuItem, String textToDisplay) {
        FileChooser directoryChooser = new FileChooser();
        directoryChooser.setInitialDirectory(project.getRootNode().getPath().toFile());
        directoryChooser.setTitle(textToDisplay);
        menuItem.setOnAction(event -> {
            File chosenPath = directoryChooser.showOpenDialog((Stage) mainMenuBar.getScene().getWindow());
            if (chosenPath != null) {
                //System.out.println(project.getRootNode().getPath().relativize(Paths.get(chosenPath.getPath())));
                ProjectServiceInstance.INSTANCE.execute(project, Mandatory.Features.Git.ADD,project.getRootNode().getPath().relativize(Paths.get(chosenPath.getPath())).toString().replace("\\","/"));
            }
        });
    }
    @FXML
    public void commitGitFeatures(MenuItem menuItem) {
        TextField textField1 = new TextField();
        TextField textField2 = new TextField();
        Button button = new Button("Submit");
        button.setTranslateX(250);
        button.setTranslateY(75);
        //Creating labels
        Label label1 = new Label("commit message: ");
        //Setting the message with read data
        Text text = new Text("");
        //Setting font to the label
        Font font = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 10);
        text.setFont(font);
        text.setTranslateX(15);
        text.setTranslateY(125);
        text.setFill(Color.BROWN);
        text.maxWidth(580);
        text.setWrappingWidth(580);
        //Displaying the message

        //Adding labels for nodes
        HBox box = new HBox(5);
        box.setPadding(new Insets(25, 5 , 5, 50));
        box.getChildren().addAll(label1, textField1);
        Group root = new Group(box, button, text);
        //Setting the stage
        Scene scene = new Scene(root, 595, 150, Color.BEIGE);
        Stage stage = new Stage();
        stage.setTitle("Commit");
        stage.setScene(scene);
        button.setOnAction(e -> {
            //Retrieving data
            String name = textField1.getText();
            text.setText("Youre commit message: "+ name +" !");
            ProjectServiceInstance.INSTANCE.execute(project, Mandatory.Features.Git.COMMIT, name);
            stage.close();
        });
        menuItem.setOnAction(event -> {
            stage.show();
        });
    }
    @FXML
    public void pullGitFeatures(MenuItem menuItem) {
        menuItem.setOnAction(event -> {
            ProjectServiceInstance.INSTANCE.execute(project, Mandatory.Features.Git.PULL);
        });
    }
    @FXML
    public void pushGitFeatures(MenuItem menuItem) {
        menuItem.setOnAction(event -> {
            ProjectServiceInstance.INSTANCE.execute(project, Mandatory.Features.Git.PUSH);

        });
    }
}
