package com.example.demo.myide.domain.entity.Report;

import com.example.demo.myide.domain.entity.Feature;

public class BadReport implements Feature.ExecutionReport {

    @Override
    public boolean isSuccess() {
        return false;
    }
}