package com.liangjie.yuxixiaozhushou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.adapter.GetAllAdapter;
import com.liangjie.yuxixiaozhushou.adapter.PushAdapter;
import com.liangjie.yuxixiaozhushou.model.AllNotice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/19.
 */
public class GetAllActivity extends BaseActivity {
    /**
     * Called when the activity is first created.
     */
    private ListView listView;
    private ArrayList<AllNotice> noticeList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_get);

        noticeList = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("NoticeTable");
        query.whereStartsWith("classroom_name", "");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e==null && list.size()>0) {
                    for (AVObject city : list) {
                        AllNotice allNotice = new AllNotice();
                        allNotice.setTime(city.get("createdAt").toString());
                        allNotice.setContent(city.get("notice_content").toString());
                        allNotice.setName(city.get("classroom_name").toString());
                        noticeList.add(allNotice);
                    }
                    refresh();
                }
            }
        });
    }
    private void refresh() {
        listView = (ListView) findViewById(R.id.listview);
        ListAdapter adapter = new GetAllAdapter(GetAllActivity.this, R.layout.item_get_all, noticeList);
        listView.setAdapter(adapter);
    }
}

