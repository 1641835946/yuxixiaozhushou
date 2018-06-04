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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SaveCallback;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.StorageStudent;
import com.liangjie.yuxixiaozhushou.adapter.MainAdapter;
import com.liangjie.yuxixiaozhushou.utils.ToastUtils;
import com.liangjie.yuxixiaozhushou.waterdroplistview.WaterDropListView;
import com.yalantis.contextmenu.lib.MenuObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/5/6.
 */
public class MainActivity extends ListViewMenuActivity {

    //commit()-->0-->classnNameList()-->3-->onRefresh()
    // 先查找StudentTable中的ObjectId,在ClassroomAndStudent中查找classroom的列表
    private List<AVObject> classContent = new ArrayList<>();
    private List<AVObject> data = new ArrayList<>();

    private AVObject dataItem = new AVObject("ClassroomAndStudent");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkSignUp();
        initView(R.layout.activity_main);

        // 保存 installation 到服务器
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                AVInstallation.getCurrentInstallation().saveInBackground();
            }
        });
        //makedir:yuxixiaozhushou
        isFolderExists(Environment.getExternalStorageDirectory() + "/yuxixiaozhushou");
        //listview第一条
        dataItem.put("classroom_name","");
        waterDropListView = (WaterDropListView) findViewById(R.id.waterdrop_listview);
//        initWaterDrop();
    }

    private void checkSignUp() {
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            // 跳转到首页
        } else {
            //缓存用户对象为空时，可打开用户注册界面
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            Log.e("梁洁checksignup", "mainactivity");
        }
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initWaterDrop(){
        waterDropListView.needFooter(getData().size(), new MainAdapter(this, R.layout.item_main, getData()));
        waterDropListView.setWaterDropListViewListener(this);
        waterDropListView.setPullLoadEnable(true);
        waterDropListView.setOnItemClickListener(this);
    }

    @Override
    protected List<AVObject> getData(){
        data.remove(dataItem);
        if (data.size()==0) {
            data.add(dataItem);
        }
        return data;
    }
    protected void onResume() {
        super.onResume();
        commit();//onrefresh();
    }
    @Override
    public void onRefresh() {
//        classnNameList();并无刷新数据的功能，classnNameList()才有，classnNameList更新数据后会调用onRefresh()
// 。各个activity中刷新不同，甚至不能刷新
        initWaterDrop();
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
    AVObject studentObj = null;
    @Override
    protected void commit() {
        //加入了哪些班级
        Log.e("梁洁", "resume commit");
        if (AVUser.getCurrentUser() == null) {
            Log.e("梁洁1", "null");
        } else {
            if (StorageStudent.getInstance().getStudent() == null || !StorageStudent.getInstance().getStudent().get("user_name").toString().equals(AVUser.getCurrentUser().getUsername())) {
                AVQuery<AVObject> query = new AVQuery<>("StudentTable");
                query.whereStartsWith("user_object_id", AVUser.getCurrentUser().getObjectId());
                query.getFirstInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        // object 就是符合条件的第一个 AVObject
                        studentObj = avObject;
                        Log.e("梁洁commit", studentObj.toString());
                        StorageStudent.getInstance().setStudent(studentObj);
                        classnNameList();
                    }
                });
            } else {
                studentObj = StorageStudent.getInstance().getStudent();
                classnNameList();
            }
        }
    }

    private void classnNameList() {
        new Thread(){
            @Override
            public void run(){
                //你要执行的方法
                // 执行完毕后给handler发送一个空消息
                AVQuery query = new AVQuery("ClassroomAndStudent");// 选课表对象
                // 设置关联
                query.whereEqualTo("student_pointer", studentObj);
                Log.e("梁洁classnNameList",studentObj.toString());
                query.orderByDescending("createdAt");
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            classContent = new ArrayList<AVObject>();
                            classContent = list;
                            data = classContent;
                            int size = list.size();
                            Log.e("梁洁size", ""+size);
                            for (AVObject avObject : list) {
                                //加载每个班级的icon
                                size--;
                                loadClassroomIcon(avObject, size);
                            }
                        } else {
                            Log.e("梁洁1212", e.toString());
                        }
                    }
                });
            }
        }.start();
    }

    private void loadClassroomIcon(AVObject avObject, int size) {
        final int sizeFinal=size;
        final String url = avObject.get("classroom_icon_url").toString();
        final String iconName = avObject.get("classroom_name").toString();
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
                int listSize = data.size();
                AVObject avObjectTmp = data.get(listSize-sizeFinal-1);
                avObjectTmp.put("local_uri", uri.toString());
                Log.e("梁洁local_uri", avObjectTmp.get("local_uri").toString());
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
                if (sizeFinal == 0) {
                    Log.e("梁洁", "sizefinal is 0");
                    handler.sendEmptyMessage(3);
                }
                // bytes 就是文件的数据流

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
                    onRefresh();
                    break;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //MainActivity-->ClassroomActivity
        Log.e("梁洁", "onitemclick");
        try {
            String serializedString = classContent.get(position-1).toString();//position-1
            Intent gotoClassIntent = new Intent(this, ClassroomActivity.class);
            gotoClassIntent.putExtra("class_info", serializedString);
            startActivity(gotoClassIntent);
        } catch (IndexOutOfBoundsException e) {
            Log.e("梁洁exception", "onItemClick");
            ToastUtils.makeShortText("出现异常", this);
        }
    }

    @Override
    protected List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();
        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        MenuObject send = new MenuObject("创建班级");
        send.setResource(R.drawable.establish);

        MenuObject like = new MenuObject("加入班级");
        like.setResource(R.drawable.search);

        MenuObject addFr = new MenuObject("退出登录");
        addFr.setResource(R.drawable.logup);

        MenuObject addFav = new MenuObject("重置密码");
        addFav.setResource(R.drawable.reset);

//        MenuObject addFa = new MenuObject("所有公告");
//        addFa.setResource(R.drawable.notice);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
//        menuObjects.add(addFa);
        return menuObjects;
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {
            case 1:
                //创建班级
                Intent establishIntent = new Intent(this, EstablishActivity.class);
                startActivity(establishIntent);
                break;
            case 2:
                //加入班级:先查找班级
                // todo
                Intent joinIntent = new Intent(this, SearchActivity.class);
                startActivity(joinIntent);
                break;
            case 3:
                //退出登录
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("确定退出登录？");
                dialog.setCancelable(false);
                dialog.setPositiveButton(R.string.submit_confirm_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AVUser.getCurrentUser().logOut();
                        Intent logupIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(logupIntent);
                        finish();
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
//                checkSignUp();
                break;
            case 4:
                //邮箱重置密码
                AlertDialog.Builder resetDialog = new AlertDialog.Builder(MainActivity.this);
                resetDialog.setTitle("提示");
                resetDialog.setMessage("确定重置密码？");
                resetDialog.setCancelable(false);
                resetDialog.setPositiveButton(R.string.submit_confirm_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AVUser.requestPasswordResetInBackground(AVUser.getCurrentUser().getEmail(),
                                new RequestPasswordResetCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            AVUser.getCurrentUser().logOut();
                                            ToastUtils.makeLongText("请检查邮件", MainActivity.this);
                                            Intent logupIntent = new Intent(MainActivity.this, LoginActivity.class);
                                            startActivity(logupIntent);
                                            finish();
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
//                Intent resetIntent = new Intent(this, ForgetPasswordActivity.class);
//                startActivity(resetIntent);
                    }
                });
                resetDialog.setNegativeButton("取消", null);
                resetDialog.show();
                break;
        }
    }

    boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);
        if (!file.exists()) {
            if (file.mkdir()) {
                return true;}
            else
                return false;
        }
        return true;
    }
}
