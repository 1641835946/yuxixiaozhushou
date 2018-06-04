package com.liangjie.yuxixiaozhushou.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.bigkoo.alertview.OnItemClickListener;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.waterdroplistview.WaterDropListView;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/5/8.
 */
public class BaseActivity extends AppCompatActivity implements OnItemClickListener, OnDismissListener{
//没用上，但也不敢删了，一动不如一静：OnItemClickListener，OnDismissListener
    //Android-AlertView
    protected AlertView mAlertView;//避免创建重复View，先创建View，然后需要的时候show出来，推荐这个做法
    protected AlertView mAlertViewExt;//窗口拓展例子
    protected EditText etName;//拓展View内容
    protected InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initView(int id) {
        Log.e("梁洁", "initView");
        setContentView(id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mAlertView = new AlertView("标题", "内容", "取消", new String[]{"确定"}, null, this, AlertView.Style.Alert, this).setCancelable(true).setOnDismissListener(this);
        //拓展窗口
        mAlertViewExt = new AlertView("添加试卷", "请输入试卷名称！", "取消", null, new String[]{"完成"}, this, AlertView.Style.Alert, this);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.alertext_form,null);
        etName = (EditText) extView.findViewById(R.id.etName);
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                //输入框出来则往上移动
                boolean isOpen=imm.isActive();
                mAlertViewExt.setMarginBottom(isOpen&&focus ? 120 :0);
                System.out.println(isOpen);
            }
        });
        mAlertViewExt.addExtView(extView);
    }

    public final <E extends View> E getView(int id) {
        try {
            return (E) findViewById(id);
        } catch (ClassCastException ex) {
            Log.e("梁洁", "Could not cast View to concrete class.", ex);
            throw ex;
        }
    }

    //****************************Android-AlertView************************************************
//扩展窗口的使用：调用alertShowExt()并实现onItemClick(Object o,int position)
    @Override
    public void onDismiss(Object o) {
        closeKeyboard();
//        Toast.makeText(this, "消失了", Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(mAlertView!=null && mAlertView.isShowing()){
                mAlertView.dismiss();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    public void alertShowExt() {
        mAlertViewExt.show();
    }
    protected void closeKeyboard() {
        //关闭软键盘
        imm.hideSoftInputFromWindow(etName.getWindowToken(),0);
        //恢复位置
        mAlertViewExt.setMarginBottom(0);
    }
    @Override
    public void onItemClick(Object o,int position) {
        closeKeyboard();
        //判断是否是拓展窗口View，而且点击的是非取消按钮
        if(o == mAlertViewExt && position != AlertView.CANCELPOSITION){
            String name = etName.getText().toString();
            if(name.isEmpty()){
                Toast.makeText(this, "啥都没填呢", Toast.LENGTH_SHORT).show();
            }
            else{
                //查询etName.getText
                etName.setText("");
            }
            return;
        }
    }

    public void alertShow1() {
        mAlertView.show();
    }
    public void alertShow2(View view) {
        new AlertView("标题", "内容", null, new String[]{"确定"}, null, this, AlertView.Style.Alert, this).show();
    }
    public void alertShow3(View view) {
        new AlertView(null, null, null, new String[]{"高亮按钮1", "高亮按钮2", "高亮按钮3"},
                new String[]{"其他按钮1", "其他按钮2", "其他按钮3", "其他按钮4", "其他按钮5", "其他按钮6",
                        "其他按钮7", "其他按钮8", "其他按钮9", "其他按钮10", "其他按钮11", "其他按钮12"},
                this, AlertView.Style.Alert, this).show();
    }
    public void alertShow4(View view) {
        new AlertView("标题", null, "取消", new String[]{"高亮按钮1"}, new String[]{"其他按钮1", "其他按钮2", "其他按钮3"}, this, AlertView.Style.ActionSheet, this).show();
    }
    public void alertShow5(View view) {
        new AlertView("标题", "内容", "取消", null, null, this, AlertView.Style.ActionSheet, this).setCancelable(true).show();
    }
    public void alertShow6(View view) {
        new AlertView("上传头像", null, "取消", null,
                new String[]{"拍照", "从相册中选择"},
                this, AlertView.Style.ActionSheet, this).show();
    }
}