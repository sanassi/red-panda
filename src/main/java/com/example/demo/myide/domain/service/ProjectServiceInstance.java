package com.example.demo.myide.domain.service;

import com.example.demo.myide.domain.entity.Feature;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.entity.ProjectClass;
import com.example.demo.myide.domain.entity.Report.BadReport;

import java.io.File;
import java.nio.file.Path;

public enum ProjectServiceInstance implements ProjectService {
    INSTANCE;
    @Override
    public Project load(Path root) {
        project_ = new ProjectClass(root);
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
