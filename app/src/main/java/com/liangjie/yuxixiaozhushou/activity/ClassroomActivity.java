package com.liangjie.yuxixiaozhushou.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SaveCallback;
import com.liangjie.yuxixiaozhushou.JSONUtil;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.StorageStudent;
import com.liangjie.yuxixiaozhushou.adapter.ClassroomAdapter;
import com.liangjie.yuxixiaozhushou.adapter.MainAdapter;
import com.liangjie.yuxixiaozhushou.model.PaperTemplate;
import com.liangjie.yuxixiaozhushou.utils.ToastUtils;
import com.liangjie.yuxixiaozhushou.waterdroplistview.WaterDropListView;
import com.yalantis.contextmenu.lib.MenuObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/5/8.
 */
public class ClassroomActivity extends ListViewMenuActivity implements AdapterView.OnItemLongClickListener{

    //初始化：加载ClassroomTable-->CommentTable中的数据，而后initWaterDrop();
    //getDataList()-->if list.size()>0:-->0-->initNameIcon()-->loadClassroomIcon()-->initWaterDrop()
    //getDataList()-->if list.size()==0-->5-->initWaterDrop()
    //点击提交comment：commit()-->getDataList()-->......-->initWaterDrop()

    //intent传递过来反序列后的结果MainActivity-->ClassroomActivity
    private AVObject deserializedObject = new AVObject();//ClassroomAndStudent
    private String serializedString = null;
    //comment列表的数量
    private int size;
    //“listview”的firstItem
    private AVObject commentFirst;
    //(AVObject) deserializedObject.get("classroom_pointer");
    private AVObject classId;
    private String cSObjectIdStr;
    //classId.getObjectId();
    private String classIdStr;
    private EditText commentEt;
    private String createrIdStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_classroom);
        Intent intent = getIntent();
        serializedString = intent.getStringExtra("class_info");
        try {
            //ClassroomAndStudent
            deserializedObject = AVObject.parseAVObject(serializedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        classId = (AVObject) deserializedObject.get("classroom_pointer");
        cSObjectIdStr = deserializedObject.getObjectId();
        classIdStr = classId.getObjectId();
        createrIdStr = deserializedObject.get("creater_name").toString();

        //first listview
        commentFirst = new AVObject("CommentTable");
        commentFirst.put("comment_content", "");
//        commentFirst.put("createdAt", commentEt.getText());
//        commentFirst.put("comment_name", "");

        // 加载ClassroomTable-->CommentTable中的数据，而后handler.sendEmptyMessage(5)：initWaterDrop();
        getDataList();
        //添加Comment
        commentEt = (EditText)findViewById(R.id.et_comment);
        Button commitBtn = (Button)findViewById(R.id.btn_commit_comment);
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initWaterDrop(){
        waterDropListView = (WaterDropListView) findViewById(R.id.waterdrop_listview);
        waterDropListView.needFooter(size, new ClassroomAdapter(this, R.layout.item_classroom, getData()));
        waterDropListView.setWaterDropListViewListener(this);
        waterDropListView.setOnItemClickListener(this);
        waterDropListView.setOnItemLongClickListener(this);
        waterDropListView.setPullLoadEnable(true);
    }

    private void getDataList() {
        AVObject classroom = AVObject.createWithoutData("ClassroomTable",
                classIdStr);
        AVQuery<AVObject> query = new AVQuery<>("CommentTable");
        query.whereEqualTo("comment", classroom);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    dataList = list;
                    Log.e("梁洁list", list.toString());
                } else {
                    e.printStackTrace();
                }
                if (list.size()>0) {
                    handler.sendEmptyMessage(0);
                } else {
                    handler.sendEmptyMessage(5);
                }
            }
        });
    }
    private List<AVObject> dataList = new ArrayList<>();
    @Override
    protected List<AVObject> getData(){
        dataList.remove(commentFirst);
        if (dataList.size() == 0) {
            dataList.add(commentFirst);
        }
        size = dataList.size();
        return dataList;
    }

    @Override
    protected void commit() {
        //上传并刷新界面
        if (!TextUtils.isEmpty(commentEt.getText())){
            Log.e("梁洁deserializedObject", classIdStr);
//**********************************数据库表：CommentTable******************************************
            AVObject classroom = AVObject.createWithoutData("ClassroomTable",
                    classIdStr);
            Log.e("梁洁classroom", classroom.toString());
            AVObject comment = new AVObject("CommentTable");// 东莞
//            comment.put("comment_name", deserializedObject.get("nickname"));
            comment.put("comment_class_stu_id", cSObjectIdStr);
            comment.put("comment_content", commentEt.getText());
            comment.put("comment", classroom);// 为东莞设置 dependent 属性为广东
            comment.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        // 广州被保存成功
                        Log.e("梁洁", "successfully");
                        handler.sendEmptyMessage(3);
                        //  刷新
                        commentEt.setText("");
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final int positionF = position-1;
        Log.e("梁洁comment", "position:"+position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(ClassroomActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("确定删除？");
        dialog.setCancelable(false);
        dialog.setPositiveButton(R.string.submit_confirm_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AVObject avObject = dataList.get(positionF);
                Log.e("comment_id", avObject.get("comment_class_stu_id").toString());
                Log.e("梁洁csObjectIdStr", cSObjectIdStr);
                if (avObject.get("comment_class_stu_id").toString().equals(cSObjectIdStr)) {
                    dataList.remove(avObject);
                    avObject.deleteInBackground();
                    initWaterDrop();
                } else {
                    ToastUtils.makeLongText("失败，无权限删除他人的帖子", ClassroomActivity.this);
                }
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击后打开ReplyActivity，“listview”的firstItem点击无效
        String name = dataList.get(position-1).get("comment_content").toString();
        if (name.equals("")){

        } else {
            String serializedString = dataList.get(position-1).toString();
            Intent replyIntent = new Intent(this, ReplyActivity.class);
            replyIntent.putExtra("class_info", serializedString);
            replyIntent.putExtra("classroom_and_student", deserializedObject.toString());
            replyIntent.putExtra("local_uri", dataList.get(position-1).get("local_uri").toString());
            startActivity(replyIntent);
        }
    }

    @Override
    public void onRefresh() {
        waterDropListView.needFooter(size, new ClassroomAdapter(this, R.layout.item_classroom, getData()));
        waterDropListView.setWaterDropListViewListener(this);
        waterDropListView.setPullLoadEnable(true);
        waterDropListView.setOnItemClickListener(this);
        waterDropListView.setOnItemLongClickListener(this);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    handler.sendEmptyMessage(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void initNameIcon() {
        for (int i = 0; i<dataList.size(); i++) {
            final int iNum = i;
            String csId = dataList.get(i).get("comment_class_stu_id").toString();
            AVQuery<AVObject> query = new AVQuery<>("ClassroomAndStudent");
            query.getInBackground(csId, new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    // object 就是 id 为 558e20cbe4b060308e3eb36c 的 Todo 对象实例
                    if (e==null) {
                        //commenttable没有nickname以及usericon,MainActivity等中也没有local_uri
                        String nickname = avObject.get("nickname").toString();
                        dataList.get(iNum).put("nickname", nickname);
                        try {
                            String userIconUri = avObject.get("user_icon_url").toString();
                            dataList.get(iNum).put("user_icon_url", userIconUri);
                            loadClassroomIcon(dataList.get(iNum), iNum);
                        } catch (Exception exception) {
//                            initNameIcon();
                            Log.e("梁洁Exception", "initNameIcon");
                        }
                    }
                }
            });
//            query.selectKeys(Arrays.asList("nickname", "user_icon_url"));
//            query.getInBackground(csId, new GetCallback<AVObject>() {
//                @Override
//                public void done(AVObject avObject, AVException e) {
//                    if (e==null) {
//                        //commenttable没有nickname以及usericon,MainActivity等中也没有local_uri
//                        dataList.get(iNum).put("nickname", avObject.get("nickname").toString());
//                        dataList.get(iNum).put("user_icon_url", avObject.get("user_icon_url").toString());
//                    }
//                }
//            });
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
                int listSize = dataList.size();
                AVObject avObjectTmp = dataList.get(sizeFinal);
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
                    handler.sendEmptyMessage(5);
                }
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                // 下载进度数据，integer 介于 0 和 100。
            }
        });
    }

    //定义Handler对象
    protected Handler handler =new Handler(){
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //处理UI
            switch (msg.what){
                case 0:
                    Log.e("梁洁", "what =0");
                    initNameIcon();
                    break;
                //在加载数据完毕，你需要调用上面俩个方法手工停止。
                case 1:
                    Log.e("梁洁", "what =1");
                    waterDropListView.stopRefresh();
                    break;
                case 2:
                    Log.e("梁洁", "what =2");
                    waterDropListView.stopLoadMore();
                    break;
                case 3:
                    Log.e("梁洁", "what =4");
                    getDataList();
                    break;
                case 4:
                    Intent testIntent = new Intent(ClassroomActivity.this, AnswerActivity.class);
                    testIntent.putExtra("class_info", classIdStr);
                    testIntent.putExtra("c_s_obj_id", cSObjectIdStr);
                    testIntent.putExtra("class_name", etName.getText().toString().trim());
                    testIntent.putExtra("test_list", classroomObj.toString());
                    startActivity(testIntent);
                    break;
                case 5:
                    initWaterDrop();
            }
        }
    };
    @Override
    protected List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();
        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        MenuObject notice = new MenuObject("公告栏");
        notice.setResource(R.drawable.notice);
        MenuObject study = new MenuObject("学习");
        study.setResource(R.drawable.study);
        MenuObject test = new MenuObject("出卷");
        test.setResource(R.drawable.paper);
        MenuObject answer = new MenuObject("答卷");
        answer.setResource(R.drawable.edit);
        MenuObject info = new MenuObject("个人资料");
        info.setResource(R.drawable.studentinfo);
        MenuObject score = new MenuObject("成绩");
        score.setResource(R.drawable.score);
//        MenuObject left = new MenuObject("退出班级");
//todo.deleteInBackground();和退订
        menuObjects.add(close);
        menuObjects.add(notice);
        menuObjects.add(study);
        menuObjects.add(test);
        menuObjects.add(answer);
        menuObjects.add(info);
        menuObjects.add(score);

        return menuObjects;
    }

    AVObject classroomObj = new AVObject("ClassroomTable");
    List<PaperTemplate> paperList = new ArrayList<>();
    PaperTemplate firstPaper = new PaperTemplate("");
    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {
            case 1:
                //老师比学生应多一个发送控件
                if (AVUser.getCurrentUser().getUsername().equals(deserializedObject.get("creater_name"))){
                    Intent pushIntent = new Intent(ClassroomActivity.this, PushActivity.class);
                    pushIntent.putExtra("c_s_obj_str", serializedString);
                    startActivity(pushIntent);
                } else {
                    Intent getIntent = new Intent(ClassroomActivity.this, GetActivity.class);
                    getIntent.putExtra("c_s_obj_str", serializedString);
                    startActivity(getIntent);
                }
                break;
            case 2:
                //班级
                Intent learnIntent = new Intent(this, LearningActivity.class);
                learnIntent.putExtra("class_info", serializedString);
                startActivity(learnIntent);
                break;
            case 3:
                Intent testIntent = new Intent(this, TestActivity.class);
                testIntent.putExtra("class_info", classIdStr);
                startActivity(testIntent);
                break;
            case 4:
                AVQuery<AVObject> avQuery = new AVQuery<>("ClassroomTable");
                avQuery.getInBackground(classIdStr, new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        if (e == null) {
                            classroomObj = avObject;
                            List<String> stringList = (List<String>) avObject.getList("test_list");
                            paperList = new ArrayList<PaperTemplate>();
                            if (stringList == null) {
                                paperList.add(firstPaper);
                            }else if (stringList.size() == 0){
                                paperList.add(firstPaper);
                            } else {
                                // string to papertemplate
                                for (int i = 0; i<stringList.size(); i++){
                                    paperList.add(JSONUtil.parseJSONWithJSONObject(stringList.get(i)));
                                }
                            }
                        }
                        handler.sendEmptyMessage(4);
                    }
                });
                break;
            case 5:
                Intent infoIntent = new Intent(ClassroomActivity.this, InfoActivity.class);
                infoIntent.putExtra("c_s_obj_str", deserializedObject.toString());
                startActivityForResult(infoIntent, 1);
                break;
            case 6:
                Intent scoreIntent = new Intent(this, ScoreActivity.class);
                scoreIntent.putExtra("class_info", classIdStr);
                startActivity(scoreIntent);
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    String returnedData = data.getStringExtra("return_class_stu");
                    try {
                        //ClassroomAndStudent
                        AVObject avObject = AVObject.parseAVObject(returnedData);
                        if (avObject.get("nickname").equals(null)) {
                            Log.e("梁洁nickname", "nickname is null");
                            avObject.put("nickname", deserializedObject.get("nickname"));
                        }
                        if (avObject.get("user_icon_url").equals(null)) {
                            avObject.put("user_icon_url", deserializedObject.get("user_icon_url"));
                        }
                        deserializedObject = avObject;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                getDataList();
                break;
            default:
        }
    }
}


