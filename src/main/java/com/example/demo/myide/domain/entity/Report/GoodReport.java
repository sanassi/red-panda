package com.example.demo.myide.domain.entity.Report;

import com.example.demo.myide.domain.entity.Feature;

public class GoodReport implements Feature.ExecutionReport {

    @Override
    public boolean isSuccess() {
        return true;
    }
}