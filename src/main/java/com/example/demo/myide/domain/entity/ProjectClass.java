package com.example.demo.myide.domain.entity;

import com.example.demo.myide.domain.entity.GitFeatures.Pull;
import com.example.demo.myide.domain.entity.GitFeatures.Push;
import com.example.demo.myide.domain.entity.GitFeatures.Commit;
import com.example.demo.myide.domain.entity.GitFeatures.Add;
import com.example.demo.myide.domain.entity.AnyFeatures.CleanUp;
import com.example.demo.myide.domain.entity.AnyFeatures.Dist;
import com.example.demo.myide.domain.entity.AnyFeatures.Search;
import com.example.demo.myide.domain.entity.MavenFeatures.*;
import com.example.demo.myide.domain.entity.MavenFeatures.Package;
import com.example.demo.myide.domain.entity.aspect.Any;
import com.example.demo.myide.domain.entity.aspect.Maven;
import com.example.demo.myide.domain.entity.aspect.OurGit;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class ProjectClass implements Project {

    public ProjectClass(Path path)
    {
        File file = new File(path.toString());
        if (file.isDirectory()) {
            this.racine_ = new NodeClass(path, Node.Types.FOLDER,null);
        }
        else {
            this.racine_ = new NodeClass(path, Node.Types.FILE, null);
        }
    }

    @Override
    public Node getRootNode() {
        return racine_;
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
    public Set<Aspect> getAspects() {
        File racine_folder = new File(racine_.getPath().toString());
        Set<Aspect> result = new HashSet<>();
        result.add(new Any());
        if (!racine_folder.isDirectory())
            return result;
        for (File file : racine_folder.listFiles())
        {
            if (file.isDirectory() && file.getName().equals(".git"))
                result.add(new OurGit());
            else if (!file.isDirectory() && file.getName().equals("pom.xml"))
                result.add(new Maven());
        }
        return result;
    }

    @Override
    public Optional<Feature> getFeature(Feature.Type featureType) {
        List<Feature>features = getFeatures();
        for (Feature feature: features)
        {
            if (feature.type().equals(featureType))
                return Optional.of(feature);
        }
        return Optional.empty();
    }

    @Override
    public List<@NotNull Feature> getFeatures() {
        Set<Aspect>aspects = getAspects();
        List<Feature> features = new ArrayList<Feature>();
        features.add(new CleanUp());
        features.add(new Dist());
        features.add(new Search());
        for(Aspect el : aspects) {
            if (el instanceof OurGit)
            {
                features.add(new Push());
                features.add(new Pull());
                features.add(new Add());
                features.add(new Commit());

            }
            if (el instanceof Maven)
            {
                features.add(new Clean());
                features.add(new Compile());
                features.add(new Exec());
                features.add(new Install());
                features.add(new Package());
                features.add(new Test());
                features.add(new Tree());
            }
        }
        return features;
    }

    private Node racine_;
}
