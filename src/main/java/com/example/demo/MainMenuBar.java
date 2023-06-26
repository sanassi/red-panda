package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

public class MainMenuBar extends MenuBar {
    public MainMenuBar() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-menu-bar.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            System.out.println("load error");
        }
    }
}
