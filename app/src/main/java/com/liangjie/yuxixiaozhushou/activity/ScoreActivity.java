package com.liangjie.yuxixiaozhushou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.liangjie.yuxixiaozhushou.JSONUtil;
import com.liangjie.yuxixiaozhushou.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tech.linjiang.suitlines.Unit;

/**
 * Created by Administrator on 2017/6/6.
 */
public class ScoreActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private ListView listView;
    private ArrayAdapter<String> scoreAdapter;
    private List<String> paperNameListStr = new ArrayList<>();
    private AVObject deserializedObject;
    private String classIdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_score);

        Intent intent = getIntent();
        classIdStr = intent.getStringExtra("class_info");

        final AVQuery<AVObject> priorityQuery = new AVQuery<>("ScoreTable");
        priorityQuery.whereStartsWith("classroom_id", classIdStr);
        final AVQuery<AVObject> statusQuery = new AVQuery<>("ScoreTable");
        statusQuery.whereStartsWith("student_id", AVUser.getCurrentUser().getObjectId());
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(priorityQuery, statusQuery));
        query.selectKeys(Arrays.asList("paper_name"));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e==null) {
                    paperNameListStr.add("个人历史成绩");
                    for (AVObject avObject : list) {
                        String paperName = avObject.getString("paper_name");
                        paperNameListStr.add(paperName);
                    }
                    scoreAdapter = new ArrayAdapter<String>(ScoreActivity.this,
                            android.R.layout.simple_list_item_1, paperNameListStr);
                    listView = (ListView) findViewById(R.id.listview);
                    listView.setAdapter(scoreAdapter);
                    listView.setOnItemClickListener(ScoreActivity.this);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("梁洁position", ""+position);
        Intent intent = new Intent(this, ScorePicActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("class_id", classIdStr);
        if (position != 0) {
            intent.putExtra("paper_name", paperNameListStr.get(position));
        }
        startActivity(intent);
    }
}

