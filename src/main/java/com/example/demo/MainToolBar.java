package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class MainToolBar extends ToolBar {
    @FXML
    Button saveButton;
    @FXML
    Button newFileButton;


    @FXML
    Image saveIcon = new Image(getClass()
            .getResource("save-button.png")
            .openStream());

    @FXML
    Image newFileIcon = new Image(getClass()
            .getResource("new-file.png")
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
    However, this file is starting to become really long ...
    TODO: refactor functions that create buttons
 */
    @FXML
    public void setSaveFileButton(MainWindowController controller) throws IOException {
        saveButton.setGraphic(new ImageView(saveIcon));
        saveButton.setOnAction(event -> {
            controller.mainTabPane.saveTab(controller.mainTabPane.getActiveTab());
        });
    }

    /*
        Create a new file
        TODO: problem, where is this file stored ?:
            - if its in the current project, we need to reload the
              project (to add the new project node (which means change the backend)),
              and the modify the tree view.
            - if its somewhere else, we still need to create a node for this
              new file.
     */
    @FXML
    public void setNewFileButton(MainWindowController controller) throws IOException {
        newFileButton.setGraphic(new ImageView(newFileIcon));
        newFileButton.setOnAction(event -> {

        });
    }

    @FXML
    public void setButtons(MainWindowController controller) throws IOException {
        setNewFileButton(controller);
        setSaveFileButton(controller);
    }
}
