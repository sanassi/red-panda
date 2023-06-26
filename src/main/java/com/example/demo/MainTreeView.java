package com.example.demo;

import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;

import java.io.File;

public class MainTreeView extends TreeView {
    public MainTreeView() {
        TreeItem<String> root1 = new TreeItem<String>("Programming Languages");
        TreeItem<String> item1 = new TreeItem<String>("Java");
        TreeItem<String> item2 = new TreeItem<String>("Python");
        TreeItem<String> item3 = new TreeItem<String>("C++");
        root1.getChildren().addAll(item1, item2, item3);

        this.setRoot(root1);
    }
}
