package com.liangjie.yuxixiaozhushou.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/14.
 */
public class JudgeProblem implements Serializable{

    private String problem;
    private String yes = "正确";//1
    private String no = "错误";//2区别没有做
    private int standardAnswer;
    private int answer;

    public JudgeProblem(String problem, int standardAnswer) {
        this.problem = problem;
        this.standardAnswer = standardAnswer;
    }

    public JudgeProblem(int answer) {
        this.answer = answer;
    }

    public JudgeProblem() {

    }

    public boolean isRight(){
        if (standardAnswer == answer) {
            return true;
        } else {
            return false;
        }
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public void setYes(String yes) {
        this.yes = yes;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setStandardAnswer(int standardAnswer) {
        this.standardAnswer = standardAnswer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public String getProblem() {
        return problem;
    }

    public String getYes() {
        return yes;
    }

    public String getNo() {
        return no;
    }

    public int getStandardAnswer() {
        return standardAnswer;
    }

    public int getAnswer() {
        return answer;
    }
}
