package com.liangjie.yuxixiaozhushou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;
import com.liangjie.yuxixiaozhushou.JSONUtil;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.adapter.JudgeAdapter;
import com.liangjie.yuxixiaozhushou.adapter.MultipleAdapter;
import com.liangjie.yuxixiaozhushou.adapter.SingleListAdapter;
import com.liangjie.yuxixiaozhushou.model.JudgeProblem;
import com.liangjie.yuxixiaozhushou.model.MultipleFixedChoice;
import com.liangjie.yuxixiaozhushou.model.PaperTemplate;
import com.liangjie.yuxixiaozhushou.model.SingleFixedChoice;
import com.liangjie.yuxixiaozhushou.utils.ToastUtils;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/17.
 */
public class MistakeActivity extends BaseActivity{

    private ListView singleListView;
    private ListView multipleListView;
    private ListView judgeListView;
    private ListAdapter singleAdapter;
    private ListAdapter multipleAdapter;
    private ListAdapter judgeAdapter;
    private me.zhanghai.android.materialratingbar.MaterialRatingBar ratingBar;
    private TextView scoreTv;

    private AVObject deserializedObject;
    private String classId;
    private PaperTemplate paperOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_mistake);

        Intent intent = getIntent();
        classId = intent.getStringExtra("class_info");
        paperOne = (PaperTemplate) intent.getSerializableExtra("paper");
        ratingBar = (me.zhanghai.android.materialratingbar.MaterialRatingBar) getView(R.id.rating_bar);
        float scoreF = (float)paperOne.getRightCount()/paperOne.getCount();
        ratingBar.setRating(5*scoreF);
        scoreTv = (TextView) getView(R.id.score_tv);
        scoreTv.setText("正确："+ paperOne.getRightCount()+"/"+paperOne.getCount());
        initListView();
    }

    private void initListView() {
        singleAdapter = new SingleListAdapter(MistakeActivity.this,
                R.layout.item_single_list, getSingleData());
        singleListView = (ListView) findViewById(R.id.single_list);
        singleListView.setAdapter(singleAdapter);
        multipleAdapter = new MultipleAdapter(MistakeActivity.this,
                R.layout.item_multiple, getMultipleData());
        multipleListView = (ListView) findViewById(R.id.multiple_list);
        multipleListView.setAdapter(multipleAdapter);
        judgeAdapter = new JudgeAdapter(MistakeActivity.this,
                R.layout.item_judge, getJudgeData());
        judgeListView = (ListView) findViewById(R.id.judge_list);
        judgeListView.setAdapter(judgeAdapter);
    }

    private List<SingleFixedChoice> getSingleData(){
        return paperOne.getsList();
    }
    private List<MultipleFixedChoice> getMultipleData(){
        return paperOne.getmList();
    }
    private List<JudgeProblem> getJudgeData(){
        return paperOne.getjList();
    }

}
