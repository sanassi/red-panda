package com.example.demo;

import com.example.demo.guiutils.FileUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import static com.example.demo.MainConsole.GUIUtils.runSafe;

public class MainConsole extends BorderPane {
    @FXML
    public TextArea output;
    @FXML
    public TextField input;

    @FXML
    public ArrayList<String> history = new ArrayList<>();
    public int historyPointer = 0;

    private Consumer<String> onMessageReceivedHandler;

    boolean isWindows = System.getProperty("os.name")
            .toLowerCase().startsWith("windows");

    ProcessBuilder builder;
    Process process;
    Path prevPath;

    public MainConsole() {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-console.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        builder = new ProcessBuilder();
        prevPath = null;

        try {
            loader.load();
        } catch (Exception e) {
            System.out.println("load error");
        }

        output.setEditable(false);

        input.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER -> {
                    String text = input.getText();
                    var split = Arrays.stream(text.split("\\s+")).toList();
                    ArrayList<String> in = new ArrayList<>(split);

                    if (isWindows) {
                        in.add(0, "/c");
                        in.add(0, "cmd.exe");
                    }

                    runSafe(() -> {
                        try {
                            if (prevPath != null)
                                builder.directory(prevPath.toFile());
                            builder.command(in);
                            process = builder.start();
                            process.waitFor();
                            String outStream = FileUtils.readFileFromInStream(process.getInputStream());
                            String errStream = FileUtils.readFileFromInStream(process.getErrorStream());

                            output.appendText(outStream);
                            output.appendText(errStream);

                            if (builder.directory() != null)
                                prevPath = builder.directory().toPath();

                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        history.add(text);
                        historyPointer++;
                        input.clear();
                    });
                }
                case UP -> {
                    if (historyPointer == 0) {
                        break;
                    }
                    historyPointer--;
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