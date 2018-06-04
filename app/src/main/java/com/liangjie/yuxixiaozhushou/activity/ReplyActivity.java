package com.liangjie.yuxixiaozhushou.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.TimeUtil;
import com.liangjie.yuxixiaozhushou.adapter.ReplyAdapter;
import com.liangjie.yuxixiaozhushou.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/12.
 */
public class ReplyActivity extends BaseActivity implements AdapterView.OnItemLongClickListener {


    //初始化：initData()
    //点击添加，上传并刷新界面：initData()
    private String serializedString;
    private AVObject deserializedObject;//CommentTable
    private TextView commentName;
    private TextView commentContent;
    private TextView commentTime;
    private int size;
    private String commentIdStr;
    private AVObject commentId;
    private ImageView ivIcon;
    private AVObject cSDeserializedObject;
    private String csDeserializedStr;
    private String cSObjectIdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_reply);
        Intent intent = getIntent();
        //CommentTable
        serializedString = intent.getStringExtra("class_info");
        try {
            deserializedObject = AVObject.parseAVObject(serializedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //ClassroomAndStudent
        csDeserializedStr = intent.getStringExtra("classroom_and_student");
        try {
            cSDeserializedObject = AVObject.parseAVObject(csDeserializedStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cSObjectIdStr = cSDeserializedObject.getObjectId();
        //Comment
        commentName=getView(R.id.tv_name);
        commentContent=getView(R.id.tv_comment);
        commentTime=getView(R.id.tv_time);
        String commentNameStr = deserializedObject.get("nickname").toString();
        commentName.setText(commentNameStr);
        commentContent.setText(deserializedObject.get("comment_content").toString());
        Date date = (Date)deserializedObject.get("createdAt");
        commentTime.setText(TimeUtil.getMyTime(date));
        ivIcon = getView(R.id.iv_icon);
        String uriStr = intent.getStringExtra("local_uri");
        ivIcon.setImageURI(Uri.parse(uriStr));
        commentId = (AVObject) deserializedObject.get("comment");
        commentIdStr = deserializedObject.getObjectId();
        initData();

        final EditText commentEt = (EditText)findViewById(R.id.et_comment);
        Button commitBtn = (Button)findViewById(R.id.btn_commit_comment);
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传并刷新界面
                if (!commentEt.getText().toString().trim().equals("")) {
                    Log.e("梁洁deserializedObject", commentIdStr);
//**********************************数据库表：ReplyTable******************************************
                    AVObject comment = AVObject.createWithoutData("CommentTable",
                            commentIdStr);
                    Log.e("comment", comment.toString());
                    AVObject reply = new AVObject("ReplyTable");// 东莞
//                reply.put("nickname", deserializedObject.get("comment_name"));
//                reply.put("user_icon_url", deserializedObject.get("user_icon_url").toString());
                    reply.put("reply_class_stu_id", cSDeserializedObject.getObjectId());
                    reply.put("reply_content", commentEt.getText());
                    reply.put("reply", comment);// 为东莞设置 dependent 属性为广东
                    reply.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                // 广州被保存成功
                                Log.e("梁洁", "successfully");
                                //  刷新
                                initData();
                                commentEt.setText("");
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    ToastUtils.makeShortText("请输入内容", ReplyActivity.this);
                }

            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final int positionF = position;
        Log.e("梁洁reply", "position:"+position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(ReplyActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("确定删除？");
        dialog.setCancelable(false);
        dialog.setPositiveButton(R.string.submit_confirm_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AVObject avObject = avObjectList.get(positionF);
                Log.e("reply_id", avObject.get("reply_class_stu_id").toString());
                Log.e("梁洁csObjectIdStr", cSObjectIdStr);
                if (avObject.get("reply_class_stu_id").toString().equals(cSObjectIdStr)) {
                    avObjectList.remove(avObject);
                    avObject.deleteInBackground();
                    initListView();
                } else {
                    ToastUtils.makeLongText("失败，无权限删除他人的回复", ReplyActivity.this);
                }
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
        return true;
    }

    private List<AVObject> avObjectList = new ArrayList<>();
    private void initData() {
        avObjectList = new ArrayList<AVObject>();
        //first listview
        final AVObject replyFirst = new AVObject("ReplyTable");
//        replyFirst.put("nickname", "");
        replyFirst.put("reply_content", "");
//        learningRes.put("createdAt", "");不是String是Date
        AVObject comment = AVObject.createWithoutData("CommentTable", commentIdStr);
        AVQuery<AVObject> query = new AVQuery<>("ReplyTable");
        query.orderByDescending("createdAt");
        query.whereEqualTo("reply", comment);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e==null) {
                    for (AVObject city : list) {
                        // list 的结果为广东省下辖的所有城市
                        if (e == null) {
                            if (list.size() != 0){
                                avObjectList=list;
                                initNameIcon();
//                                for (int i = 0; i<avObjectList.size(); i++) {
//                                    loadClassroomIcon(avObjectList.get(i), i);
//                                }
                            }
                        }
                        if (avObjectList.size()==0){
                            avObjectList.add(replyFirst);
                        }
                    }
                }else {
                    Log.e("梁洁e", e.toString());
                }

            }
        });
    }
    private void initNameIcon() {
        for (int i = 0; i<avObjectList.size(); i++) {
            final int iNum = i;
            String csId = avObjectList.get(i).get("reply_class_stu_id").toString();
            AVQuery<AVObject> query = new AVQuery<>("ClassroomAndStudent");
            query.getInBackground(csId, new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    // object 就是 id 为 558e20cbe4b060308e3eb36c 的 Todo 对象实例
                    if (e==null) {
                        //commenttable没有nickname以及usericon,MainActivity等中也没有local_uri
                        String nickname = avObject.get("nickname").toString();
                        avObjectList.get(iNum).put("nickname", nickname);
                        String userIconUri = avObject.get("user_icon_url").toString();
                        avObjectList.get(iNum).put("user_icon_url", userIconUri);
                        loadClassroomIcon(avObjectList.get(iNum), iNum);
                    }
                }
            });
        }
    }
    private void loadClassroomIcon(AVObject avObject, int size) {
        final int sizeFinal=size;
        final String url = avObject.get("user_icon_url").toString();
        final String iconName = avObject.get("nickname").toString();
        final AVFile file = new AVFile(iconName,
                url, new HashMap<String, Object>());
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                if (e == null) {
                    Log.d("saved", "文件大小" + bytes.length);
                } else {
                    Log.d("saved", "出错了" + e.getMessage());
                }
                Log.e("梁洁", "done");
                //下载后文件的存储路径：Environment.getExternalStorageDirectory() + "/"+resName
                File downloadedFile = new File(Environment.getExternalStorageDirectory() + "/yuxixiaozhushou/"+iconName);
                Uri uri = Uri.fromFile(downloadedFile);
                int listSize = avObjectList.size();
                AVObject avObjectTmp = avObjectList.get(sizeFinal);
                avObjectTmp.put("local_uri", uri.toString());
                Log.e("梁洁localUri", avObjectTmp.get("local_uri").toString());
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(downloadedFile);
                    fout.write(bytes);
                    Log.d("saved", "文件写入成功.");
                    fout.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                    Log.d("saved", "文件找不到.." + e1.getMessage());
                } catch (IOException e1) {
                    Log.d("saved", "文件读取异常.");
                }
                // bytes 就是文件的数据流
                if (sizeFinal == listSize-1) {
                    Log.e("梁洁", "sizefinal is 0");
                    initListView();
                }
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                // 下载进度数据，integer 介于 0 和 100。
            }
        });
    }

    private void initListView(){
        ListAdapter adapter = new ReplyAdapter(ReplyActivity.this, R.layout.item_classroom, avObjectList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(ReplyActivity.this);
    }

}
