package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.IOException;

public class SearchBar extends ToolBar {

    @FXML
    Image chevronDownIcon = new Image(getClass()
            .getResource("img/chevron-down.png")
            .openStream());

    @FXML
    Image chevronUpIcon = new Image(getClass()
            .getResource("img/chevron-up.png")
            .openStream());
    @FXML
    Image closeIcon = new Image(getClass()
            .getResource("img/close.png")
            .openStream());
    @FXML
    Image searchIcon = new Image(getClass()
            .getResource("img/search.png")
            .openStream());
    @FXML public Button buttonCloseSearch;
    @FXML public CustomTextField fieldSearch;
    @FXML public Button buttonSearchUp;
    @FXML public Button buttonSearchDown;
    @FXML public Label labelMatches;

    @FXML public Label searchLabel;

    public Boolean isOn;

    public SearchBar() throws IOException {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("search-bar.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        isOn = false;

        try {
            loader.load();
        } catch (Exception e) {
            System.out.println("load error");
        }
    }

    @FXML
    public void initialize() {
        searchLabel.setGraphic(new ImageView(searchIcon));
        buttonSearchUp.setGraphic(new ImageView(chevronUpIcon));
        buttonSearchDown.setGraphic(new ImageView(chevronDownIcon));
        buttonCloseSearch.setGraphic(new ImageView(closeIcon));
    }

    @FXML
    public void setCloseEvent(MainWindowController controller) {
        buttonCloseSearch.setOnAction(e -> {
            controller.mainBox.getChildren().remove(0);
        });
    }
}
