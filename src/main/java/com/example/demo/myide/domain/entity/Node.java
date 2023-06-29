package com.example.demo.myide.domain.entity;

import com.example.demo.utils.Given;
import javafx.util.Pair;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.List;

@Given()
public interface Node {

    /**
     * @return The Node path.
     */
    @NotNull Path getPath();

    /**
     * @return The Node type.
     */
    @NotNull Type getType();

    /**
     * If the Node is a Folder, returns a list of its children,
     * else returns an empty list.
     *
     * @return List of node
     */
    @NotNull List<@NotNull Node> getChildren();

    static Pair<Boolean, Node> FindNode(Node startNode, Path path)
    {
        if (startNode.getPath().toString().equals(path.toString())) {
            return new Pair<>(true, startNode);
        }
        else {
            for (Node child : startNode.getChildren()) {
                var pair = FindNode(child, path);
                if (pair.getKey())
                    return pair;
            }
        }

        return new Pair<>(false, null);
    }

    default boolean isFile() {
        return getType().equals(Types.FILE);
    }

    default boolean isFolder() {
        return getType().equals(Types.FOLDER);
    }

    enum Types implements Type {
        FILE,
        FOLDER
    }

    interface Type {

    }
}
