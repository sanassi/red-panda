package com.example.demo;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;

import java.io.File;

public class MainTreeView<T> extends TreeView<T> {
    public MainTreeView() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-tree-view.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            System.out.println("load error");
        }
    }
}
