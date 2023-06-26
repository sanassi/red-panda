package com.example.demo.myide.domain.service;

import com.example.demo.myide.domain.entity.Node;
import com.example.demo.myide.domain.entity.NodeClass;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public enum NodeServiceInstance implements NodeService{
    INSTANCE;
    @Override
    public Node update(Node node, int from, int to, byte[] insertedContent) {
        File file =  new File(node.getPath().toString());
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            StringBuilder begin = new StringBuilder();
            StringBuilder end = new StringBuilder();
            int r;
            int i = 0;
            while((r = inputStream.read()) != -1)
            {
                if (i < from)
                {
                    begin.append((char) r);
                }
                else if (i >= to)
                {
                    end.append((char) r);
                }
                i++;
            }
            byte[] b = begin.toString().getBytes();
            byte[] e = end.toString().getBytes();
            inputStream.close();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(b);
            outputStream.write(insertedContent);
            outputStream.write(e);
            outputStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return node;
    }

    public boolean deleteDir(File file) {
        if (!file.exists())
            return false;
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        return file.delete();
    }

    @Override
    public boolean delete(Node node) {
        File file = new File(node.getPath().toString());
        if (!deleteDir(file))
            return false;
        /*
        if (node.isFile()){
            if (!file.exists())
                return false;

            boolean deleted = file.delete();
            if (!deleted)
                return false;
        }
        else {
            if (!deleteDir(file))
                return false;
        }
         */

        NodeClass castednode = (NodeClass) node;
        Node dad = castednode.getDad_();
        if (dad == null)
        {
            return true;
        }
        return dad.getChildren().remove(node);
    }

    @Override
    public Node create(Node folder, String name, Node.Type type) {
        //TODO: good exceptions
        if (folder.isFile())
            throw new RuntimeException();
        File newfile = new File(folder.getPath().toString() +"/" + name);

        Node new_node = new NodeClass(Path.of(folder.getPath().toString() +"/" + name),(Node.Types)type,folder);

        folder.getChildren().add(new_node);

        if (type == Node.Types.FILE)
        {
            try {
               newfile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
        {
            try {
                Files.createDirectories(Path.of(folder.getPath().toString() +"/" + name));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new_node;

    }

    @Override
    public Node move(Node nodeToMove, Node destinationFolder) {
        NodeClass castednode = (NodeClass) nodeToMove;
        Node dad = castednode.getDad_();
        if (dad ==null)
            return nodeToMove;
        dad.getChildren().remove(nodeToMove);
        destinationFolder.getChildren().add(nodeToMove);


        try {
            Files.move(nodeToMove.getPath(),Path.of(destinationFolder.getPath().toString()+"/" + nodeToMove.getPath().getFileName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ((NodeClass) nodeToMove).set_parent(destinationFolder);
        ((NodeClass) nodeToMove).set_path(Path.of(destinationFolder.getPath().toString()+"/" + nodeToMove.getPath().getFileName()));
        return nodeToMove;
    }
}
