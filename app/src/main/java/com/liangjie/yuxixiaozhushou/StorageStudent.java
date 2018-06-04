package com.liangjie.yuxixiaozhushou;

import android.util.Log;

import com.avos.avoscloud.AVObject;

/**
 * Created by Administrator on 2017/5/13.
 */

public class StorageStudent {

    private AVObject student;

    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static StorageStudent instance = null;

    /* 私有构造方法，防止被实例化 */
    private StorageStudent() {
    }

    /*3.双重锁定:只在第一次初始化的时候加上同步锁*/
    public static StorageStudent getInstance() {
        if (instance == null) {
            synchronized (StorageStudent.class) {
                if (instance == null) {
                    instance = new StorageStudent();
                }
            }
        }
        return instance;
    }

    public AVObject getStudent() {
        return student;
    }

    public void setStudent(AVObject student) {
        this.student = student;
    }
}
