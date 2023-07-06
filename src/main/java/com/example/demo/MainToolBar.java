package com.example.demo;

import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.NodeClass;
import com.example.demo.myide.domain.entity.Report.GoodReport;
import com.example.demo.myide.domain.service.ProjectServiceInstance;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainToolBar extends ToolBar {
    @FXML Button saveButton;
    @FXML Button newFileButton;
    @FXML Button runButton;
    @FXML Button searchProjectButton;
    @FXML
    MenuButton mavenButton;
    @FXML
    MenuItem mavenExec;
    @FXML
    MenuItem mavenInstall;
    @FXML
    MenuItem mavenClean;
    @FXML
    MenuItem mavenPackage;
    @FXML
    MenuItem mavenTree;
    @FXML
    MenuItem mavenTest;
    @FXML SearchTextField searchTextField;
    @FXML Pane execPane;
    @FXML HBox execBox;

    Boolean collapsed;
    Boolean searchFieldIsOn;

    @FXML Image saveIcon = new Image(Objects.requireNonNull(getClass()
                    .getResource("img/save.png"))
            .openStream());

    @FXML Image newFileIcon = new Image(Objects.requireNonNull(getClass()
                    .getResource("img/addAny.png"))
            .openStream());
    @FXML
    Image runIcon = new Image(Objects.requireNonNull(getClass()
                    .getResource("img/run.png"))
            .openStream());

    @FXML
    Image searchIcon = new Image(Objects.requireNonNull(getClass()
                    .getResource("img/search.png"))
            .openStream());
    @FXML
    Image arrowCollapse = new Image(Objects.requireNonNull(getClass()
                    .getResource("img/arrow-collapse.png"))
            .openStream());
    @FXML
    Image mavenIcon = new Image(Objects.requireNonNull(getClass()
                    .getResource("img/maven-project.png"))
            .openStream());

    public MainToolBar() throws IOException {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-tool-bar.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        collapsed = true;
        searchFieldIsOn = false;

        try {
            loader.load();
        } catch (Exception e) {
            System.out.println("load error");
        }
    }

    /**
    * Creates a button to save the current file.
    * Button created dynamically since its makes it easier to add events and images.
    * When the tab does not have an userData (i.e. the file was not saved before,
    * open a fileChooser and select path where to save the file).
    */
    @FXML
    public void setSaveFileButton(EditorWindowController controller) throws IOException {
        saveButton.setGraphic(new ImageView(saveIcon));
        saveButton.setOnAction(event -> {
            Tab active = controller.mainTabPane.getActiveTab();

            if (active.getUserData() == null) {
                FileChooser fileChooser = new FileChooser();
                File savePath = fileChooser.showSaveDialog(controller.mainTabPane.getScene().getWindow());
                if (savePath == null)
                    return;

                // TODO: WARNING check if OK.
                if (controller.project == null) {
                    active.setText(savePath.toPath().getFileName().toString());
                    NodeClass newNode = new NodeClass(savePath.toPath(), Node.Types.FILE, null);

                    active.setText(savePath.toPath().getFileName().toString());
                    active.setUserData(newNode);

                    return;
                }
                //

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
    public void setNewFileButton(EditorWindowController controller) throws IOException {
        newFileButton.setGraphic(new ImageView(newFileIcon));
        newFileButton.setOnAction(event -> {
            Tab newTab = controller.mainTabPane.CreateTabWithCodeArea("untitled", "");
            controller.mainTabPane.AddTab(newTab);
        });
    }

    /**
     * Run the code contained in the current active tab of the tabPane.
     * TODO: change this to detect if file is python or java (java project: check if pom.xml exists)
     */
    @FXML
    public void setRunButton(EditorWindowController controller) throws IOException {
        runButton.setGraphic(new ImageView(runIcon));
        runButton.setOnAction(event -> {
            System.out.println("run button set");
            Boolean isPom = false;
            isPom = Node.FindNode(controller.project.getRootNode(), controller.project.getRootNode().getPath().resolve("pom.xml")).getKey();

            if (isPom) {
                System.out.println("has pom");
                runExec(controller);
            }
            else {
                Tab active = controller.mainTabPane.getActiveTab();
                Node node = (Node) active.getUserData();
                if (node != null) {
                    controller.mainConsole.execute("python " + node.getPath());
                }
            }
        });
    }

    public void runExec(EditorWindowController controller) {
        final Task<String> runTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                var execReport = ProjectServiceInstance.INSTANCE.execute(controller.project,
                        Mandatory.Features.Maven.EXEC);

                if (execReport instanceof GoodReport<?>) {
                    return (String) ((GoodReport<?>) execReport).getData();
                }
                else
                    System.out.println("bad report");

                return null;
            }
        };

        runTask.setOnSucceeded(event -> {
            controller.mainConsole.println(runTask.getValue());
        });

        Thread t = new Thread(runTask);
        t.setDaemon(true); // thread will not prevent application shutdown
        t.start();
    }

    /**
     * Set the search Button.
     * If user clicks on button, add a SearchTextField after the button,
     * and a Button with collapse arrow.
     */
    @FXML
    public void setSearchProjectButton(EditorWindowController controller) {
        searchProjectButton.setGraphic(new ImageView(searchIcon));
        searchProjectButton.setOnAction(event -> {
            this.searchTextField = new SearchTextField();

            this.collapsed = false;
            this.searchFieldIsOn = true;

            Button collapseButton = new Button();
            collapseButton.setGraphic(new ImageView(arrowCollapse));

            collapseButton.setOnAction(e -> {
                int size = this.execBox.getChildren().size();
                this.execBox.getChildren().remove(size - 1);
                this.execBox.getChildren().remove(size - 2);

                this.collapsed = true;
                searchProjectButton.setDisable(false);
            });

            if (!searchProjectButton.isDisabled() && !this.collapsed)
                this.execBox.getChildren().addAll(this.searchTextField, collapseButton);

            searchProjectButton.setDisable(true);
            this.searchTextField.setListener(controller);
        });
    }

    @FXML
    public void setMavenButton(EditorWindowController controller) {
        mavenButton.setGraphic(new ImageView(mavenIcon));
        if (controller.project == null)
            return;

    }

    @FXML
    public void setButtons(EditorWindowController controller) throws IOException {
        setNewFileButton(controller);
        setSaveFileButton(controller);
        setRunButton(controller);
        setSearchProjectButton(controller);
        setMavenButton(controller);
    }
}
