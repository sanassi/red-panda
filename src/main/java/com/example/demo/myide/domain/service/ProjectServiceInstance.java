package com.example.demo.myide.domain.service;

import com.example.demo.MyIde;
import com.example.demo.myide.domain.entity.Feature;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.entity.ProjectClass;
import com.example.demo.myide.domain.entity.Report.BadReport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public enum ProjectServiceInstance implements ProjectService {
    INSTANCE;
    @Override
    public Project load(Path root) {
        project_ = new ProjectClass(root);
        Path indexFile = Path.of(".pandaIndex");
        Path tmpFolder = Path.of(".tmp");
        try {
            Files.createDirectory(indexFile);
            Files.createDirectory(tmpFolder);
        } catch (IOException e) {
            System.err.println("[INFO] load: failed to create folders (expected)");
        }
        MyIde.Configuration config = new MyIde.Configuration(indexFile, tmpFolder);
        MyIde.init(config);
        return project_;
    }

    @Override
    public Feature.ExecutionReport execute(Project project, Feature.Type featureType, Object... params) {
        if (project.getFeature(featureType).isPresent()) {
            return project.getFeature(featureType).get().execute(project,params);
        } else
            return new BadReport();
    }


    @Override
    public NodeService getNodeService() {
        return NodeServiceInstance.INSTANCE;
    }

    private Project project_;
}
