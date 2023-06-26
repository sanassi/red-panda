package com.example.demo.myide.domain.entity.aspect;

import com.example.demo.myide.domain.entity.GitFeatures.Add;
import com.example.demo.myide.domain.entity.GitFeatures.Commit;
import com.example.demo.myide.domain.entity.GitFeatures.Pull;
import com.example.demo.myide.domain.entity.GitFeatures.Push;
import com.example.demo.myide.domain.entity.Aspect;
import com.example.demo.myide.domain.entity.Feature;
import com.example.demo.myide.domain.entity.Mandatory;

import java.util.ArrayList;
import java.util.List;

public class OurGit implements Aspect {
    @Override
    public Mandatory.Aspects getType() {
        return Mandatory.Aspects.GIT;
    }
    @Override
    public List<Feature> getFeatureList() {
        List<Feature> result = new ArrayList<Feature>();
        result.add(new Add());
        result.add(new Commit());
        result.add(new Push());
        result.add(new Pull());
        return result;
    }

}
