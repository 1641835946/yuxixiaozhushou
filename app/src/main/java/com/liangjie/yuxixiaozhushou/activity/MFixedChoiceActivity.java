package com.liangjie.yuxixiaozhushou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.liangjie.yuxixiaozhushou.CleanEditText;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.model.MultipleFixedChoice;
import com.liangjie.yuxixiaozhushou.model.SingleFixedChoice;
import com.liangjie.yuxixiaozhushou.utils.ToastUtils;

/**
 * Created by Administrator on 2017/5/14.
 */
public class MFixedChoiceActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener,
View.OnClickListener{

    private CleanEditText problemEt;
    private Button certainBtn;
    private CheckBox mCheckBoxA, mCheckBoxB ,mCheckBoxC, mCheckBoxD;
    private EditText aEt, bEt, cEt, dEt;
    private MultipleFixedChoice problem;
    private int answer = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_multiple);

        certainBtn = (Button) findViewById(R.id.btn_commit);
        certainBtn.setOnClickListener(this);
        problemEt = (CleanEditText) findViewById(R.id.et_problem);
        problemEt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        problemEt.setTransformationMethod(HideReturnsTransformationMethod
                .getInstance());
        aEt = (EditText) findViewById(R.id.et_A);
        aEt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        aEt.setTransformationMethod(HideReturnsTransformationMethod
                .getInstance());
        bEt = (EditText) findViewById(R.id.et_B);
        bEt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        bEt.setTransformationMethod(HideReturnsTransformationMethod
                .getInstance());
        cEt = (EditText) findViewById(R.id.et_C);
        cEt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        cEt.setTransformationMethod(HideReturnsTransformationMethod
                .getInstance());
        dEt = (EditText) findViewById(R.id.et_D);
        dEt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        dEt.setImeOptions(EditorInfo.IME_ACTION_GO);
        dEt.setTransformationMethod(HideReturnsTransformationMethod
                .getInstance());
        dEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    commit();
                }
                return false;
            }
        });
        mCheckBoxA = (CheckBox) findViewById(R.id.checkbox_a);
        mCheckBoxB = (CheckBox) findViewById(R.id.checkbox_b);
        mCheckBoxC = (CheckBox) findViewById(R.id.checkbox_c);
        mCheckBoxD = (CheckBox) findViewById(R.id.checkbox_d);

        //通过OnCheckedChangeListener来设置来个CheckBox对象
        mCheckBoxA.setOnCheckedChangeListener(this);
        mCheckBoxB.setOnCheckedChangeListener(this);
        mCheckBoxC.setOnCheckedChangeListener(this);
        mCheckBoxD.setOnCheckedChangeListener(this);
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {
        answer = 0;
        // TODO Auto-generated method stub
        //在这里进行你需要的逻辑
        if(mCheckBoxA.isChecked()){
            answer = answer + 1;
        }
        if(mCheckBoxB.isChecked()){
            answer = answer + 2;
        }
        if(mCheckBoxC.isChecked()){
            answer = answer + 4;
        }
        if(mCheckBoxD.isChecked()){
            answer = answer + 8;
        }
    }
    @Override
    public void onClick(View v) {
        commit();
    }

    private void commit() {
        if (TextUtils.isEmpty(problemEt.getText().toString().trim())){
            ToastUtils.makeShortText("题目为空!", this);
        } else if (TextUtils.isEmpty(aEt.getText().toString().trim())) {
            ToastUtils.makeShortText("选项A未填答案！",this);
        } else if (TextUtils.isEmpty(bEt.getText().toString().trim())) {
            ToastUtils.makeShortText("选项B未填答案！",this);
        } else if (TextUtils.isEmpty(cEt.getText().toString().trim())) {
            ToastUtils.makeShortText("选项C未填答案！",this);
        } else if (TextUtils.isEmpty(dEt.getText().toString().trim())) {
            ToastUtils.makeShortText("选项D未填答案！",this);
        } else if (answer==0) {
            ToastUtils.makeShortText("未选答案！",this);
        } else {
            problem = new MultipleFixedChoice(
                    problemEt.getText().toString().trim(),
                    aEt.getText().toString().trim(),
                    bEt.getText().toString().trim(),
                    cEt.getText().toString().trim(),
                    dEt.getText().toString().trim(),
                    answer);
            ToastUtils.makeLongText("保存成功！", this);
            Intent intent = new Intent();
            intent.putExtra("data_return", problem);
            Log.e("梁洁problem", problem.toString());
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}

