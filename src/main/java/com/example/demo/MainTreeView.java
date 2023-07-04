package com.example.demo;

import com.example.demo.guiutils.FileUtils;
import com.example.demo.myide.domain.entity.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class MainTreeView<T> extends TreeView<T> {
    @FXML
    Image folderIcon = new Image(getClass()
            .getResource("img/folder.png")
            .openStream());

    @FXML
    Image javaIcon = new Image(getClass()
            .getResource("img/java.png")
            .openStream());
    @FXML
    Image pythonIcon = new Image(getClass()
            .getResource("img/python.png")
            .openStream());

    public MainTreeView() throws IOException {
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

    /**
     *   Build the tree view of the project architecture.
     *   Calls recurseOnProjectNodes to traverse the project files and folders.
     *   TODO: remove code duplication when assigning treeItem color (python or java)
     */
    @FXML
    public void populateTreeView(MainWindowController windowController) {
        windowController.mainTreeView.setShowRoot(true);
        var root = new TreeItem<>(windowController.project.getRootNode());
        root.setExpanded(true);
        windowController.mainTreeView.setRoot(root);

        for (Node node : windowController.project.getRootNode().getChildren()) {
            if (node.isFolder())
                recurseOnProjectNodes(node, root);
            else {
                TreeItem<Node> item = new TreeItem<>(node);
                if (node.getPath().getFileName().toString().endsWith("java"))
                    item.setGraphic(new ImageView(javaIcon));
                else if (node.getPath().getFileName().toString().endsWith("py"))
                    item.setGraphic(new ImageView(pythonIcon));
                root.getChildren().add(item);
            }
        }
    }

    /**
     *   Recurse on the nodes of the project to build the tree.
     */
    @FXML
    public void recurseOnProjectNodes(Node n, TreeItem<Node> parent) {
        TreeItem<Node> cur = new TreeItem<>(n);
        for (Node child : n.getChildren()) {
            if (child.isFolder()) {
                recurseOnProjectNodes(child, cur);
            }
            else {
                TreeItem<Node> item = new TreeItem<>(child);
                if (child.getPath().getFileName().endsWith("java"))
                    item.setGraphic(new ImageView(javaIcon));
                else
                    item.setGraphic(new ImageView(pythonIcon));

                cur.getChildren().add(item);
            }
        }
        parent.setGraphic(new ImageView(folderIcon));
        parent.getChildren().add(cur);
    }

    /**
     * Add an event listener to the mainTreeView.
     * Opens a new tab when a menuItem is clicked in the file tree.
     * Store the project node inside the tab for later use (in the userData field).
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

    /**
     * Find the tree item with value equal to "value".
     */
    public TreeItem getTreeViewItem(TreeItem<Node> item , Node value)
    {
        if (item != null && item.getValue().equals(value))
            return item;

        for (TreeItem<Node> child : item.getChildren()){
            TreeItem<Node> s = getTreeViewItem(child, value);
            if (s!=null)
                return s;
        }
        return null;
    }

    /**
     * Find the tree item with value equal to parent,
     * and add node in its children list.
     */
    public void addItem(Node node, Node parent) {
        TreeItem<Node> parentItem = getTreeViewItem((TreeItem<Node>) this.getRoot(), parent);
        parentItem.getChildren().add(new TreeItem<>(node));
    }
}
