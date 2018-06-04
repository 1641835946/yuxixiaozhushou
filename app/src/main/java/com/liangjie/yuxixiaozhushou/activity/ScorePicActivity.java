package com.liangjie.yuxixiaozhushou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.liangjie.yuxixiaozhushou.R;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tech.linjiang.suitlines.SuitLines;
import tech.linjiang.suitlines.Unit;

/**
 * Created by Administrator on 2017/6/6.
 */
public class ScorePicActivity extends BaseActivity {

    private SuitLines suitLines;
    private String classIdStr;
    private String paperName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_scorepic);
        suitLines = (SuitLines) getView(R.id.suitlines);
        suitLines.setLineForm(true);
        suitLines.setDefaultOneLineColor(R.color.review_green);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        classIdStr = intent.getStringExtra("class_id");
        if (position == 0) {
            //个人历史成绩
            final AVQuery<AVObject> priorityQuery = new AVQuery<>("ScoreTable");
            priorityQuery.whereStartsWith("classroom_id", classIdStr);
            final AVQuery<AVObject> statusQuery = new AVQuery<>("ScoreTable");
            statusQuery.whereStartsWith("student_id", AVUser.getCurrentUser().getObjectId());
            AVQuery<AVObject> query = AVQuery.and(Arrays.asList(priorityQuery, statusQuery));
            query.selectKeys(Arrays.asList("score","paper_name"));
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e==null) {
                        List<Unit> lines = new ArrayList<>();
                        for (AVObject avObject : list) {
                            int score = avObject.getNumber("score").intValue();
                            String paperName = avObject.getString("paper_name");
                            lines.add(new Unit(score, paperName));
                        }
                        suitLines.feedWithAnim(lines);
                    }
                }
            });
        } else {
            //班级某卷成绩
            paperName = intent.getStringExtra("paper_name");
            final AVQuery<AVObject> priorityQuery = new AVQuery<>("ScoreTable");
            priorityQuery.whereStartsWith("classroom_id", classIdStr);
            final AVQuery<AVObject> statusQuery = new AVQuery<>("ScoreTable");
            statusQuery.whereStartsWith("paper_name", paperName);
            AVQuery<AVObject> query = AVQuery.and(Arrays.asList(priorityQuery, statusQuery));
            query.selectKeys(Arrays.asList("score","student_name"));
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e==null) {
                        List<Unit> lines = new ArrayList<>();
                        for (AVObject avObject : list) {
                            int score = avObject.getNumber("score").intValue();
                            String studentName = avObject.getString("student_name");
                            lines.add(new Unit(score, studentName));
                        }
                        suitLines.feedWithAnim(lines);
                    }
                }
            });
        }
//        final AVQuery<AVObject> priorityQuery = new AVQuery<>("ScoreTable");
//        priorityQuery.whereStartsWith("classroom_id", classIdStr);
//        final AVQuery<AVObject> statusQuery = new AVQuery<>("ScoreTable");
//        statusQuery.whereStartsWith("student_id", AVUser.getCurrentUser().getObjectId());
//        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(priorityQuery, statusQuery));
//        query.selectKeys(Arrays.asList("paper_name"));
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                if (e==null) {
//                    for (AVObject avObject : list) {
//                        String paperName = avObject.getString("paper_name");
//                        paperNameListStr.add(paperName);
//                    }
//                    scoreAdapter = new ArrayAdapter<String>(ScoreActivity.this,
//                            android.R.layout.simple_list_item_1, paperNameListStr);
//                    listView = (ListView) findViewById(R.id.listview);
//                    listView.setAdapter(scoreAdapter);
//                    listView.setOnItemClickListener(ScoreActivity.this);
//                }
//            }
//        });
//        List<Unit> lines = new ArrayList<>();
//        for (int i = 0; i < 14; i++) {
//            lines.add(new Unit(new SecureRandom().nextInt(48), i + ""));
//        }
//        suitLines.feedWithAnim(lines);
    }
}
