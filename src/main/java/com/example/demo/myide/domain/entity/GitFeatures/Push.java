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
import  org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;

import javax.validation.constraints.AssertFalse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Push extends OurGit implements Feature {
    @Override
    public Feature.ExecutionReport execute(Project project, Object... params)
    {
        /*try {
            File root =  project.getRootNode().getPath().toFile();
            ProcessBuilder processBuilder = new ProcessBuilder();
            List<String> files = new ArrayList<String>();
            files.add("git");
            files.add("push");
            if (processBuilder.directory(root).command(files).start().waitFor() == 0)
                return new GoodReport();
            return new BadReport();
        } catch (Exception e) {
            return new BadReport();
        }*/
        Repository repo = null;
        try {
            repo = new FileRepositoryBuilder().setGitDir(new File(project.getRootNode().getPath().toFile()+"/.git")).readEnvironment().setMustExist(true).build();
        } catch (IOException e) {
            //System.out.println("error1");
            return new BadReport();
        }
        Git git = new Git(repo);
        try {
            Iterable<PushResult> results = git.push().call();
            for (PushResult result : results) {
                Collection<RemoteRefUpdate> remoteUpdates = result.getRemoteUpdates();

                for (RemoteRefUpdate remoteUpdate : remoteUpdates) {
                    if (remoteUpdate.getStatus() == RemoteRefUpdate.Status.UP_TO_DATE) {
                        //System.out.println("uptodate");
                        return new BadReport();
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println(e);
            return new BadReport();
        }
        return new GoodReport();
    }
    @Override
    public Feature.Type type() {
        return Mandatory.Features.Git.PUSH;
    }
}
