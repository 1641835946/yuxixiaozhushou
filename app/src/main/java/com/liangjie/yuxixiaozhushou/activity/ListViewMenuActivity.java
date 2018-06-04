package com.liangjie.yuxixiaozhushou.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Toast;

import com.avos.avoscloud.AVObject;
import com.liangjie.yuxixiaozhushou.R;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import com.liangjie.yuxixiaozhushou.waterdroplistview.WaterDropListView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ListViewMenuActivity extends BaseActivity implements
        OnMenuItemClickListener, OnMenuItemLongClickListener, WaterDropListView.IWaterDropListViewListener,
        AdapterView.OnItemClickListener{
    //WaterDropListView
    protected WaterDropListView waterDropListView;
    //Context-Menu.Android
    protected FragmentManager fragmentManager;
    protected ContextMenuDialogFragment mMenuDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initView(int id) {
        //BaseActivity中加载AlertView
        super.initView(id);

        fragmentManager = getSupportFragmentManager();
        initMenuFragment();
    }

    //***********************Context-Menu.Android**************************************
    protected abstract List<MenuObject> getMenuObjects();
    public abstract void onMenuItemClick(View clickedView, int position);

//    @Override
//    public abstract void onItemClick(Object o, int position);

    protected void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }
    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded()) {
            mMenuDialogFragment.dismiss();
        } else {
            finish();
        }
    }
    //*******************************WaterDropListView*******************************************
    protected abstract void initWaterDrop();
    protected abstract List<AVObject> getData();
    protected abstract void commit();
    public abstract void onRefresh();
    @Override
    protected void onResume() {
        super.onResume();
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

    //定义Handler对象
    protected Handler handler =new Handler(){
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //处理UI
            switch (msg.what){
//                case 0:
//                    Log.e("梁洁", "what =0");
//                    onRefresh();
//                    break;
                //在加载数据完毕，你需要调用上面俩个方法手工停止。
                case 1:
                    Log.e("梁洁", "what =1");
                    waterDropListView.stopRefresh();
                    break;
                case 2:
                    Log.e("梁洁", "what =2");
                    waterDropListView.stopLoadMore();
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.context_menu) {
            if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}