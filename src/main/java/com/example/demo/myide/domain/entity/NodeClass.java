package com.example.demo.myide.domain.entity;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NodeClass implements Node{

    public NodeClass(Path path, Types type, Node mom)
    {
        this.path_= path;
        this.dad_= mom;
        this.type_ = type;
        this.children_ = create_Children();
    }

    public void set_parent(Node parent)
    {
        dad_ = parent;
    }
    public void set_path(Path path)
    {
        this.path_=path;
    }
    @Override
    public Path getPath() {
        return path_;
    }

    @Override
    public Type getType() {
        return type_;
    }

    public List<@NotNull Node> create_Children() {
        if (isFile())
            return new ArrayList<>();
        else
        {
            File actual_root = new File(String.valueOf(path_));
            if (!actual_root.exists())
                return new ArrayList<>();

            List<Node> result = new ArrayList<Node>();

            for (File file : actual_root.listFiles())
            {
                Node tmp_node;
                if (file.isDirectory())
                {
                    tmp_node = new NodeClass(Path.of(file.getPath()),Types.FOLDER,this);
                }
                else
                {
                    tmp_node = new NodeClass(Path.of(file.getPath()), Types.FILE,this);
                }
                result.add(tmp_node);
            }
            return result;
        }
    }

    @Override
    public String toString() {
        return path_.getFileName().toString();
    }

    public Node getDad_() {
        return dad_;
    }

    public void set_children(List<Node> children)
    {
        children_=children;
    }

    @Override
    public List<@NotNull Node> getChildren() {
        return children_;
    }

    private Types type_;
    private List<Node> children_;
    private Path path_;
    private Node dad_;
}
