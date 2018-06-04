package com.liangjie.yuxixiaozhushou.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/14.
 */
public class PaperTemplate implements Serializable{

    private String name;
    private int count;
    private int rightCount;
    private List<SingleFixedChoice> sList;
    private List<MultipleFixedChoice> mList;
    private List<JudgeProblem> jList;

    public PaperTemplate(String name) {
        this.name = name;
        sList = new ArrayList<>();
        mList = new ArrayList<>();
        jList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "paper name is "+ name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setRightCount(int rightCount) {
        this.rightCount = rightCount;
    }

    public void setsList(List<SingleFixedChoice> sList) {
        this.sList = sList;
    }

    public void setjList(List<JudgeProblem> jList) {
        this.jList = jList;
    }

    public void setmList(List<MultipleFixedChoice> mList) {
        this.mList = mList;
    }

    public int getCountNum() {
        if (sList!=null) {
            count = sList.size();
            Log.e("梁洁count", ""+count);
        }
        if (mList!=null) {
            count = count + mList.size();
        }
        if (jList!=null) {
            count = count + jList.size();
        }
        return count;
    }
    public int getCount() {
        return count;
    }

    public int getRightCountNum() {
        rightCount = 0;
        for (int i = 0; i<sList.size(); i++){
            Log.e("梁洁slist", sList.get(i).toString());
            if(sList.get(i).isRight()){
                rightCount++;
            }
        }
        for (int i = 0; i<mList.size(); i++){
            if(mList.get(i).isRight()){
                rightCount++;
            }
        }
        for (int i = 0; i<jList.size(); i++){
            if(jList.get(i).isRight()){
                rightCount++;
            }
        }
        return rightCount;
    }

    public int getRightCount() {
        return rightCount;
    }

    public List<SingleFixedChoice> getsList() {
        return sList;
    }

    public List<MultipleFixedChoice> getmList() {
        return mList;
    }

    public List<JudgeProblem> getjList() {
        return jList;
    }
}
