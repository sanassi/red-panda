package com.example.demo;

import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.Report.GoodReport;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.lucene.document.Document;

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
            GoodReport<List<Document>> found;

            if (controller.project == null)
                return;

            // Populate entriesPopup with list of files that was found
            entriesPopup.getItems().addAll(new MenuItem("File 1"), new MenuItem("File 2"));
            entriesPopup.show(SearchTextField.this, Side.BOTTOM, 0, 0);

            var execReport = ProjectServiceInstance.INSTANCE.execute(controller.project,
                    Mandatory.Features.Any.SEARCH, toSearch);

            System.out.println(execReport.isSuccess());

            if (execReport instanceof GoodReport<?>) {
                System.out.println("is good report");
                found = (GoodReport<List<Document>>) execReport;
                for (Document doc : found.getData())
                    System.out.println(doc);
            }
            e.consume();
        });
    }
}
