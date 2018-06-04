package com.liangjie.yuxixiaozhushou.activity;

/**
 * Created by Administrator on 2017/5/9.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.liangjie.yuxixiaozhushou.JSONUtil;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.Sandwich.SandwichWizardModel;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.AbstractWizardModel;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.ModelCallbacks;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.Page;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.ui.PageFragmentCallbacks;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.ui.ReviewFragment;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.ui.StepPagerStrip;
import com.liangjie.yuxixiaozhushou.model.PaperTemplate;

import org.json.JSONException;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tech.linjiang.suitlines.Unit;

public class AnswerActivity extends FragmentActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks {
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private boolean mEditingAfterReview;
    private AbstractWizardModel mWizardModel;
    private boolean mConsumePageSelectedEvent;
    private Button mNextButton;
    private Button mPrevButton;
    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;

    private AVObject deserializedObject;
    private String classId;//classroom objectId
    public static ArrayList<PaperTemplate> testList;
//    private PaperTemplate paperTemplate;
    private String className;
    private List<String> strTestList;
    private int position;
    private String paperName;
    private String cSObjectIdStr;
    public static int paperNum;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sandwich);

        Intent intent = getIntent();
        classId = intent.getStringExtra("class_info");
        paperName = intent.getStringExtra("class_name");
        cSObjectIdStr = intent.getStringExtra("c_s_obj_id");
        String serializedString = intent.getStringExtra("test_list");
        try {
            deserializedObject = AVObject.parseAVObject(serializedString);
            Log.e("梁洁answer", serializedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        strTestList = (List<String>) deserializedObject.getList("test_list");
        final ArrayList<PaperTemplate> paperList = new ArrayList<PaperTemplate>();
        if (strTestList != null && strTestList.size()>0) {
            // string to papertemplate
            for (int i = 0; i<strTestList.size(); i++){
                paperList.add(JSONUtil.parseJSONWithJSONObject(strTestList.get(i)));
            }
            testList = paperList;
        }

        mWizardModel = new SandwichWizardModel(this);
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdapter.getCount() - 1, position);
                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AnswerActivity.this);
                    dialog.setTitle("提示");
                    dialog.setMessage(R.string.submit_confirm_message);
                    dialog.setCancelable(false);
                    dialog.setPositiveButton(R.string.submit_confirm_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            List<String> dataList = new ArrayList<>();
//                            List<Integer> scoreList = new ArrayList<Integer>();
//                            for (int i = 0; i<testList.size(); i++){
//                                try{
//                                    String str = JSONUtil.createJson(testList.get(i));
//                                    Log.e("梁洁json", str);
//                                    dataList.add(str);
//                                } catch(JSONException e){
//                                    e.printStackTrace();
//                                }
//                            }
//                            AVObject todo = AVObject.createWithoutData("ClassroomAndStudent", cSObjectIdStr);
//                            todo.put("answer_list",dataList);
//                            // 保存到云端
//                            Log.e("梁洁testlist", testList.toString());
//                            todo.saveInBackground();
                            //todo 成绩上传与成绩榜，试题库
//********************************************ScoreTable***********************************************************
                            final PaperTemplate paperTemplate = testList.get(paperNum);
                            paperTemplate.setRightCount(paperTemplate.getRightCountNum());
                            final int score = 100*paperTemplate.getRightCount()/paperTemplate.getCount();

//                            final AVQuery<AVObject> priorityQuery = new AVQuery<>("ScoreTable");
//                            priorityQuery.whereStartsWith("student_name", AVUser.getCurrentUser().getUsername());
                            final AVQuery<AVObject> stuIdQuery = new AVQuery<>("ScoreTable");
                            stuIdQuery.whereStartsWith("student_id", AVUser.getCurrentUser().getObjectId());
                            final AVQuery<AVObject> statusQuery = new AVQuery<>("ScoreTable");
                            statusQuery.whereStartsWith("classroom_id", classId);
                            final AVQuery<AVObject> paperQuery = new AVQuery<>("ScoreTable");
                            statusQuery.whereStartsWith("paper_name", paperTemplate.getName());
                            AVQuery<AVObject> query = AVQuery.and(Arrays.asList(stuIdQuery, statusQuery, paperQuery));
                            query.findInBackground(new FindCallback<AVObject>() {
                                @Override
                                public void done(List<AVObject> list, AVException e) {
                                    if (e==null) {
                                        if (list.size()==0) {
                                            AVObject todoFolder = new AVObject("ScoreTable");// 构建对象
                                            todoFolder.put("student_name", AVUser.getCurrentUser().getUsername());
                                            todoFolder.put("student_id", AVUser.getCurrentUser().getObjectId());
                                            todoFolder.put("classroom_id", classId);//String
                                            todoFolder.put("score", score);//int
                                            todoFolder.put("paper_name", paperTemplate.getName());
                                            todoFolder.put("paper_num", paperNum);
                                            todoFolder.saveInBackground();// 保存到服务端
                                        } else {
                                            // 第一参数是 className,第二个参数是 objectId
                                            AVObject todo = AVObject.createWithoutData("ScoreTable", list.get(0).getObjectId());
                                            // 修改 content
                                            todo.put("score",score);
                                            // 保存到云端
                                            todo.saveInBackground();
                                        }
                                        Intent mistakeIntent = new Intent(AnswerActivity.this, MistakeActivity.class);
                                        mistakeIntent.putExtra("class_info", classId);
                                        mistakeIntent.putExtra("paper", testList.get(paperNum));
                                        startActivity(mistakeIntent);
                                        finish();
                                    }
                                }
                            });
                        }
                    });
                    dialog.setNegativeButton("取消", null);
                    dialog.show();
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        onPageTreeChanged();
        updateBottomBar();
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 = review step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            mNextButton.setText(R.string.finish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        } else {
            mNextButton.setText(mEditingAfterReview
                    ? R.string.review
                    : R.string.next);
            mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
            mNextButton.setTextAppearance(this, v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            if (mCurrentPageSequence == null) {
                return 0;
            }
            return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }
}

