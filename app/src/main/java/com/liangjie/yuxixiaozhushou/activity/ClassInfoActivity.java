package com.liangjie.yuxixiaozhushou.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.liangjie.yuxixiaozhushou.R;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/5/8.
 */
public class ClassInfoActivity extends BaseActivity implements View.OnClickListener{

    private Button joinBtn;
    private TextView classRoomName;
    private TextView classRoomCreater;
    private TextView classRoomInfo;
    private ImageView classRoomIcon;
    private TextView memberTv;
    AVObject deserializedObject = null;//ClassroomTable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_class_info);

        memberTv =getView(R.id.member);
        joinBtn = getView(R.id.btn_join);
        joinBtn.setOnClickListener(this);
        classRoomName = getView(R.id.tv_class_name_content);
        classRoomCreater = getView(R.id.tv_class_creater_content);
        classRoomInfo = getView(R.id.tv_class_info_content);
        classRoomIcon = getView(R.id.iv_class_icon);

        Intent intent = getIntent();
        String serializedString = intent.getStringExtra("class_info");
        try {
            deserializedObject = AVObject.parseAVObject(serializedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AVQuery<AVObject> query = new AVQuery<>("ClassroomAndStudent");
        query.whereEqualTo("classroom_name", deserializedObject.get("classroom_name").toString());
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    // 查询成功，输出计数
                    Log.d("梁洁", "今天完成了" + i + "条待办事项。");
                    memberTv.setText(i+"人");
                } else {
                    // 查询失败
                }
            }
        });
        String iconURLStr = (String)deserializedObject.get("local_uri");
        Log.e("梁洁", "icon url is " +iconURLStr );
        classRoomIcon.setImageURI(Uri.parse(iconURLStr));
        classRoomName.setText((String)deserializedObject.get("classroom_name"));
        classRoomInfo.setText((String)deserializedObject.get("classroom_info"));
        classRoomCreater.setText(deserializedObject.get("creater_name").toString());
    }
    public Bitmap getBmp(byte[] in) {
        Bitmap bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);
        return bmpout;
    }

    AVObject serializedObj = null;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_join:
                //已经加入不能再加入
                //学生表与班级表：多对多
                new Thread(){
                    @Override
                    public void run(){
                        //你要执行的方法
                        // 执行完毕后给handler发送一个空消息
                        // 先查找StudentTable中属于用户的ObjectId
                        AVQuery<AVObject> query = new AVQuery<>("StudentTable");
                        query.whereStartsWith("user_object_id", AVUser.getCurrentUser().getObjectId());
                        query.getFirstInBackground(new GetCallback<AVObject>() {
                            @Override
                            public void done(AVObject avObject, AVException e) {
                                // object 就是符合条件的第一个 AVObject
                                serializedObj = avObject;
                                handler.sendEmptyMessage(0);
                            }
                        });
                    }
                }.start();
                break;
            default:
                break;
        }
    }

    private void check() {
        new Thread(){
            @Override
            public void run(){
                AVQuery<AVObject> query1 = new AVQuery<>("ClassroomAndStudent");
                query1.whereEqualTo("student_pointer", serializedObj);
                Log.e("梁洁", serializedObj.getObjectId());
                Log.e("梁洁", deserializedObject.getObjectId());
                query1.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        boolean has = false;
                        if (e == null) {
                            Log.e("梁洁1212", "not null");
                            for (AVObject avObject : list) {
                                AVObject classObj = (AVObject) avObject.get("classroom_pointer");
                                Log.e("梁洁1212classID", classObj.getObjectId());
                                Log.e("梁洁1212deseria", deserializedObject.getObjectId());
                                if (classObj.getObjectId().equals(deserializedObject.getObjectId())) {
                                    has = true;
                                    Log.e("梁洁1212", "has");
                                    break;
                                } else {
                                    Log.e(classObj.getObjectId(),deserializedObject.getObjectId());
                                }
                            }
                            if (has) {
                                handler.sendEmptyMessage(3);
                            } else {
                                handler.sendEmptyMessage(1);
                            }
                        } else {
                            Log.e("梁洁1212", e.toString());
                            handler.sendEmptyMessage(1);
                        }
                    }
                });
            }
        }.start();
    }
    private void classAndStudent() {
        new Thread(){
            @Override
            public void run(){
                //你要执行的方法
                // 执行完毕后给handler发送一个空消息
//***************************************数据库表：ClassroomAndStudent******************************
                AVObject studentCourseMapTom = new AVObject("ClassroomAndStudent");// 选课表对象
                // 设置关联
                studentCourseMapTom.put("student_pointer", serializedObj);//pointer
                studentCourseMapTom.put("classroom_pointer", deserializedObject);
                //***********************************************************************************
                studentCourseMapTom.put("nickname",serializedObj.get("user_name"));//string name
                studentCourseMapTom.put("user_icon_url", serializedObj.get("user_icon_url"));//todo 默认值
                studentCourseMapTom.put("classroom_icon_url", deserializedObject.get("classroom_icon_url"));
                studentCourseMapTom.put("classroom_name", deserializedObject.get("classroom_name"));//string name
                studentCourseMapTom.put("creater_name", deserializedObject.get("creater_name"));
                //***********************************************************************************
                // 保存选课表对象
                studentCourseMapTom.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e != null) {
                            Log.e("梁洁", e.toString());
                        } else {
                            Log.e("梁洁", "成功加入");
                            PushService.setDefaultPushCallback(ClassInfoActivity.this, PushActivity.class);
                            // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
                            PushService.subscribe(ClassInfoActivity.this, deserializedObject.getObjectId(), MainActivity.class);
                            handler.sendEmptyMessage(2);
                        }
                    }
                });
            }
        }.start();
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
                    check();//检查是否该同学已加入该班级
                    break;
                case 1:
                    classAndStudent();//未加入该班级时加入。
                    break;
                case 2:
                    Toast.makeText(ClassInfoActivity.this, "加入成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case 3:
                    Toast.makeText(ClassInfoActivity.this, "已加入该班级！", Toast.LENGTH_SHORT).show();
                    Intent backIntent = new Intent();
                    setResult(RESULT_OK, backIntent);
                    finish();
                    break;
                default:
            }
        }
    };

}
