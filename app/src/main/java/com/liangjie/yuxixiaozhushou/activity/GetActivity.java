package com.liangjie.yuxixiaozhushou.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SendCallback;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.adapter.PushAdapter;
import com.liangjie.yuxixiaozhushou.model.AllNotice;
import com.liangjie.yuxixiaozhushou.model.Notice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/19.
 */
public class GetActivity extends BaseActivity {
    /**
     * Called when the activity is first created.
     */
    private ListView listView;
    private AVObject deserializedObject;
    private AVObject classObjId;
    private ArrayList<Notice> noticeList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_get);

        Intent intent = getIntent();
        String serializedString = intent.getStringExtra("c_s_obj_str");
        try {
            deserializedObject = AVObject.parseAVObject(serializedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        classObjId = (AVObject) deserializedObject.get("classroom_pointer");
        noticeList = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("NoticeTable");
        query.whereStartsWith("classroom_name", deserializedObject.get("classroom_name").toString());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e==null && list.size()>0) {
                    for (AVObject city : list) {
                        if (city.get("classroom_name").toString().equals(deserializedObject.get("classroom_name").toString())) {
                            Notice notice = new Notice();
                            notice.setTime((Date)city.get("createdAt"));
                            notice.setContent(city.get("notice_content").toString());
                            noticeList.add(notice);
                        }
                    }
                    refresh();
                }
            }
        });
    }
    private void refresh() {
        listView = (ListView) findViewById(R.id.listview);
        ListAdapter adapter = new PushAdapter(GetActivity.this, R.layout.item_push, noticeList);
        listView.setAdapter(adapter);
    }
}

