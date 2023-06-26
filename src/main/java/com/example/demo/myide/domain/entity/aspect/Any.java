package com.example.demo.myide.domain.entity.aspect;

import com.example.demo.myide.domain.entity.AnyFeatures.CleanUp;
import com.example.demo.myide.domain.entity.AnyFeatures.Dist;
import com.example.demo.myide.domain.entity.AnyFeatures.Search;
import com.example.demo.myide.domain.entity.Aspect;
import com.example.demo.myide.domain.entity.Feature;
import com.example.demo.myide.domain.entity.Mandatory;

import java.util.ArrayList;
import java.util.List;

public class Any implements Aspect {
    @Override
    public Mandatory.Aspects getType() {
        return Mandatory.Aspects.ANY;
    }

    @Override
    public List<Feature> getFeatureList() {
        List<Feature> result = new ArrayList<Feature>();
        result.add(new CleanUp());
        result.add(new Dist());
        result.add(new Search());
        return result;
    }

}
