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
}
