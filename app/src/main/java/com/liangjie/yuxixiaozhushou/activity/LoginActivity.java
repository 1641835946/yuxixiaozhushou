package com.liangjie.yuxixiaozhushou.activity;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.liangjie.yuxixiaozhushou.CleanEditText;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.StorageStudent;
import com.liangjie.yuxixiaozhushou.utils.ToastUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements OnClickListener{

    private static final String TAG = "loginActivity";
    private static final int REQUEST_CODE_TO_REGISTER = 0x001;

    // UI references.
    private CleanEditText usernameEdit;
    private CleanEditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_login);
        initViews();
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        usernameEdit = (CleanEditText) this.findViewById(R.id.et_name);
        usernameEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        usernameEdit.setTransformationMethod(HideReturnsTransformationMethod
                .getInstance());
        passwordEdit = (CleanEditText) this.findViewById(R.id.et_password);
        passwordEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordEdit.setImeOptions(EditorInfo.IME_ACTION_GO);
        passwordEdit.setTransformationMethod(PasswordTransformationMethod
                .getInstance());
        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    clickLogin();
                }
                return false;
            }
        });
    }

    /**
     * 跳转到忘记密码
     */
    private void enterForgetPwd() {
        //邮箱重置密码
        String username = usernameEdit.getText().toString().trim();
        if (!TextUtils.isEmpty(username)) {
            AVUser.requestPasswordResetInBackground(AVUser.getCurrentUser().getEmail(),
                    new RequestPasswordResetCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                ToastUtils.makeLongText("请检查邮件", LoginActivity.this);
                            } else {
                                ToastUtils.makeLongText(e.toString(), LoginActivity.this);
                            }
                        }
                    });
        } else {
            ToastUtils.makeLongText("请输入用户名", LoginActivity.this);
        }
//        Intent intent = new Intent(this, ForgetPasswordActivity.class);
//        startActivity(intent);
    }

    /**
     * 跳转到注册页面
     */
    private void enterRegister() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivityForResult(intent, REQUEST_CODE_TO_REGISTER);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
//            case R.id.iv_cancel:
//                finish();
//                break;
            case R.id.btn_login:
                clickLogin();
                break;
            case R.id.tv_create_account:
                enterRegister();
                break;
            case R.id.tv_forget_password:
                enterForgetPwd();
                break;
            default:
                break;
        }
    }
    /**
     * 检查输入
     *
     * @param account
     * @param password
     * @return
     */
    public boolean checkInput(String account, String password) {
        // 账号为空时提示
        if (account == null || account.trim().equals("")) {
            Toast.makeText(this, R.string.tip_name_can_not_be_empty, Toast.LENGTH_LONG)
                    .show();
        } else if (password == null || password.trim().equals("")) {
                Toast.makeText(this, R.string.tip_password_can_not_be_empty,
                        Toast.LENGTH_LONG).show();
        } else {
            return true;
        }
        return false;
    }
    private void clickLogin() {
        String account = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (checkInput(account, password)) {
            // TODO: 请求服务器登录账号
            AVUser.logInInBackground(account, password, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null) {
                        //登录成功
                        Log.e("梁洁", AVUser.getCurrentUser().getUsername());
                        AVQuery<AVObject> query = new AVQuery<>("StudentTable");
                        query.whereStartsWith("user_object_id", AVUser.getCurrentUser().getObjectId());
                        query.countInBackground(new CountCallback() {
                            @Override
                            public void done(int i, AVException e) {
                                if (e == null) {
                                    // 查询成功，输出计数
                                    Log.e("梁洁", "今天完成了" + i + "条待办事项。");
                                    if (i == 0) {
                                        addStudentTable();
                                    }
                                } else {
                                    // 查询失败
                                    Log.e("梁洁", e.toString());
                                    addStudentTable();
                                }
                            }
                        });
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //登录失败
                        Toast.makeText(LoginActivity.this, e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void addStudentTable() {
//**********************************数据库表：StudentTable******************************************
        AVObject student = new AVObject("StudentTable");// 构建对象
        student.put("user_object_id", AVUser.getCurrentUser().getObjectId());
        student.put("user_name", AVUser.getCurrentUser().getUsername());
        student.put("user_icon_url", AVUser.getCurrentUser().get("user_icon_url"));
        student.saveInBackground();// 保存到服务端
    }
}

