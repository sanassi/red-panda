package com.example.demo;

import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.Report.GoodReport;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;

public class SearchTextField extends TextField {
    @FXML
    public ContextMenu entriesPopup;

    public SearchTextField() {
        super();
        this.entriesPopup = new ContextMenu();
        this.setPromptText("Search everywhere");
    }

    @FXML
    public void setListener(MainWindowController controller) {
        this.setOnAction(e -> {
            if (Objects.equals(this.getText(), ""))
                return;

            System.out.println("made it here");

            String toSearch = this.getText();
            Platform.runLater(() -> {
                GoodReport<List<Document>> found;

                if (controller.project == null)
                    return;

                entriesPopup.getItems().clear();

                var execReport = ProjectServiceInstance.INSTANCE.execute(controller.project,
                        Mandatory.Features.Any.SEARCH, toSearch);

                System.out.println(execReport.isSuccess());

                // Populate entriesPopup with list of files that were found
                if (execReport instanceof GoodReport<?>) {
                    System.out.println("is good report");
                    found = (GoodReport<List<Document>>) execReport;

                    for (Document doc : found.getData()) {
                        String path = doc.getField("path").stringValue();
                        Node node = Node.FindNode(controller.project.getRootNode(), Path.of(path)).getValue();

                        MenuItem item = new MenuItem(path);
                        item.setOnAction(event -> {
                            try {
                                controller.mainTabPane.openTab(node);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        entriesPopup.getItems().add(item);
                    }

                    // Show the result of the search
                    entriesPopup.show(SearchTextField.this, Side.BOTTOM, 0, 0);
                }
                e.consume();
            });

        });
    }
}
