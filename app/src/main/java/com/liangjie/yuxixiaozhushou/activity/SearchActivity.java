package com.liangjie.yuxixiaozhushou.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.liangjie.yuxixiaozhushou.CleanEditText;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.adapter.MainAdapter;
import com.liangjie.yuxixiaozhushou.waterdroplistview.WaterDropListView;

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
 * Created by Administrator on 2017/5/7.
 */
public class SearchActivity extends BaseActivity implements WaterDropListView.IWaterDropListViewListener,
        AdapterView.OnItemClickListener,View.OnClickListener{
    private CleanEditText keywordEdit;
    private Button searchBtn;
    private List<AVObject> list = new ArrayList<>();
    protected WaterDropListView waterDropListView;
    private AVObject dataItem = new AVObject("ClassroomTable");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_join);
        initViews();
        dataItem.put("classroom_name","");
        commit();
    }

    private void initViews() {
        waterDropListView = (WaterDropListView) findViewById(R.id.waterdrop_listview);
        searchBtn = getView(R.id.btn_search);
        searchBtn.setOnClickListener(this);
        keywordEdit = getView(R.id.et_keyword);
        keywordEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        keywordEdit.setImeOptions(EditorInfo.IME_ACTION_GO);
        keywordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // 点击虚拟键盘的done
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    commit();//case R.id.btn_search:保持一致
                }
                return false;
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                commit();//保持一致
                break;
            default:
                break;
        }
    }

    protected void commit() {
        final AVQuery<AVObject> query = new AVQuery<>("ClassroomTable");
        final String keyword = keywordEdit.getText().toString().trim();
//        query.whereContains("classroom_name", keyword);
        new Thread(){
            @Override
            public void run(){
                //你要执行的方法
                // 执行完毕后给handler发送一个空消息
                if (TextUtils.isEmpty(keyword)) {
                    //全部列出
                    query.whereContains("classroom_name", "");
                } else {
                    query.whereContains("classroom_name", keyword);
                }
                try {
                    list.remove(dataItem);
                    list = query.find();
                    Log.e("梁洁list", list.toString());
                    Log.e("梁洁list", ""+list.size());
                } catch (AVException e) {
                    e.printStackTrace();
                }
                if (list.size() > 0) {
                    loadIconList();
                } else {
                    list.add(dataItem);
                    handler.sendEmptyMessage(3);
                }

            }
        }.start();
    }

    private void loadIcon(int size) {
        final int sizeFinal=size;
        final String url = list.get(size).get("classroom_icon_url").toString();
        final String iconName = list.get(size).get("classroom_name").toString();
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
                AVObject avObjectTmp = list.get(sizeFinal);
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
                if (sizeFinal >= list.size()-1) {
                    handler.sendEmptyMessage(3);
                }
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                // 下载进度数据，integer 介于 0 和 100。
            }
        });
    }
    private void loadIconList(){
        for (int i = 0; i<list.size();i++) {
            loadIcon(i);
        }
    }

    protected List<AVObject> getData(){
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
//        data.remove(dataItem);
//        if (data.size()==0) {
//            data.add(dataItem);
//        }
//        Log.e("梁洁", "1data.size is " + data.size() +data.toString());
//        return data;
    }
    @Override
    public void onRefresh() {
        waterDropListView.needFooter(getData().size(), new MainAdapter(this, R.layout.item_main, getData()));
//        waterDropListView.setEmptyView(findViewById(R.id.empty_view));
        waterDropListView.setWaterDropListViewListener(this);
        waterDropListView.setPullLoadEnable(true);
        waterDropListView.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //todo 班级信息
        AVObject todoFolder = list.get(position-1);
        Log.e("梁洁", todoFolder.toString());
        String serializedString = todoFolder.toString();
        Intent classInfoIntent = new Intent(this, ClassInfoActivity.class);
        classInfoIntent.putExtra("class_info", serializedString);
        startActivityForResult(classInfoIntent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    finish();
                }
                break;
            default:
        }
    }

    protected Handler handler =new Handler(){
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //处理UI
            switch (msg.what){
                case 0:

                    break;
                case 1:
                    waterDropListView.stopRefresh();
                    break;
                case 2:
                    waterDropListView.stopLoadMore();
                    break;
                case 3:
                    onRefresh();
                    //这里的onrefresh()与原作者的不同，所以调用，通常是不需要调用的。
                    break;
                default:
            }
        }
    };

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
