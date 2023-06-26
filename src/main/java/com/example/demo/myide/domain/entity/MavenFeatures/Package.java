package com.example.demo.myide.domain.entity.MavenFeatures;

import com.example.demo.myide.domain.entity.Aspect;
import com.example.demo.myide.domain.entity.Feature;
import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.entity.Report.BadReport;
import com.example.demo.myide.domain.entity.Report.GoodReport;
import com.example.demo.myide.domain.entity.aspect.Maven;

import java.io.File;
import java.io.IOException;

public class Package extends Maven implements Feature {
    @Override
    public ExecutionReport execute(Project project, Object... params) {
        File root =  project.getRootNode().getPath().toFile();
        ProcessBuilder processBuilder = new ProcessBuilder("mvn","package");
        try {
            processBuilder.directory(root).start().waitFor();
            return new GoodReport();
        } catch (Exception e) {
            return new BadReport();
        }
    }

    @Override
    public Feature.Type type() {
        return Mandatory.Features.Maven.PACKAGE;
    }
}
