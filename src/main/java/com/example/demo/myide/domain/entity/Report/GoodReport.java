package com.example.demo.myide.domain.entity.Report;

import com.example.demo.myide.domain.entity.Feature;

public class GoodReport<T> implements Feature.ExecutionReport {

    @Override
    public boolean isSuccess() {
        return true;
    }

    public GoodReport(T data) {
        this.data = data;
    }

    private T data = null;
    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}