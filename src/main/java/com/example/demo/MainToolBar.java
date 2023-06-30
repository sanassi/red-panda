package com.example.demo;

import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.NodeClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MainToolBar extends ToolBar {
    @FXML
    Button saveButton;
    @FXML
    Button newFileButton;
    @FXML
    Button runButton;
    @FXML
    Image saveIcon = new Image(getClass()
            .getResource("save-button.png")
            .openStream());

    @FXML
    Image newFileIcon = new Image(getClass()
            .getResource("new-file.png")
            .openStream());
    @FXML
    Image runIcon = new Image(getClass()
            .getResource("execute.png")
            .openStream());

    public MainToolBar() throws IOException {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-tool-bar.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            System.out.println("load error");
        }
    }

    /*
    Creates a button to save the current file.
    Button created dynamically since its makes it easier to add events and images.
    When the tab does not an userData (i.e. the file was not saved before,
    open a fileChooser and select path where to save the file).
 */
    @FXML
    public void setSaveFileButton(MainWindowController controller) throws IOException {
        saveButton.setGraphic(new ImageView(saveIcon));
        saveButton.setOnAction(event -> {
            Tab active = controller.mainTabPane.getActiveTab();

            if (active.getUserData() == null) {
                FileChooser fileChooser = new FileChooser();
                File savePath = fileChooser.showSaveDialog(controller.mainTabPane.getScene().getWindow());
                if (savePath == null)
                    return;
                Pair<Boolean, Node> foundRes = Node.FindNode(controller.project.getRootNode(), savePath.toPath().getParent());
                if (!foundRes.getKey())
                    return;

                Node parent = foundRes.getValue();

                active.setText(savePath.toPath().getFileName().toString());

                NodeClass newNode = new NodeClass(savePath.toPath(), Node.Types.FILE, parent);

                active.setUserData(newNode);
                // Add the new node in its parent children list
                parent.getChildren().add(newNode);

                controller.mainTreeView.addItem(newNode, parent);

                System.out.println(((Node) active.getUserData()).getPath());
            }

            controller.mainTabPane.saveTab(active);
        });
    }

    @FXML
    public void setNewFileButton(MainWindowController controller) throws IOException {
        newFileButton.setGraphic(new ImageView(newFileIcon));
        newFileButton.setOnAction(event -> {
            Tab newTab = controller.mainTabPane.CreateTabWithCodeArea("untitled", "");
            controller.mainTabPane.AddTab(newTab);
        });
    }

    @FXML
    public void setRunButton(MainWindowController controller) throws IOException {
        runButton.setGraphic(new ImageView(runIcon));
        runButton.setOnAction(event -> {
            Tab active = controller.mainTabPane.getActiveTab();
            Node node = (Node) active.getUserData();
            if (node != null) {
                controller.mainConsole.execute("python " + node.getPath());
            }
        });
    }

    @FXML
    public void setButtons(MainWindowController controller) throws IOException {
        setNewFileButton(controller);
        setSaveFileButton(controller);
        setRunButton(controller);
    }
}
