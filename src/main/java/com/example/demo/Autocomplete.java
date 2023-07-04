package com.example.demo;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import org.fxmisc.richtext.CodeArea;
import java.util.Arrays;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Autocomplete {

    public static void setListener(CodeArea codeArea) {
        final String[] keywords = new String[] {
                "abstract", "assert", "boolean", "break", "byte",
                "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else",
                "enum", "extends", "final", "finally", "float",
                "for", "goto", "if", "implements", "import",
                "instanceof", "int", "interface", "long", "native",
                "new", "package", "private", "protected", "public",
                "return", "short", "static", "strictfp", "super",
                "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while"
        };

        ContextMenu suggestions = new ContextMenu();
        //suggestions.hide();

        List<String> words = Arrays.stream(keywords).toList();

        codeArea.caretPositionProperty().addListener((obs, oldPosition, newPosition) -> {
            if (newPosition < oldPosition)
                return;

            String text = codeArea.getText().substring(0, newPosition);

            int index ;

            for (index = text.length() - 1;
                 index >= 0 && ! Character.isWhitespace(text.charAt(index));
                 index--);

            int startIndex = Math.max(index, 0);
            String prefix = text.substring(index+1);

            for (index = newPosition;
                 index < codeArea.getLength() && ! Character.isWhitespace(codeArea.getText().charAt(index));
                 index++);

            String suffix = codeArea.getText().substring(newPosition, index);

            // replace regex wildcards (literal ".") with "\.". Looks weird but correct...
            prefix = prefix.replaceAll("\\.", "\\.");
            suffix = suffix.replaceAll("\\.", "\\.");

            if (prefix.length() == 0 && suffix.length() == 0)
                return;

            Pattern pattern = Pattern.compile(prefix+".+"+suffix,
                    Pattern.CASE_INSENSITIVE);

            String finalPrefix = prefix;
            suggestions.getItems().setAll(
                    words.stream().filter(word -> pattern.matcher(word).matches())
                            .map(word -> {
                                MenuItem item = new MenuItem(word);
                                item.setOnAction(click -> {
                                    codeArea.replaceText(codeArea.getCaretPosition() - finalPrefix.length(), codeArea.getCaretPosition(), word);
                                });
                                return item;
                            })
                            .limit(100)
                            .collect(Collectors.toList())
            );

            suggestions.show(codeArea, codeArea.getCaretBounds().get().getMaxX(), codeArea.getCaretBounds().get().getMaxY());
        });
    }
}
