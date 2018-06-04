package com.liangjie.yuxixiaozhushou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.liangjie.yuxixiaozhushou.JSONUtil;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.adapter.JudgeAdapter;
import com.liangjie.yuxixiaozhushou.adapter.MultipleAdapter;
import com.liangjie.yuxixiaozhushou.adapter.ReplyAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/14.
 */
public class APaperActivity extends BaseActivity implements
        OnMenuItemClickListener, OnMenuItemLongClickListener {

    //Context-Menu.Android
    protected FragmentManager fragmentManager;
    protected ContextMenuDialogFragment mMenuDialogFragment;
    private ListView singleListView;
    private ListView multipleListView;
    private ListView judgeListView;
    private ListAdapter singleAdapter;
    private ListAdapter multipleAdapter;
    private ListAdapter judgeAdapter;

    private AVObject deserializedObject;
    private String classId;
    private ArrayList<PaperTemplate> testList;
    private PaperTemplate paper;
    private String paperName;
    private SingleFixedChoice firstSingle;
    private MultipleFixedChoice firstMultiple;
    private JudgeProblem firstJudge;
    private List<String> strTestList;
    private PaperTemplate paperOne;
    private boolean isRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_apaper);

        Intent intent = getIntent();
        classId = intent.getStringExtra("class_info");
        paperName = intent.getStringExtra("class_name");
        paper = new PaperTemplate(paperName);
        String serializedString = intent.getStringExtra("test_list");
        if (serializedString != null) {
            try {
                deserializedObject = AVObject.parseAVObject(serializedString);
                Log.e("梁洁apaper", serializedString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            strTestList = (List<String>) deserializedObject.getList("test_list");
            ArrayList<PaperTemplate> paperList = new ArrayList<PaperTemplate>();
            if (strTestList != null && strTestList.size()>0) {
                // string to papertemplate
                for (int i = 0; i<strTestList.size(); i++){
                    paperList.add(JSONUtil.parseJSONWithJSONObject(strTestList.get(i)));
                }
                testList = paperList;
            }
            isRead = false;
        }
        paperOne = (PaperTemplate) intent.getSerializableExtra("paper");
        if (paperOne != null) {
            isRead = true;
        }
        firstSingle = new SingleFixedChoice("","","","","",1);
        firstMultiple = new MultipleFixedChoice("","","","","",1);
        firstJudge = new JudgeProblem("",1);

        initListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListView();
    }

    private void initListView() {
        singleAdapter = new SingleListAdapter(APaperActivity.this,
                R.layout.item_single_list, getSingleData());
        singleListView = (ListView) findViewById(R.id.single_list);
        singleListView.setAdapter(singleAdapter);
        multipleAdapter = new MultipleAdapter(APaperActivity.this,
                R.layout.item_multiple, getMultipleData());
        multipleListView = (ListView) findViewById(R.id.multiple_list);
        multipleListView.setAdapter(multipleAdapter);
        judgeAdapter = new JudgeAdapter(APaperActivity.this,
                R.layout.item_judge, getJudgeData());
        judgeListView = (ListView) findViewById(R.id.judge_list);
        judgeListView.setAdapter(judgeAdapter);
    }

    private List<SingleFixedChoice> getSingleData(){
//        List<SingleFixedChoice> singList;
//        if (paper.getsList().size() == 0) {
//            singList = new ArrayList<>();
//            singList.add(firstSingle);
//        } else {
//            singList = paper.getsList();
//        }
//        return singList;
        if (isRead) {
            return paperOne.getsList();
        } else{
            return paper.getsList();
        }
    }
    private List<MultipleFixedChoice> getMultipleData(){
//        List<MultipleFixedChoice> multipleList;
//        if (paper.getsList().size() == 0) {
//            multipleList = new ArrayList<>();
//            multipleList.add(firstMultiple);
//        } else {
//            multipleList = paper.getmList();
//        }
//        return multipleList;
        if (isRead) {
            return paperOne.getmList();
        } else{
            return paper.getmList();
        }
    }
    private List<JudgeProblem> getJudgeData(){
//        List<JudgeProblem> judgeList;
//        if (paper.getsList().size() == 0) {
//            judgeList = new ArrayList<>();
//            judgeList.add(firstJudge);
//        } else {
//            judgeList = paper.getjList();
//        }
//        return judgeList;
        if (isRead) {
            return paperOne.getjList();
        } else{
            return paper.getjList();
        }
    }
    protected void initView(int id) {
        //BaseActivity中加载AlertView
        super.initView(id);

        fragmentManager = getSupportFragmentManager();
        initMenuFragment();
    }
    protected List<MenuObject> getMenuObjects() {
        List<MenuObject> menuObjects = new ArrayList<>();
        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);
        MenuObject send = new MenuObject("单选题");
        send.setResource(R.drawable.single);
        MenuObject like = new MenuObject("多选题");
        like.setResource(R.drawable.multiple);
        MenuObject addFr = new MenuObject("判断题");
        addFr.setResource(R.drawable.judge);
        MenuObject save = new MenuObject("保存");
        save.setResource(R.drawable.save);
        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(save);
        return menuObjects;
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {
            case 1:
                if (isRead) {
                    ToastUtils.makeShortText( "已上传的试卷不能编辑，只能查看！",APaperActivity.this);
                } else {
                    Intent sIntent = new Intent(this, SFixedChoiceActivity.class);
                    startActivityForResult(sIntent,1);
                }
                break;
            case 2:
                if (isRead) {
                    ToastUtils.makeShortText( "已上传的试卷不能编辑，只能查看！",APaperActivity.this);
                } else {
                    Intent mIntent = new Intent(this, MFixedChoiceActivity.class);
                    startActivityForResult(mIntent,2);
                }
                break;
            case 3:
                if (isRead) {
                    ToastUtils.makeShortText( "已上传的试卷不能编辑，只能查看！",APaperActivity.this);
                } else {
                    Intent jIntent = new Intent(this, JudgeActivity.class);
                    startActivityForResult(jIntent,3);
                }
                break;
            case 4:
                if (isRead) {
                    ToastUtils.makeShortText( "已上传的试卷不能编辑，只能查看！",APaperActivity.this);
                } else {
                    Boolean unSEmpty = false;
                    if (paper.getsList().size() > 0) unSEmpty = true;
                    Boolean unMEmpty = false;
                    if (paper.getmList().size() > 0) unMEmpty = true;
                    Boolean unJEmpty =false;
                    if (paper.getjList().size() > 0) unJEmpty = true;
//                    Boolean unMEmpty = !TextUtils.isEmpty(paper.getmList().get(0).getProblem().toString().trim());
//                    Boolean unJEmpty = !TextUtils.isEmpty(paper.getjList().get(0).getProblem().toString().trim());
                    if(unSEmpty || unMEmpty || unJEmpty){
                        paper.setCount(paper.getCountNum());
                        if(testList==null){
                            testList = new ArrayList<>();
                            Log.e("梁洁testlist", "testList is null");
                        }
                        testList.add(0, paper);
                        Log.e("梁洁testlist", testList.toString());
                        List<String> dataList = new ArrayList<>();
                        for (int i = 0; i<testList.size(); i++){
                            try{
                                String str = JSONUtil.createJson(testList.get(i));
                                Log.e("梁洁json", str);
                                dataList.add(str);
                            } catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                        AVObject todo = AVObject.createWithoutData("ClassroomTable", classId);
                        todo.put("test_list",dataList);
                        // 保存到云端
                        Log.e("梁洁paper", paper.toString());
                        Log.e("梁洁testlist", testList.toString());
                        todo.saveInBackground();
                        finish();
                    }
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                try {
                    SingleFixedChoice singleFixedChoice =
                            (SingleFixedChoice) data.getSerializableExtra("data_return");
                    paper.getsList().add(singleFixedChoice);
                } catch (Exception e) {
                    Log.e("梁洁exception", "onActivityResult");
                }
                break;
            case 2:
                try {
                    MultipleFixedChoice multipleFixedChoice =
                            (MultipleFixedChoice) data.getSerializableExtra("data_return");
                    paper.getmList().add(multipleFixedChoice);
                } catch (Exception e) {
                    Log.e("梁洁exception", "onActivityResult");
                }
                break;
            case 3:
                try {
                    JudgeProblem judgeProblem =
                            (JudgeProblem) data.getSerializableExtra("data_return");
                    paper.getjList().add(judgeProblem);
                } catch (Exception e) {
                    Log.e("梁洁exception", "onActivityResult");
                }
                break;
            default:
        }
    }

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
