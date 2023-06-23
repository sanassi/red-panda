package com.example.demo;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

public class MainTabPane extends TabPane {
    public MainTabPane() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-tab-pane.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        this.tabClosingPolicyProperty().set(TabClosingPolicy.SELECTED_TAB);
        this.getTabs().add(CreateNewTabButton());

        try {
            loader.load();
        } catch (Exception e) {
            System.out.println("load error");
        }
    }

    private Tab CreateNewTabButton() {
        Tab addTab = new Tab("+"); // You can replace the text with an icon
        addTab.setClosable(false);
        this.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab == addTab) {
                this.getTabs().add(this.getTabs().size() - 1, CreateTabWithTextArea("untitled")); // Adding new tab before the "button" tab
                this.getSelectionModel().select(this.getTabs().size() - 2); // Selecting the tab before the button, which is the newly created one
            }
        });
        return addTab;
    }

    public Tab CreateTabWithTextArea(String tabTitle) {
        Tab tab = new Tab(tabTitle);
        tab.setClosable(true);
        TextArea textArea = new TextArea();
        tab.setContent(textArea);
        return tab;
    }
}
