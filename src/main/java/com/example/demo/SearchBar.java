package com.example.demo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.util.Pair;
import org.controlsfx.control.textfield.CustomTextField;
import org.fxmisc.richtext.CodeArea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class SearchBar extends ToolBar {

    @FXML
    Image chevronDownIcon = new Image(Objects.requireNonNull(getClass()
                    .getResource("img/chevron-down.png"))
            .openStream());

    @FXML
    Image chevronUpIcon = new Image(Objects.requireNonNull(getClass()
                    .getResource("img/chevron-up.png"))
            .openStream());
    @FXML
    Image closeIcon = new Image(Objects.requireNonNull(getClass()
                    .getResource("img/close.png"))
            .openStream());
    @FXML
    Image searchIcon = new Image(Objects.requireNonNull(getClass()
                    .getResource("img/search.png"))
            .openStream());
    @FXML public Button buttonCloseSearch;
    @FXML public CustomTextField fieldSearch;
    @FXML public Button buttonSearchUp;
    @FXML public Button buttonSearchDown;
    @FXML public Label labelMatches;

    @FXML public Label searchLabel;

    /**
     * List of pairs of integers, to keep track of the position
     * of the text we want to search for.
     */
    @FXML public ArrayList<Pair<Integer, Integer>> occurrences;

    /**
     *  Index of the current occurrence (moved with up and down arrows)
     */
    @FXML public Integer occurrenceIndex;

    public Boolean isOn;

    public SearchBar() throws IOException {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("search-bar.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        this.occurrences = new ArrayList<>();
        this.occurrenceIndex = 0;
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
    public void setSearchBar(MainWindowController controller) {
        setCloseEvent(controller);
        setOnTextFieldWrite(controller);
        setOnChevronDown(controller);
        setOnChevronUp(controller);
    }

    /**
     * When writing on Field, look for the occurrences of the text
     * contained in the field.
     * Then select said text, and set focus back to code area (to edit the selected text)
     */
    @FXML
    public void setOnTextFieldWrite(MainWindowController controller) {
        fieldSearch.setOnKeyPressed(e -> {
            if (!Objects.equals(fieldSearch.getText(), "")) {
                occurrences =
                        controller.mainTabPane.findOccurrences(controller.mainTabPane.getActiveTab(),
                        fieldSearch.getText());

                CodeArea area = (CodeArea) controller.mainTabPane.getActiveTab().getContent();

                if (occurrences.size() != 0)
                    area.selectRange(occurrences.get(0).getKey(), occurrences.get(0).getValue());

                if (e.getCode().equals(KeyCode.ENTER)) {
                    Platform.runLater(area::requestFocus);
                }
            }
        });
    }

    /**
     * Move through list of occurrences positions.
     */
    @FXML
    public void setOnChevronUp(MainWindowController controller) {
        buttonSearchUp.setOnAction(e -> {
            if (occurrences.size() != 0) {
                if (occurrenceIndex > 0) {
                    occurrenceIndex -= 1;
                    occurrenceIndex = Math.max(occurrenceIndex, 0);

                    CodeArea area = (CodeArea) controller.mainTabPane.getActiveTab().getContent();
                    area.selectRange(occurrences.get(occurrenceIndex).getKey(), occurrences.get(occurrenceIndex).getValue());

                    Platform.runLater(area::requestFocus);
                }
            }
        });

    }

    @FXML
    public void setOnChevronDown(MainWindowController controller) {
        buttonSearchDown.setOnAction(e -> {
            if (occurrences.size() != 0) {
                occurrenceIndex += 1;
                occurrenceIndex = Math.min(occurrenceIndex, occurrences.size() - 1);

                CodeArea area = (CodeArea) controller.mainTabPane.getActiveTab().getContent();
                area.selectRange(occurrences.get(occurrenceIndex).getKey(), occurrences.get(occurrenceIndex).getValue());

                Platform.runLater(area::requestFocus);
            }
        });
    }

    /*
        // search for toto in codeArea, by using Ctrl-F shortcut
    codeArea.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<>() {
        final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F,
                KeyCombination.CONTROL_DOWN);
        public void handle(KeyEvent ke) {
            if (keyComb.match(ke)) {
                System.out.println("Key Pressed: " + keyComb);
                findAndSelectString(tab, "toto");
                ke.consume(); // <-- stops passing the event to next node
            }
        }
    });
    */

    /**
     * Remove the searchBar from the main VBOX.
     * Clear the occurence list for the next search.
     * Clear the text field.
     */
    @FXML
    public void setCloseEvent(MainWindowController controller) {
        buttonCloseSearch.setOnAction(e -> {
            this.isOn = false;
            controller.mainBox.getChildren().remove(0);
            occurrences.clear();
            fieldSearch.clear();
        });
    }
}
