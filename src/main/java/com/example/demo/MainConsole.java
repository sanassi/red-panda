package com.example.demo;

import com.example.demo.guiutils.FileUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import static com.example.demo.MainConsole.GUIUtils.runSafe;

public class MainConsole extends BorderPane {
    @FXML public TextArea output;
    @FXML public TextField input;

    @FXML public ArrayList<String> history = new ArrayList<>();
    public int historyPointer = 0;

    private Consumer<String> onMessageReceivedHandler;

    boolean isWindows = System.getProperty("os.name")
            .toLowerCase().startsWith("windows");

    ProcessBuilder builder;
    Process process;

    public MainConsole() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-console.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        builder = new ProcessBuilder();

        try {
            loader.load();
        } catch (Exception e) {
            System.out.println("Console: load error");
        }

        output.setEditable(false);

        input.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER -> {
                    String text = input.getText();

                    execute(text);

                    history.add(text);
                    historyPointer++;
                    input.clear();
                }
                case UP -> {
                    if (historyPointer == 0) {
                        break;
                    }
                    historyPointer--;
                    historyPointer = Math.max(historyPointer, 0);
                    runSafe(() -> {
                        input.setText(history.get(historyPointer));
                        input.selectAll();
                    });
                }
                case DOWN -> {
                    if (historyPointer == history.size() - 1) {
                        break;
                    }
                    historyPointer++;
                    historyPointer = Math.min(historyPointer, history.size() - 1);
                    runSafe(() -> {
                        input.setText(history.get(historyPointer));
                        input.selectAll();
                    });
                }
                default -> {
                }
            }
        });
    }

    @FXML
    public void initialize() {
        output.setStyle("-fx-font-family: Consolas; -fx-font-size: 10pt");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem clear = new MenuItem("Clear");
        clear.setOnAction(e -> Platform.runLater(() -> output.clear()));
        contextMenu.getItems().add(clear);

        output.setContextMenu(contextMenu);
    }

    @FXML
    public void execute(String cmd) {
        final String[] outStream = {null};
        final String[] errStream = {null};

        final Task<Void> executeTask = new Task<Void>() {
            @Override
            protected Void call() throws IOException, InterruptedException {
                var split = Arrays.stream(cmd.split("\\s+")).toList();
                ArrayList<String> in = new ArrayList<>(split);
                if (isWindows) {
                    in.add(0, "/c");
                    in.add(0, "cmd.exe");
                }

                builder.command(in);
                process = builder.start();
                process.waitFor();

                outStream[0] = FileUtils.readFileFromInStream(process.getInputStream());
                errStream[0] = FileUtils.readFileFromInStream(process.getErrorStream());

                return null;
            }
        };

        executeTask.setOnSucceeded(event -> {
            output.appendText(outStream[0]);
            output.appendText(errStream[0]);
        });

        Thread t = new Thread(executeTask);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        input.requestFocus();
    }

    public void setOnMessageReceivedHandler(final Consumer<String> onMessageReceivedHandler) {
        this.onMessageReceivedHandler = onMessageReceivedHandler;
    }

    public void clear() {
        runSafe(() -> output.clear());
    }

    public void print(final String text) {
        Objects.requireNonNull(text, "text");
        runSafe(() -> output.appendText(text));
    }

    public void println(final String text) {
        Objects.requireNonNull(text, "text");
        runSafe(() -> output.appendText(text + System.lineSeparator()));
    }

    public void println() {
        runSafe(() -> output.appendText(System.lineSeparator()));
    }

    public final class GUIUtils {
        private GUIUtils() {
            throw new UnsupportedOperationException();
        }

        public static void runSafe(final Runnable runnable) {
            Objects.requireNonNull(runnable, "runnable");
            if (Platform.isFxApplicationThread()) {
                runnable.run();
            }
            else {
                Platform.runLater(runnable);
            }
        }
    }

}