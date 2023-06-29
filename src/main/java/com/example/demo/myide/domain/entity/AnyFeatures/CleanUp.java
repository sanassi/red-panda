package com.example.demo.myide.domain.entity.AnyFeatures;

import com.example.demo.myide.domain.entity.*;
import com.example.demo.myide.domain.entity.Report.BadReport;
import com.example.demo.myide.domain.entity.Report.GoodReport;
import com.example.demo.myide.domain.entity.aspect.Any;
import com.example.demo.myide.domain.service.NodeServiceInstance;
import javafx.util.Pair;

import java.io.*;
import java.nio.file.Path;

public class CleanUp extends Any implements Feature {
    // Move to NodeServiceInstance

    @Override
    //fuction gitadd
    public ExecutionReport execute(Project project, Object... params) {
        // check if .myide doesn't exist
        //
        try {
            File ignorePath = new File(project.getRootNode().getPath().resolve(".myideignore").toString());
            if (!ignorePath.exists())
                return new BadReport();

            FileReader fileReader = new FileReader(ignorePath);
            BufferedReader reader = new BufferedReader(fileReader);

            String line = reader.readLine();
            var children = project.getRootNode().getChildren();

            while (line != null) {
                // TODO delete file using its name I guess
                File toDelete = new File(/*project.getRootNode().getPath().toString() + "/" + line*/
                        project.getRootNode().getPath().resolve(line).toString());

                // find the node

                Pair<Boolean, Node> pair = null;
                for (Node node : children)
                {
                    pair = Node.FindNode(node, toDelete.toPath());
                    if (pair.getKey())
                        break;
                }

                if (pair != null && pair.getValue() != null) {
                    //System.out.println(((NodeClass) pair.getValue()).getDad_().getChildren().size());
                    NodeServiceInstance.INSTANCE.delete(pair.getValue());
                    //System.out.println(((NodeClass) pair.getValue()).getDad_().getChildren().size());
                }

                line = reader.readLine();
            }

            reader.close();
            fileReader.close();

        } catch (Exception e) {
            System.out.println(e);
            return new BadReport();
        }

        return new GoodReport();
    }

    @Override
    public Feature.Type type() {
        return Mandatory.Features.Any.CLEANUP;

    }

}
