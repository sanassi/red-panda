package com.example.demo;

import com.example.demo.guiutils.FileUtils;
import com.example.demo.myide.domain.entity.Node;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.Subscription;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTabPane extends TabPane {
    /*
        Create an empty tab Pane, with only the "Add Tab (+)" tab (use as a button here).
     */
    public MainTabPane() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-tab-pane.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        setTabClosingPolicy(MainTabPane.TabClosingPolicy.SELECTED_TAB);
        this.getTabs().add(CreateNewTabButton());

        try {
            loader.load();
        } catch (Exception e) {
            System.out.println("load error");
        }
    }

    /*
    Get the active tab from the tab pane.
 */
    @FXML
    public Tab getActiveTab() {
        return getSelectionModel().getSelectedItem();
    }

    /*
    Write the content of the file stored in the tab.
    */
    @FXML
    public void saveTab(Tab tab) {
        if (tab.getUserData() != null) {
            Node node = (Node) tab.getUserData();
            try {
                CodeArea codeArea = (CodeArea) tab.getContent();
                FileUtils.writeToFile(node.getPath(), codeArea.getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
        Creates a "dummy" tab, to serve as a button to add new tabs to
        the tabPane.
        The tabs are inserted before the "button".
     */
    @FXML
    private Tab CreateNewTabButton() {
        Tab addTab = new Tab("+"); // You can replace the text with an icon
        addTab.setClosable(false);
        this.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab == addTab) {
                this.getTabs().add(this.getTabs().size() - 1,
                        CreateTabWithCodeArea("untitled", "")); // Adding new tab before the "button" tab
                this.getSelectionModel()
                        .select(this.getTabs().size() - 2); // Selecting the tab before the button, which is the newly created one
            }
        });
        return addTab;
    }

    /*
        Adds a new tab before the + tab button (to add a new tab),
        then select the added tab
     */
    @FXML
    public void AddTab(Tab tab) {
        this.getTabs().add(this.getTabs().size() - 1, tab);
        this.getSelectionModel().select(this.getTabs().size() - 2);
    }

    /*
        Returns a new tab, with its content being a new CodeArea.
        The code area has its lines numbered, and by default the syntax highlighting is enabled.
     */
    @FXML
    public Tab CreateTabWithCodeArea(String tabTitle, String content) {
        Tab tab = new Tab(tabTitle);
        tab.setClosable(true);

        CodeArea codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setContextMenu( new DefaultContextMenu() );

        // recompute the syntax highlighting for all text, 500 ms after user stops editing area
        // Note that this shows how it can be done but is not recommended for production with
        // large files as it does a full scan of ALL the text every time there is a change !
        Subscription cleanupWhenNoLongerNeedIt = codeArea

                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))

                // run the following code block when previous stream emits an event
                //.subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
                .subscribe(ignore -> codeArea.setStyleSpans(0, (tabTitle.matches("[a-zA-Z0-9_-]+.java") ?
                        SyntaxHighlightingJava.computeHighlighting(codeArea.getText()) :
                        SyntaxHighlightingPython.computeHighlighting(codeArea.getText()))));

        // when no longer need syntax highlighting and wish to clean up memory leaks
        // run: `cleanupWhenNoLongerNeedIt.unsubscribe();`


        codeArea.getVisibleParagraphs().addModificationObserver
                (
                        (tabTitle.matches("[a-zA-Z0-9_-]+.java") ?
                                new SyntaxHighlightingJava.VisibleParagraphStyler<>( codeArea, SyntaxHighlightingJava::computeHighlighting)
                                : new SyntaxHighlightingPython.VisibleParagraphStyler<>( codeArea, SyntaxHighlightingPython::computeHighlighting))
                        //new SyntaxHighlighting.VisibleParagraphStyler<>( codeArea, SyntaxHighlighting::computeHighlighting )
                );

        // auto-indent: insert previous line's indents on enter
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        codeArea.addEventHandler( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.ENTER ) {
                int caretPosition = codeArea.getCaretPosition();
                int currentParagraph = codeArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher( codeArea.getParagraph( currentParagraph-1 ).getSegments().get( 0 ) );
                if ( m0.find() ) Platform.runLater( () -> codeArea.insertText( caretPosition, m0.group() ) );
            }
        });

        codeArea.replaceText(0, 0, content);
        if (tabTitle.matches("[a-zA-Z0-9_-]+.java"))
            codeArea.getStylesheets().add(getClass().getResource("java-keywords.css").toExternalForm());
        else
            codeArea.getStylesheets().add(getClass().getResource("python-keywords.css").toExternalForm());

        tab.setContent(codeArea);
        codeArea.setStyle("-fx-font-family: consolas; -fx-font-size: 9pt;");

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

        return tab;
    }

    @FXML
    public void findAndSelectString(Tab tab, String lookingFor)
    {
        CodeArea codeArea = (CodeArea) tab.getContent();
        Pattern pattern = Pattern.compile("\\b" + lookingFor + "\\b");
        Matcher matcher = pattern.matcher(codeArea.getText());
        boolean found = matcher.find(0);
        if (found){
            codeArea.selectRange(matcher.start(), matcher.end());
        }
    }
}
