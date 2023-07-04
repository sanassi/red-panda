package com.example.demo.myide.domain.entity.GitFeatures;

import com.example.demo.myide.domain.entity.Feature;
import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.Project;
import com.example.demo.myide.domain.entity.Report.BadReport;
import com.example.demo.myide.domain.entity.Report.GoodReport;
import com.example.demo.myide.domain.entity.aspect.OurGit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Commit extends OurGit implements Feature {
    @Override
    //fuction gitadd
    public ExecutionReport execute(Project project, Object... params) {

        /*try {
            File root =  project.getRootNode().getPath().toFile();
            ProcessBuilder processBuilder = new ProcessBuilder();
            List<String> files = new ArrayList<String>();
            files.add("git");
            files.add("commit");
            for (Object param : params)
            {
                if (param.getClass() == String.class)
                {
                    files.add((String) param);
                }
            }
            if (processBuilder.directory(root).command(files).start().waitFor() == 0)
                return new GoodReport();
            return new BadReport();
        } catch (Exception e) {
            return new BadReport();
        }*/

        Repository repo = null;
        try {
            repo = new FileRepositoryBuilder().setGitDir(new File(project.getRootNode().getPath().toFile() + "/.git")).readEnvironment().setMustExist(true).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Git git = new Git(repo);

        try {
            int i = 0;
            for (Object param : params) {
                if (param.getClass() == String.class) {
                    git.commit().setMessage((String) param).call();
                }
            i++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        return new GoodReport(null);
    }


    @Override
    public Feature.Type type() {
        return Mandatory.Features.Git.COMMIT;

    }
}
