package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.nio.file.Path;

public class MainWindowController {
    @FXML
    public MainMenuBar mainMenuBar;
    @FXML
    public MainTabPane mainTabPane;
    @FXML
    public TextArea consoleTextArea;
    @FXML
    public MainTreeView mainTreeView;
}
