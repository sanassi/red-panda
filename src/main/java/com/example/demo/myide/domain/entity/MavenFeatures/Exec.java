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
import java.util.ArrayList;
import java.util.List;

public class Exec extends Maven implements Feature {
    @Override
    public ExecutionReport execute(Project project, Object... params) {
        File root =  project.getRootNode().getPath().toFile();
        List<String> files = new ArrayList<String>();
        files.add("mvn");
        files.add("exec:java");

        for (Object param : params)
        {
            if (param.getClass() == String.class)
            {
                files.add((String) param);
            }
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(files);
            int r = processBuilder.directory(root).start().waitFor();
            if (r == 1)
                return new BadReport();
            return new GoodReport(null);
        } catch (Exception e) {
            return new BadReport();
        }
    }

    @Override
    public Feature.Type type() {
        return Mandatory.Features.Maven.EXEC;
    }
}
