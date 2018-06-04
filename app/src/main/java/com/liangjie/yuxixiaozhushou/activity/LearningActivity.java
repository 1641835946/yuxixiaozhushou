package com.liangjie.yuxixiaozhushou.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/5/10.
 */

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.bigkoo.alertview.AlertView;
import com.liangjie.yuxixiaozhushou.JSONUtil;
import com.liangjie.yuxixiaozhushou.adapter.LearningAdapter;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.adapter.MainAdapter;
import com.liangjie.yuxixiaozhushou.utils.ToastUtils;
import com.liangjie.yuxixiaozhushou.waterdroplistview.WaterDropListView;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LearningActivity extends BaseActivity implements WaterDropListView.IWaterDropListViewListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    //WaterDropListView
    protected WaterDropListView waterDropListView;

    //(AVObject) deserializedObject.get("classroom_pointer");为了获取classroom的objectId
    private AVObject classObjId;
    //打开文件时调用应用的Intent
    private Intent urlIntent;
    //资源列表LearningTable，根据classroom_id查找
    private List<AVObject> avObjectList = new ArrayList<AVObject>();
    //intent传递过来反序列后的结果MainActivity-->ClassroomActivity-->LearningActivity
    private AVObject deserializedObject;
    //文件在云端存储的url,并保存在LearningTable中
    private String urlStr=null;
    //uriStr.substring(uriStr.lastIndexOf("/") + 1);去除路径，带后缀名的
    private String filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_learning);

        Intent intent = getIntent();
        String serializedString = intent.getStringExtra("class_info");
        try {
            deserializedObject = AVObject.parseAVObject(serializedString);
            Log.e("梁洁serializedString", serializedString);
            Log.e("梁洁deserializedObject", deserializedObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        classObjId = (AVObject) deserializedObject.get("classroom_pointer");
        waterDropListView = (WaterDropListView) findViewById(R.id.waterdrop_listview);

        //获取资源列表LearningTable，根据classroom_id查找。在创建adapter前要先从后端加载数据（并完成，注意多线程）。
        initData();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlIntent = new Intent(Intent.ACTION_GET_CONTENT);
                urlIntent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                urlIntent.addCategory(Intent.CATEGORY_OPENABLE); // 只有设置了这个，返回的uri才能使用 getContentResolver().openInputStream(uri) 打开。
                startActivityForResult(urlIntent, 1);
            }
        });
    }

    //从LearningTable中加载资料列表
    private void initData() {
        avObjectList = new ArrayList<AVObject>();
        final AVObject learningRes = new AVObject("LearningTable");
        learningRes.put("res_name", "");
        learningRes.put("upload_id", "");
        learningRes.put("classroom_id", "");
//        learningRes.put("createdAt", "");不是String是Date
        AVQuery<AVObject> avQuery = new AVQuery<>("LearningTable");
        avQuery.whereEqualTo("classroom_id", classObjId.getObjectId());
        avQuery.orderByDescending("createdAt");
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() != 0){
                        avObjectList=list;
                    }
                }
                if (avObjectList.size()==0){
                    avObjectList.add(learningRes);
                }
                initWaterDrop();
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (avObjectList.get(position-1).get("res_name").equals("")){

        } else {
            final int innerPos = position;
            AlertDialog.Builder dialog = new AlertDialog.Builder(LearningActivity.this);
            dialog.setTitle("提示");
            dialog.setMessage("确定要删除？");
            dialog.setCancelable(false);
            dialog.setPositiveButton(R.string.submit_confirm_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final AVObject avObject = avObjectList.get(innerPos-1);
                    if (avObject.get("upload_id").toString().equals(AVUser.getCurrentUser().getUsername()) ||
                            deserializedObject.get("creater_name").equals(AVUser.getCurrentUser().getUsername())) {
                        final String resName = avObject.get("res_name").toString();
                        final String url = avObject.get("url").toString();
                        avObject.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                ToastUtils.makeLongText("删除成功", LearningActivity.this);
                                initData();
                            }
                        });
                    } else {
                        ToastUtils.makeLongText("只有文件上传者或班级创建人可以删除文件！", LearningActivity.this);
                    }
                }
            });
            dialog.setNegativeButton("取消", null);
            dialog.show();
        }
        return true;
    }

    //点击下载资源并打开，“listview”的firstItem点击无效
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (avObjectList.get(position-1).get("res_name").equals("")){

        } else {
            final String resName = avObjectList.get(position-1).get("res_name").toString();
            final ProgressDialog progressDialog = new ProgressDialog(LearningActivity.this);
            progressDialog.setTitle(resName);
            progressDialog.setMessage("下载中......");
            progressDialog.show();
            final String url = avObjectList.get(position-1).get("url").toString();
            final AVFile file = new AVFile(resName, url, new HashMap<String, Object>());
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
                    File downloadedFile = new File(Environment.getExternalStorageDirectory() + "/yuxixiaozhushou/"+resName);
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
                    //关闭progressdialog，（必要）
                    progressDialog.dismiss();
                    //打开文件
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/yuxixiaozhushou/"+resName));
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                    startActivity(intent);
                }
            }, new ProgressCallback() {
                @Override
                public void done(Integer integer) {
                    // 下载进度数据，integer 介于 0 和 100。
                    Log.e("梁洁", "loading..."+integer);
                }
            });
        }
    }

    //上传文件
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && data != null) {
            String uriStr = data.getData().toString();
            Log.e("梁洁", uriStr);
//            uriStr.substring(uriStr.lastIndexOf("/") + 1);
            filename= Uri.decode(uriStr).substring(uriStr.lastIndexOf("/") + 1);
            Log.e("梁洁", filename);
            //****************************************************************************
            Log.e("梁洁lj", deserializedObject.get("classroom_pointer").toString());
            final ProgressDialog progressDialog = new ProgressDialog(LearningActivity.this);
            progressDialog.setTitle(filename);
            progressDialog.setMessage("上传中......");
            progressDialog.show();
            try {
                final AVFile file = AVFile.withAbsoluteLocalPath(
                        filename,
                        getRealFilePath(LearningActivity.this,data.getData()));
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        urlStr = file.getUrl();
                        progressDialog.dismiss();
                        Log.e("梁洁", "URLStr is " + urlStr);//返回一个唯一的 Url 地址
                        handler.sendEmptyMessage(0);
                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer integer) {
                        // 上传进度数据，integer 介于 0 和 100。
                        Log.e("梁洁", "i"+integer);
                    }
                });
            }catch (FileNotFoundException e) {
                Log.e("梁洁", "file not found");
            }
        }
    }
    //
    protected Handler handler =new Handler(){
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //处理UI
            switch (msg.what){
                case 0:
//                    AVObject classroom = AVObject.createWithoutData("ClassroomTable",
//                            classObjId.getObjectId());
//**************************************数据库表：LearningTable**************************************
                    AVObject learningRes = new AVObject("LearningTable");
                    learningRes.put("res_name", filename);
                    learningRes.put("upload_id", AVUser.getCurrentUser().getUsername());
                    learningRes.put("classroom_id", classObjId.getObjectId());
                    learningRes.put("url", urlStr);
                    //learningRes.put("dependent", classroom);
                    learningRes.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                // 保存成功
                                initData();
                            }
                        }
                    });
                    break;
                case 1:
                    waterDropListView.stopRefresh();
                    break;
                case 2:
                    waterDropListView.stopLoadMore();
                    break;
                case 3:
//                    Log.e("梁洁", "what =4");
//                    onRefresh();
                    break;
                default:
            }
        }
    };

    //uri to path
    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    @Override
    public void onRefresh() {
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

    protected void initWaterDrop(){
        waterDropListView.needFooter(avObjectList.size(), new LearningAdapter(this, R.layout.item_learning, avObjectList));
        waterDropListView.setWaterDropListViewListener(this);
        waterDropListView.setPullLoadEnable(true);
        waterDropListView.setOnItemClickListener(this);
        waterDropListView.setOnItemLongClickListener(this);
    }

    @Override
    public void onLoadMore() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(2000);
                    handler.sendEmptyMessage(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

