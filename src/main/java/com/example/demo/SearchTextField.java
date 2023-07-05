package com.example.demo;

import com.almasb.fxgl.logging.Logger;
import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.Report.GoodReport;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.java.Log;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;

public class SearchTextField extends TextField {
    static Logger searchTextFieldLogger = Logger.get(SearchTextField.class.getName());

    @FXML
    public ContextMenu entriesPopup;

    public SearchTextField() {
        super();
        this.entriesPopup = new ContextMenu();
        this.setPromptText("Search everywhere");
    }

    /**
     * Starts search on whole project.
     * When user writes iin searchField and clicks on enter,
     * create task to search for the word, then
     * get the result of the task, and display the results.
     */
    @FXML
    public void setListener(MainWindowController controller) {
        this.setOnAction(e -> {
            if (Objects.equals(this.getText(), ""))
                return;

            String toSearch = this.getText();

            final Task<List<Document>> searchTask = new Task<>() {
                @Override
                protected List<Document> call() {
                    GoodReport<List<Document>> found;

                    searchTextFieldLogger.debug("SearchTextField: searching");

                    var execReport = ProjectServiceInstance.INSTANCE.execute(controller.project,
                            Mandatory.Features.Any.SEARCH, toSearch);
                    if (execReport instanceof GoodReport<?>) {
                        found = (GoodReport<List<Document>>) execReport;
                        return found.getData();
                    }

                    return null;
                }
            };

            searchTask.setOnSucceeded(event -> {
                List<Document> found = searchTask.getValue(); // result of computation
                // update UI with result
                entriesPopup.requestFocus();
                for (Document doc : found) {
                    String path = doc.getField("path").stringValue();

                    Node node = Node.FindNode(controller.project.getRootNode(), Path.of(path)).getValue();

                    MenuItem item = new MenuItem(path);
                    item.setOnAction(click -> {
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
                searchTextFieldLogger.info("SearchTextField: searching done");
            });

            Thread t = new Thread(searchTask);
            t.setDaemon(true); // thread will not prevent application shutdown
            t.start();

            e.consume();
        });
    }
}
