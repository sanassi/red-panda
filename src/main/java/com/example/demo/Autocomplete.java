package com.example.demo;

import com.almasb.fxgl.logging.Logger;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import org.fxmisc.richtext.CodeArea;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Autocomplete {
    public Autocomplete(FileType fileType) {
        switch (fileType) {
            case JAVA -> {
                keywords = new String[] {
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
            }
            case PYTHON -> {
                keywords = new String[] {
                        "and", "as", "assert", "break", "class", "continue",
                        "def", "del", "elif", "else", "except", "False",
                        "finally", "for", "from", "global", "if", "import",
                        "in", "is", "None", "not", "or", "pass", "raise",
                        "return", "True", "try", "while", "with", "yield", "range"
                };
            }
            default -> {
                keywords = new String[]{};
            }
        }

        words = Arrays.stream(keywords).toList();
    }
    Logger autoCompleteLogger = Logger.get(Autocomplete.class.getName());

    /**
     * Utility function to check if character is not whitespace
     * @param c
     * @return Whether c is not a whitespace character
     */
    public Boolean isNotWhite(Character c) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_]+");
        return pattern.matcher(String.valueOf(c)).matches();
    }

    public String[] keywords;

    public List<String> words;


    /**
     * Add the autocomplete feature to the given CodeArea.
     * Add a listener to the caret, when the caret changes,
     * retrieve the word that is being typed and find the
     * keywords that can be used next.
     * @param codeArea
     */
    public void setAutocompletionListener(CodeArea codeArea) {
        autoCompleteLogger.info("Autocomplete: setting listener.");
        ContextMenu suggestions = new ContextMenu();

        codeArea.caretPositionProperty().addListener((obs, oldPosition, newPosition) -> {
            if (newPosition < oldPosition)
                return;

            String text = codeArea.getText().substring(0, newPosition);

            int index ;

            for (index = text.length() - 1;
                 index >= 0 && /*! Character.isWhitespace*/isNotWhite(text.charAt(index));
                 index--);

            int startIndex = Math.max(index, 0);
            String prefix = text.substring(index+1);

            for (index = newPosition;
                 index < codeArea.getLength() && /*! Character.isWhitespace*/isNotWhite(codeArea.getText().charAt(index));
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
                                item.setOnAction(click -> codeArea.replaceText(codeArea.getCaretPosition() - finalPrefix.length(),
                                        codeArea.getCaretPosition(), word));
                                return item;
                            })
                            .limit(100)
                            .collect(Collectors.toList())
            );

            if (codeArea.getCaretBounds().isPresent())
                suggestions.show(codeArea, codeArea.getCaretBounds().get().getMaxX(), codeArea.getCaretBounds().get().getMaxY());
        });

        autoCompleteLogger.info("Autocomplete: listener done");
    }
}
