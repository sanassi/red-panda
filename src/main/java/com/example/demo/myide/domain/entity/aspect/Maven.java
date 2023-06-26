package com.example.demo.myide.domain.entity.aspect;

import com.example.demo.myide.domain.entity.Aspect;
import com.example.demo.myide.domain.entity.Feature;
import com.example.demo.myide.domain.entity.Mandatory;
import com.example.demo.myide.domain.entity.MavenFeatures.*;
import com.example.demo.myide.domain.entity.MavenFeatures.Package;

import java.util.ArrayList;
import java.util.List;

public class Maven implements Aspect {
    @Override
    public Mandatory.Aspects getType() {
        return Mandatory.Aspects.MAVEN;
    }
     public List<Feature> getFeatureList() {
        List<Feature> result = new ArrayList<Feature>();
        result.add(new Clean());
        result.add(new Compile());
        result.add(new Exec());
        result.add(new Install());
        result.add(new Package());
        result.add(new Test());
        result.add(new Tree());
        return result;
    }
}
