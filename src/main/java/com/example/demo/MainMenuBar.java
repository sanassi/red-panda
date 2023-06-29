package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

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
        MenuItem addFile = new MenuItem("Add File");
        MenuItem commit = new MenuItem("commit");
        MenuItem push = new MenuItem("push");
        MenuItem pull = new MenuItem("pull");

        //git_add(addFile,"file to add");
        MenuItem closeMenu = new MenuItem("close");
        gitMenu.getItems().addAll(addFile, commit, push, pull ,closeMenu);
        windowController.mainMenuBar.getMenus().add(gitMenu);
        windowController.addGitFeatures(addFile, "add");
        windowController.commitGitFeatures(commit);
        windowController.pullGitFeatures(pull);
        windowController.pushGitFeatures(push);



    }
}
