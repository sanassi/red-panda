package com.example.demo.myide.domain.entity.GitFeatures;

import com.example.demo.myide.domain.entity.Feature;
import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.entity.Report.BadReport;
import com.example.demo.myide.domain.entity.Report.GoodReport;
import com.example.demo.myide.domain.entity.aspect.OurGit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.util.Arrays.asList;

public class Add extends OurGit implements Feature {

    @Override
    //function gitadd
    public Feature.ExecutionReport execute(Project project, Object... params) {
        Repository repo = null;
        try {
            repo = new FileRepositoryBuilder().setGitDir(new File(project.getRootNode().getPath().toFile()+"/.git")).readEnvironment().setMustExist(true).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Git git = new Git(repo);

        if (project.getRootNode() == null)
            return new BadReport();
        try {
            for (Object param : params) {
                if (param.getClass() == String.class) {
                    File file = new File(project.getRootNode().getPath().toString().concat("/").concat((String) param));
                    if (!file.exists())
                        return new BadReport();
                }
            }
            for (Object param : params) {
                    git.add().addFilepattern((String) param).call();
                }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new GoodReport();
/*
            try {
                File root =  project.getRootNode().getPath().toFile();
                ProcessBuilder processBuilder = new ProcessBuilder();
                List<String> files = new ArrayList<String>();
                files.add("git");
                files.add("add");
                for (Object param : params)
                {
                    if (param.getClass() == String.class)
                    {
                        File file = new File(project.getRootNode().getPath().toString().concat("/").concat((String) param));
                        if (!file.exists())
                            return new BadReport();
                        files.add((String) param);
                    }
                }
                if (processBuilder.directory(root).command(files).start().waitFor() == 0)
                    return new GoodReport();
                return new BadReport();
            } catch (Exception e) {
                return new BadReport();
            }*/
    }

    @Override
    public Feature.Type type() {
        return Mandatory.Features.Git.ADD;
    }

}
