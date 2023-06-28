package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class MainConsole extends BorderPane {
    @FXML
    TextArea output;
    @FXML
    TextField input;
    public MainConsole() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-console.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            System.out.println("load error");
        }
    }
}
