package com.liangjie.yuxixiaozhushou.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 */
public class TestList implements Serializable{

    private List<String> testList;

    public List<String> getTestList() {
        return testList;
    }

    public void setTestList(List<String> testList) {
        this.testList = testList;
    }

    public TestList(List<String> testList) {
        this.testList = testList;

    }
}
