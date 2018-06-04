package com.liangjie.yuxixiaozhushou.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.adapter.PushAdapter;
import com.liangjie.yuxixiaozhushou.adapter.ReplyAdapter;
import com.liangjie.yuxixiaozhushou.model.Notice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */
public class PushActivity extends BaseActivity {
    /**
     * Called when the activity is first created.
     */
    private EditText noticeEt;
    private Button commitBtn;
    private ListView listView;
    private AVObject deserializedObject;
    private AVObject classObjId;
    private ArrayList<Notice> noticeList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_push);

        Intent intent = getIntent();
        String serializedString = intent.getStringExtra("c_s_obj_str");
        try {
            deserializedObject = AVObject.parseAVObject(serializedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        classObjId = (AVObject) deserializedObject.get("classroom_pointer");
        noticeEt = (EditText) findViewById(R.id.et_comment);
        commitBtn = (Button) findViewById(R.id.btn_commit_comment);
        noticeList = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("NoticeTable");
        query.whereStartsWith("classroom_name", deserializedObject.get("classroom_name").toString());
        query.orderByDescending("createdAt");
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

        final Context context = this;

        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!noticeEt.getText().toString().trim().equals("")) {
                    AVQuery pushQuery = AVInstallation.getQuery();
                    pushQuery.whereEqualTo("channels", classObjId.getObjectId());
                    AVPush push = new AVPush();
                    push.setQuery(pushQuery);
                    push.setMessage(noticeEt.getText().toString().trim());
                    push.setPushToAndroid(true);
                    push.sendInBackground(new SendCallback() {
                        @Override
                        public void done(AVException e) {
                            Toast toast = null;
                            if (e == null) {
                                toast = Toast.makeText(context, "Send successfully.", Toast.LENGTH_SHORT);
/*********************************************NoticeTable数据库表*************************************************************/
                                AVObject todoFolder = new AVObject("NoticeTable");// 构建对象
                                todoFolder.put("classroom_name", deserializedObject.get("classroom_name").toString());
                                todoFolder.put("notice_content", noticeEt.getText().toString().trim());
                                todoFolder.saveInBackground();// 保存到服务端
                                Notice notice = new Notice();
                                notice.setTime(null);
                                notice.setContent(noticeEt.getText().toString().trim());
//                                if (noticeList.size()>0) {
                                    noticeList.add(0,notice);
//                                    noticeList.set(0, notice);
//                                }else {
//                                    noticeList.add(notice);
//                                }
                                refresh();
                                noticeEt.setText("");
                            } else {
                                toast =
                                        Toast.makeText(context, "Send fails with :" + e.getMessage(), Toast.LENGTH_LONG);
                            }
                            // 放心大胆地show，我们保证 callback 运行在 UI 线程。
                            toast.show();
                        }
                    });
                }
            }
        });
    }

    private List<Notice> getData() {
        return noticeList;
    }
    private void refresh() {
        listView = (ListView) findViewById(R.id.listview);
        ListAdapter adapter = new PushAdapter(PushActivity.this, R.layout.item_push, getData());
        listView.setAdapter(adapter);
    }
}
