package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;

public class TabController {
    @FXML
    private Tab myTab;

    protected void addTextArea() {
        myTab.setContent(new TextArea());
    }
}
