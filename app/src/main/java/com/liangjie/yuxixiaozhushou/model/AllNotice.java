package com.liangjie.yuxixiaozhushou.model;

/**
 * Created by Administrator on 2017/5/19.
 */
public class AllNotice {

    private String time;
    private String name;
    private String content;

    public AllNotice() {
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
