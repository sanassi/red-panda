package com.example.demo.guiutils;

import javafx.fxml.FXML;

import java.io.*;
import java.nio.file.Path;

public class FileUtils {
    /*
    Utility functions to read and write to files.
    TODO: move to utils folder
 */
    @FXML
    public static String readFile(Path path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));

        StringBuilder res = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null)
            res.append(line).append("\n");

        reader.close();

        return res.toString();
    }

    @FXML
    public static void writeToFile(Path path, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()));
        writer.write(content);

        writer.close();
    }
}
