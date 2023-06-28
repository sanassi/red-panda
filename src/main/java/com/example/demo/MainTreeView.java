package com.example.demo;

import com.example.demo.guiutils.FileUtils;
import com.example.demo.myide.domain.entity.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;

import java.io.IOException;

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

    /*
        Build the tree view of the project architecture.
        Calls recurseOnProjectNodes to traverse the project files and folders.
     */
    @FXML
    public void populateTreeView(MainWindowController windowController) {
        windowController.mainTreeView.setShowRoot(true);
        var root = new TreeItem<>(windowController.project.getRootNode());
        windowController.mainTreeView.setRoot(root);

        for (Node node : windowController.project.getRootNode().getChildren()) {
            if (node.isFolder())
                recurseOnProjectNodes(node, root);
            else
                root.getChildren().add(new TreeItem<>(node));
        }
    }

    /*
        Recurse on the nodes of the project to build the tree.
     */
    @FXML
    public void recurseOnProjectNodes(Node n, TreeItem<Node> parent) {
        TreeItem<Node> cur = new TreeItem<>(n);
        for (Node child : n.getChildren()) {
            if (child.isFolder()) {
                recurseOnProjectNodes(child, cur);
            }
            else
                cur.getChildren().add(new TreeItem<>(child));
        }

        parent.getChildren().add(cur);
    }

    /*
    Add an event listener to the mainTreeView.
    Opens a new tab when a menuItem is clicked in the file tree.
    Store the project node inside the tab for later use (in the userData field).
 */
    @FXML
    public void setMainTreeViewClickEvent(MainWindowController mainWindowController) {
        mainWindowController.mainTreeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue != null && newValue != oldValue){
                        Node node = newValue.getValue();
                        try {
                            if (node.isFile()) {
                                String content = FileUtils.readFile(node.getPath());
                                Tab newTab = mainWindowController.mainTabPane.CreateTabWithCodeArea(node.
                                        getPath().getFileName().toString(), content);

                                newTab.setUserData(node);
                                mainWindowController.mainTabPane.AddTab(newTab);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }
}
