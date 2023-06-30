package com.example.demo;

import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckTreeView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class MainMenuBar extends MenuBar {

    @FXML
    public Menu editMenu;
    @FXML
    public Menu fileMenu;
    @FXML
    public MenuItem openProject;
    // Git MenuItems
    @FXML
    public Menu gitMenu;
    @FXML
    public MenuItem gitAddButton;
    @FXML
    public MenuItem gitCommitButton;
    @FXML
    public MenuItem gitPushButton;
    @FXML
    public MenuItem gitPullButton;


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
        gitMenu.setVisible(true);
        gitAddButton.setOnAction(event -> {
            addGitFeatures(windowController, "add");
        });
        gitCommitButton.setOnAction(event -> {
            try {
                commitGitFeatures(windowController);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        gitPullButton.setOnAction(event -> {
            pullGitFeatures(windowController);
        });
        gitPushButton.setOnAction(event -> {
            pushGitFeatures(windowController);
        });
    }

    @FXML
    public void addGitFeatures(MainWindowController controller, String textToDisplay) {
        FileChooser directoryChooser = new FileChooser();
        directoryChooser.setInitialDirectory(controller.project.getRootNode().getPath().toFile());
        directoryChooser.setTitle(textToDisplay);
            File chosenPath = directoryChooser.showOpenDialog((Stage) controller.mainMenuBar.getScene().getWindow());
            if (chosenPath != null) {
                ProjectServiceInstance.INSTANCE.execute(controller.project,
                        Mandatory.Features.Git.ADD, controller.project
                                .getRootNode()
                                .getPath()
                                .relativize(Paths.get(chosenPath.getPath()))
                                .toString().replace("\\","/"));
            }
    }
    @FXML
    public void commitGitFeatures(MainWindowController controller/*, MenuItem menuItem*/) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("git-commit-popup.fxml"));
        Stage commitStage = loader.load();
        commitStage.show();

        Scene commitScene = commitStage.getScene();
        TextArea commitText = (TextArea) commitScene.lookup("#commitTextArea");
        Button commitCancel = (Button) commitScene.lookup("#commitCancel");
        Button commitOk = (Button) commitScene.lookup("#commitOk");

        commitOk.setOnAction(event -> {
            //Retrieving data
            String name = commitText.getText();
            ProjectServiceInstance.INSTANCE.execute(controller.project, Mandatory.Features.Git.COMMIT, name);
            commitStage.close();
        });

        commitCancel.setOnAction(event -> {
            commitStage.close();
        });
    }
    @FXML
    public void pullGitFeatures(MainWindowController controller/*, MenuItem menuItem*/) {
            ProjectServiceInstance.INSTANCE.execute(controller.project, Mandatory.Features.Git.PULL);
    }
    @FXML
    public void pushGitFeatures(MainWindowController controller/*, MenuItem menuItem*/) {
            ProjectServiceInstance.INSTANCE.execute(controller.project, Mandatory.Features.Git.PUSH);
    }
}
