package com.liangjie.yuxixiaozhushou.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/14.
 */
public class MultipleFixedChoice implements Serializable{
    private String problem;
    private String a;//1
    private String b;//2
    private String c;//4
    private String d;//8
    private int standardAnswer;
    private int answer;

    public MultipleFixedChoice(String problem, String a, String b, String c, String d, int standardAnswer) {
        this.problem = problem;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.standardAnswer = standardAnswer;
    }

    public MultipleFixedChoice(int answer) {
        this.answer = answer;
    }

    public MultipleFixedChoice() {

    }

    public boolean isRight(){
        if (standardAnswer == answer) {
            return true;
        } else {
            return false;
        }
    }

    public int getStandardAnswer() {
        return standardAnswer;
    }

    public void setStandardAnswer(int standardAnswer) {
        this.standardAnswer = standardAnswer;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public void setA(String a) {
        this.a = a;
    }

    public void setB(String b) {
        this.b = b;
    }

    public void setC(String c) {
        this.c = c;
    }

    public void setD(String d) {
        this.d = d;
    }

    public int getAnswer() {
        return answer;
    }

    public String getA() {
        return a;
    }

    public String getC() {
        return c;
    }

    public String getB() {
        return b;
    }

    public String getD() {
        return d;
    }

    public String getProblem() {
        return problem;
    }
}
