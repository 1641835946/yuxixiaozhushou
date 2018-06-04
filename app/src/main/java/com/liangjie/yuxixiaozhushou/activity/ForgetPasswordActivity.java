package com.liangjie.yuxixiaozhushou.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.liangjie.yuxixiaozhushou.CleanEditText;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.utils.ToastUtils;

import static android.view.View.OnClickListener;

/**
 * @desc 忘记密码
 * Created by devilwwj on 16/1/24.
 */
public class ForgetPasswordActivity extends Activity implements OnClickListener {
    private static final String TAG = "SignupActivity";
    // 界面控件
    private CleanEditText usernameEdit;
    private CleanEditText passwordEdit;
    private CleanEditText answerEdit;
    private CleanEditText problemHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpwd);
        initViews();
    }

    /**
     * 通用findViewById,减少重复的类型转换
     *
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <E extends View> E getView(int id) {
        try {
            return (E) findViewById(id);
        } catch (ClassCastException ex) {
            Log.e(TAG, "Could not cast View to concrete class.", ex);
            throw ex;
        }
    }

    private void initViews() {
        usernameEdit = getView(R.id.et_username);
        usernameEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        problemHint = getView(R.id.ht_problem);
        problemHint.setHint(R.string.problem);
        answerEdit = getView(R.id.et_answer);
        answerEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        passwordEdit = getView(R.id.et_password);
        passwordEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordEdit.setImeOptions(EditorInfo.IME_ACTION_GO);
        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // 点击虚拟键盘的done
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    commit();
                }
                return false;
            }
        });
    }

    String username;
    String answer;
    String password;
    private void commit() {
        username = usernameEdit.getText().toString().trim();
        answer = answerEdit.getText().toString().trim();
        password = passwordEdit.getText().toString().trim();

        if (checkInput(username, answer, password)) {
            Log.e("梁洁", "验证密保答案，修改密码");
            // TODO:请求服务端注册账号
        }
    }

    private boolean checkInput(String username, String answer, String password) {
        if (TextUtils.isEmpty(username)) {
            ToastUtils.showShort(this, R.string.tip_name_can_not_be_empty);
        } else if (TextUtils.isEmpty(answer)) {
            ToastUtils.showShort(this, R.string.tip_answer_can_not_be_empty);
        } else if (password.length() < 6 || password.length() > 32
                || TextUtils.isEmpty(password)) { // 密码格式
            ToastUtils.showShort(this,
                    R.string.tip_please_input_6_32_password);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset_pwd:
                commit();
                break;
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.btn_get_problem:
                username = usernameEdit.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    ToastUtils.showShort(this,
                            R.string.tip_name_can_not_be_empty);
                } else {
                    // TODO 请求发送密保问题，并setHint()
                }
                break;
            default:
                break;
        }
    }
}
