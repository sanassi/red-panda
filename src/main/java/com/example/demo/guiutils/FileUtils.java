package com.example.demo.guiutils;

import javafx.fxml.FXML;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    @FXML
    public static String readFileFromInStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder res = new StringBuilder();

        String line = "";
        while ((line = reader.readLine()) != null) {
            res.append(line).append("\n");
        }

        return res.toString();
    }

    public static void CreateDirectory(Path path) throws IOException {
        Files.createDirectory(path);
    }

    public static void addToJson(String id, String value, String file) {
        final ObjectMapper mapper = new ObjectMapper();
        try{
            String content = Files.readString(Path.of(file));
            Map<String, String> map;
            if (content.length() != 0)
                map = mapper.readValue(content, new TypeReference<Map<String,String>>(){});
            else
                map = new HashMap<>();
            map.put(id, value);

            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(mapper.writeValueAsString(map).getBytes());
            outputStream.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static Map<String, String> readJson(Path file) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        String content = Files.readString(file);
        Map<String, String> map;
        if (content.length() != 0) {
            System.out.println(content);
            map = mapper.readValue(content, new TypeReference<Map<String, String>>() {
            });
        }
        else
            map = new HashMap<>();

        return map;
    }
}
