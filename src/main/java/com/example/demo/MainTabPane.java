package com.example.demo;

import com.example.demo.guiutils.FileUtils;
import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.NodeClass;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.Subscription;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTabPane extends TabPane {
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
            System.out.println("main tab pane load error");
        }
    }

    /**
     * Get the active tab from the tab pane.
     */
    @FXML
    public Tab getActiveTab() {
        return getSelectionModel().getSelectedItem();
    }

    /**
     * Write the content of the file stored in the tab's userData field.
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

    /**
     * Creates a "dummy" tab, to serve as a button to add new tabs the tabPane.
     * The tabs are inserted before the "button".
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

    /**
     * Adds a new tab before the + tab button (to add a new tab),
     * then select the added tab
     */
    @FXML
    public void AddTab(Tab tab) {
        this.getTabs().add(this.getTabs().size() - 1, tab);
        this.getSelectionModel().select(this.getTabs().size() - 2);
    }

    /**
     * Returns a new tab, with its content being a new CodeArea.
     * The code area has its lines numbered, and by default the syntax highlighting is enabled.
     * Checks the extension of the file (Java or Python), and sets syntax highlighting accordingly
     * TODO: refactor function
     */
    @FXML
    public Tab CreateTabWithCodeArea(String tabTitle, String content) {
        Tab tab = new Tab(tabTitle);
        tab.setClosable(true);

        CodeArea codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setContextMenu( new DefaultContextMenu() );

        addSyntaxHighlightingEvent(codeArea, tabTitle);

        codeArea.replaceText(0, 0, content);
        boolean isPythonFile = tabTitle.matches("[a-zA-Z0-9_-]+.py");
        boolean isJavaFile = tabTitle.matches("[a-zA-Z0-9_-]+.java");

        if (isJavaFile) {
            codeArea.getStylesheets().add(getClass().getResource("styles/java-keywords.css").toExternalForm());
            (new Autocomplete(FileType.JAVA)).setAutocompletionListener(codeArea);
        }
        else if (isPythonFile) {
            codeArea.getStylesheets().add(getClass().getResource("styles/python-keywords.css").toExternalForm());
            (new Autocomplete(FileType.PYTHON)).setAutocompletionListener(codeArea);
        }
        else
            (new Autocomplete(FileType.OTHER)).setAutocompletionListener(codeArea);

        addAutoBracketsEvent(codeArea);

        tab.setContent(codeArea);
        codeArea.getStylesheets().add(getClass().getResource("styles/code-area.css").toExternalForm());

        return tab;
    }

    public void addSyntaxHighlightingEvent(CodeArea codeArea, String tabTitle) {
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
    }

    public void addAutoBracketsEvent(CodeArea codeArea) {
        codeArea.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals("{")) {
                codeArea.replaceText(codeArea.getCaretPosition(), codeArea.getCaretPosition(), "}");
                event.consume();
                codeArea.moveTo(codeArea.getCaretPosition() - 1);
            }
        });
    }

    /**
     * Recursively find the occurrences of "lookingFor", and store
     * them in an ArrayList
     * @param tab Tab where to start search
     * @param lookingFor String to search
     * @param res List of occurrences, contains positions of occurences of "lookingFor"
     * @param start Position where to start research
     */
    @FXML
    public void findOccurrencesRec(Tab tab, String lookingFor, ArrayList<Pair<Integer, Integer>> res, Integer start)
    {
        CodeArea codeArea = (CodeArea) tab.getContent();
        Pattern pattern = Pattern.compile("\\b" + lookingFor + "\\b");
        Matcher matcher = pattern.matcher(codeArea.getText());
        boolean found = matcher.find(start);
        if (found){
            res.add(new Pair<>(matcher.start(), matcher.end()));
            if (!matcher.hitEnd()) {
                findOccurrencesRec(tab, lookingFor, res, matcher.end());
            }
        }
    }

    @FXML
    public ArrayList<Pair<Integer, Integer>> findOccurrences(Tab tab, String lookingFor) {
        ArrayList<Pair<Integer, Integer>> res = new ArrayList<>();
        findOccurrencesRec(tab, lookingFor, res, 0);
        return res;
    }

    /**
     * Set the tab "tab" as active.
     * @param tab
     */
    @FXML
    public void openTab(Tab tab) {
        if (this.getTabs().contains(tab)) {
            this.getSelectionModel().select(tab);
        }
    }

    /**
     * Set the tab whose userData equal to "node"
     * as active.
     * If the tab does not contain a tab whose userData is equal to "node",
     * create and add the tab.
     * @param node the node associated with the tab (the tab's userData)
     * @throws IOException
     */
    @FXML
    public void openTab(Node node) throws IOException {
        Tab tab;
        for (Tab t : this.getTabs()) {
            if (t.getUserData() == null) {
                System.out.println("skipped tab");
                continue;
            }

            if (/*t.getUserData().equals(node)*/ ((NodeClass) t.getUserData()).getPath().equals(node.getPath())) {
                System.out.println(((NodeClass) t.getUserData()).getPath());
                System.out.println("found the tab hehe");
                this.getSelectionModel().select(t);
                return;
            }
        }

        tab = CreateTabWithCodeArea(String.valueOf(((NodeClass) node).getPath().getFileName()),
                FileUtils.readFile(node.getPath()));

        tab.setUserData(node);

        AddTab(tab);
        System.out.println("added tab");
    }
}
