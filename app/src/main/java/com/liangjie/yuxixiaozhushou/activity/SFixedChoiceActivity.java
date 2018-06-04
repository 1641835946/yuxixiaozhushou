package com.liangjie.yuxixiaozhushou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.liangjie.yuxixiaozhushou.CleanEditText;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.model.SingleFixedChoice;
import com.liangjie.yuxixiaozhushou.utils.ToastUtils;

/**
 * Created by Administrator on 2017/5/14.
 */
public class SFixedChoiceActivity extends BaseActivity implements View.OnClickListener{

    private Boolean isClick = false;
    private CleanEditText problemEt;
    private Button certainBtn;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioA, mRadioB ,mRadioC, mRadioD;
    private EditText aEt, bEt, cEt, dEt;
    private SingleFixedChoice problem;
    private int select;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_single);
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
        mRadioGroup = (RadioGroup) findViewById(R.id.gendergroup);
        mRadioA = (RadioButton) findViewById(R.id.radioBtn_A);
        mRadioB = (RadioButton) findViewById(R.id.radioBtn_B);
        mRadioC = (RadioButton) findViewById(R.id.radioBtn_C);
        mRadioD = (RadioButton) findViewById(R.id.radioBtn_D);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isClick = true;
                switch (checkedId) {
                    case R.id.radioBtn_A:
                        select = 1;
                        break;
                    case R.id.radioBtn_B:
                        select = 2;
                        break;
                    case R.id.radioBtn_C:
                        select = 4;
                        break;
                    case R.id.radioBtn_D:
                        select = 8;
                        break;
                }
            }
        });
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
        } else if (!isClick) {
            ToastUtils.makeShortText("未选答案！",this);
        } else {
            problem = new SingleFixedChoice(
                    problemEt.getText().toString().trim(),
                    aEt.getText().toString().trim(),
                    bEt.getText().toString().trim(),
                    cEt.getText().toString().trim(),
                    dEt.getText().toString().trim(),
                    select);
            ToastUtils.makeLongText("保存成功！", this);
            Intent intent = new Intent();
            intent.putExtra("data_return", problem);
            Log.e("梁洁problem", problem.toString());
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}
