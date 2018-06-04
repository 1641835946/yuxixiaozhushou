package com.liangjie.yuxixiaozhushou.model;

import java.util.Date;

/**
 * Created by Administrator on 2017/5/19.
 */
public class Notice {

    private String content;
    private Date time;

    public Notice() {
    }

    public String getContent() {
        return content;
    }

    public Date getTime() {
        return time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
