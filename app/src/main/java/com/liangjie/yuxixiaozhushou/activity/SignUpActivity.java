package com.liangjie.yuxixiaozhushou.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.liangjie.yuxixiaozhushou.CleanEditText;
import com.liangjie.yuxixiaozhushou.MyLeanCloudApp;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.utils.ToastUtils;
import com.liangjie.yuxixiaozhushou.utils.VerifyCode;

/**
 * @desc 注册界面
 */
public class SignUpActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "SignupActivity";
    // 界面控件
    private CleanEditText usernameEdit;
    private CleanEditText passwordEdit;
    private CleanEditText passwordAgainEdit;
    private CleanEditText verifyCodeEdit;
    private CleanEditText emailEdit;
    private VerifyCode getVerifiCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_signup);
        initViews();
    }

    private void initViews() {

        usernameEdit = getView(R.id.et_username);
        usernameEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        getVerifiCodeButton = getView(R.id.btn_send_verifi_code);
        getVerifiCodeButton.setOnClickListener(this);
        verifyCodeEdit = getView(R.id.et_verifiCode);
        verifyCodeEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        passwordEdit = getView(R.id.et_password);
        passwordEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        passwordAgainEdit = getView(R.id.et_password_again);
        passwordAgainEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        emailEdit = getView(R.id.et_email);
        emailEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        emailEdit.setImeOptions(EditorInfo.IME_ACTION_GO);
        emailEdit.setOnEditorActionListener(new OnEditorActionListener() {

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

    private void commit() {
        String username = usernameEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String passwordAgain = passwordAgainEdit.getText().toString().trim();
        String code = verifyCodeEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        if (checkInput(username, password, passwordAgain, code, email)) {
            // TODO:请求服务端注册账号
            AVUser user = new AVUser();// 新建 AVUser 对象实例
            user.setUsername(username);// 设置用户名
            user.setEmail(email);//设置邮箱
            user.setPassword(password);// 设置密码

//            user.setMobilePhoneNumber("187-1275-6816");
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        // 注册成功
                        ToastUtils.showShort(SignUpActivity.this, R.string.sign_up_successfully);
                        finish();
                    } else {
                        // 失败的原因
                        ToastUtils.showShort(SignUpActivity.this, e.toString());
                    }
                }
            });
        }
    }

    private boolean checkInput(String name, String password, String passwordAgain, String code, String email) {
        if (TextUtils.isEmpty(name)) { // 用户名为空
            ToastUtils.showShort(this, R.string.tip_name_can_not_be_empty);
        } else if (TextUtils.isEmpty(code)) { // 验证码为空
            ToastUtils.showShort(this, R.string.tip_please_input_code);
        } else if (password.length() < 6 || password.length() > 32
                || TextUtils.isEmpty(password)) { // 密码格式
            ToastUtils.showShort(this,
                    R.string.tip_please_input_6_32_password);
        } else if (!password.equals(passwordAgain)) { // 核对密码
            ToastUtils.showShort(this, R.string.tip_password_verifity);
        } else if (TextUtils.isEmpty(email)) { // 邮箱地址为空
            ToastUtils.showShort(this, R.string.tip_account_empty);
        } else if (!getVerifiCodeButton.isEqualsIgnoreCase(code)) {//验证码错误
            ToastUtils.showShort(this, R.string.tip_input_code_is_false);
        } else {
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.iv_cancel:
//                finish();//梁洁：跳出程序***************************************************************
//                //还有按下back键
//                break;
            case R.id.btn_create_account:
                commit();
                break;
            default:
                break;
        }
    }
}
