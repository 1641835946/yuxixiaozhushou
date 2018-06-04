package com.liangjie.yuxixiaozhushou;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;

/**
 * Created by Administrator on 2017/5/2.
 */
public class MyLeanCloudApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"Tluhsh1VtkggEaX85aXq6Xz9-gzGzoHsz","MG5JpiBM8ILagrzsXAzUJBJj");
        AVOSCloud.setDebugLogEnabled(true);
    }
}
