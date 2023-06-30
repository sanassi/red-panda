package com.example.demo;

import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

public class MainMenuBar extends MenuBar {

    @FXML
    public Menu editMenu;
    @FXML
    public Menu fileMenu;
    @FXML
    public MenuItem openProject;

    public MainMenuBar() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-menu-bar.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void setMenus(MainWindowController windowController) {
        windowController.addProjectFolderChooser(openProject, "Open Project");
    }

    @FXML
    public void setGitMenu(MainWindowController windowController)
    {
        Menu gitMenu = new Menu("Git");
        MenuItem addFile = new MenuItem("Add");
        MenuItem commit = new MenuItem("Commit");
        MenuItem push = new MenuItem("Push");
        MenuItem pull = new MenuItem("Pull");

        //MenuItem closeMenu = new MenuItem("close");
        gitMenu.getItems().addAll(addFile, commit, push, pull);
        windowController.mainMenuBar.getMenus().add(gitMenu);
        addGitFeatures(windowController, addFile, "add");
        commitGitFeatures(windowController, commit);
        pullGitFeatures(windowController, pull);
        pushGitFeatures(windowController, push);
    }

    @FXML
    public void addGitFeatures(MainWindowController controller, MenuItem menuItem, String textToDisplay) {
        FileChooser directoryChooser = new FileChooser();
        directoryChooser.setInitialDirectory(controller.project.getRootNode().getPath().toFile());
        directoryChooser.setTitle(textToDisplay);
        menuItem.setOnAction(event -> {
            File chosenPath = directoryChooser.showOpenDialog((Stage) controller.mainMenuBar.getScene().getWindow());
            if (chosenPath != null) {
                //System.out.println(project.getRootNode().getPath().relativize(Paths.get(chosenPath.getPath())));
                ProjectServiceInstance.INSTANCE.execute(controller.project,
                        Mandatory.Features.Git.ADD, controller.project
                                .getRootNode()
                                .getPath()
                                .relativize(Paths.get(chosenPath.getPath()))
                                .toString().replace("\\","/"));
            }
        });
    }
    @FXML
    public void commitGitFeatures(MainWindowController controller, MenuItem menuItem) {
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
        Scene scene = new Scene(root, 595, 150, Color.WHITE);
        Stage stage = new Stage();
        stage.setTitle("Commit");
        stage.setScene(scene);
        button.setOnAction(e -> {
            //Retrieving data
            String name = textField1.getText();
            text.setText("Your commit message: "+ name +" !");
            ProjectServiceInstance.INSTANCE.execute(controller.project, Mandatory.Features.Git.COMMIT, name);
            stage.close();
        });
        menuItem.setOnAction(event -> {
            stage.show();
        });
    }
    @FXML
    public void pullGitFeatures(MainWindowController controller, MenuItem menuItem) {
        menuItem.setOnAction(event -> {
            ProjectServiceInstance.INSTANCE.execute(controller.project, Mandatory.Features.Git.PULL);
        });
    }
    @FXML
    public void pushGitFeatures(MainWindowController controller, MenuItem menuItem) {
        menuItem.setOnAction(event -> {
            ProjectServiceInstance.INSTANCE.execute(controller.project, Mandatory.Features.Git.PUSH);
        });
    }
}
