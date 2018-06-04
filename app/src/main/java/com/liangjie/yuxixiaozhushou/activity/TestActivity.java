package com.liangjie.yuxixiaozhushou.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.Toast;

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
import com.bigkoo.alertview.AlertView;
import com.liangjie.yuxixiaozhushou.JSONUtil;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.Sandwich.SandwichActivity;
import com.liangjie.yuxixiaozhushou.adapter.LearningAdapter;
import com.liangjie.yuxixiaozhushou.model.PaperTemplate;
import com.liangjie.yuxixiaozhushou.model.SingleFixedChoice;
import com.liangjie.yuxixiaozhushou.model.TestList;
import com.liangjie.yuxixiaozhushou.waterdroplistview.WaterDropListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by Administrator on 2017/5/14.
 */
public class TestActivity extends BaseActivity implements WaterDropListView.IWaterDropListViewListener,
        AdapterView.OnItemClickListener, View.OnClickListener{
    //WaterDropListView
    protected WaterDropListView waterDropListView;

    private ArrayList<PaperTemplate> paperList;
    private ArrayList<PaperTemplate> dataList;
    private String classId;
    private AVObject classroomObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_learning);

        Intent intent = getIntent();
        classId = intent.getStringExtra("class_info");

        waterDropListView = (WaterDropListView) findViewById(R.id.waterdrop_listview);

        initData();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        //paper的名字
        alertShowExt();
    }

    @Override
    public void onItemClick(Object o, int position) {
        closeKeyboard();
        //判断是否是拓展窗口View，而且点击的是非取消按钮
        if(o == mAlertViewExt && position != AlertView.CANCELPOSITION){
            String name = etName.getText().toString();
            if(name.isEmpty()){
                Toast.makeText(this, "啥都没填呢", Toast.LENGTH_SHORT).show();
            }
            else{
                Intent aPaperIntent = new Intent(TestActivity.this, APaperActivity.class);
                aPaperIntent.putExtra("class_info", classId);
                aPaperIntent.putExtra("class_name", etName.getText().toString().trim());
                aPaperIntent.putExtra("test_list", classroomObj.toString());
                startActivity(aPaperIntent);
                etName.setText("");
                //todo 刷新
                initData();
            }
            return;
        }
    }

    PaperTemplate firstPaper = new PaperTemplate("");
    //从LearningTable中加载资料列表
    private void initData() {
//        paperList = new ArrayList<PaperTemplate>();
        AVQuery<AVObject> avQuery = new AVQuery<>("ClassroomTable");
        avQuery.getInBackground(classId, new GetCallback<AVObject>() {
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
                    dataList = paperList;
                    Log.e("梁洁datalist size", ""+dataList.size());
                }
                initWaterDrop();
            }
        });
    }

    //“listview”的firstItem点击无效
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PaperTemplate paperTemplate = dataList.get(position - 1);
        if (paperTemplate.getName().equals("")){

        } else {
            //todo 老师: 查看试卷
            Intent aPaperIntent = new Intent(TestActivity.this, APaperActivity.class);
            aPaperIntent.putExtra("class_info", classId);
            aPaperIntent.putExtra("class_name", etName.getText().toString().trim());
            aPaperIntent.putExtra("paper", dataList.get(position-1));
            startActivity(aPaperIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    protected Handler handler =new Handler(){
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //处理UI
            switch (msg.what){
                case 1:
                    waterDropListView.stopRefresh();
                    break;
                case 2:
                    waterDropListView.stopLoadMore();
                    break;
                default:
            }
        }
    };

    private List<String> getData() {
        List<String> strList = new ArrayList<>();
        for (int i = 0; i<dataList.size(); i++) {
            PaperTemplate paperTemplate = dataList.get(i);
            String str = paperTemplate.getName();
            strList.add(str);
        }
        return strList;
    }

    protected void initWaterDrop(){
        Log.e("梁洁resume", "initwaterdrop");
        waterDropListView.needFooter(getData().size(), new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getData()));
        waterDropListView.setWaterDropListViewListener(this);
        waterDropListView.setPullLoadEnable(true);
        waterDropListView.setOnItemClickListener(this);
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

    @Override
    public void onRefresh() {
        initData();
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

}


