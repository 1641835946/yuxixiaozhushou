package com.liangjie.yuxixiaozhushou.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.liangjie.yuxixiaozhushou.CleanEditText;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.StorageStudent;
import com.liangjie.yuxixiaozhushou.utils.ToastUtils;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Administrator on 2017/5/6.
 */
public class EstablishActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "EstablishActivity";
    private ImageView classIconAdd;
    private CleanEditText classNameEt;
    private CleanEditText classInfoEt;
    private Button commitBtn;
    private Bitmap bitmap = null;
    private String className;
    private String classInfo;
    private String imageUrlStr;
    private Uri bitmap2ImageUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_establish);
        initViews();
        classIconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classIconAdd.setImageDrawable(null);
                Crop.pickImage(EstablishActivity.this);
            }
        });
    }

    private void initViews() {
        classIconAdd = getView(R.id.class_icon);
        commitBtn = getView(R.id.btn_commit);
        classNameEt = getView(R.id.et_class_name);
        classNameEt.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        classInfoEt = getView(R.id.et_class_info);
        classInfoEt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        classInfoEt.setImeOptions(EditorInfo.IME_ACTION_GO);
        classInfoEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // 点击虚拟键盘的done
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    checkInput();
                }
                return false;
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                finish();//梁洁：跳出程序***************************************************************
                //还有按下back键
                break;
            case R.id.btn_commit:
                checkInput();
                break;
            default:
                break;
        }
    }
    private void commit() {
        // TODO:请求服务端添加班级
//***************************************数据库表：ClassroomTable***********************************
        AVObject add = new AVObject("ClassroomTable");
        add.put("classroom_name", className);
        add.put("classroom_info", classInfo);
        add.put("classroom_icon_url", imageUrlStr);
        //todo 外键
        add.put("creater_name", AVUser.getCurrentUser().getUsername());
//        add.saveInBackground();// 保存到服务端
        ToastUtils.makeLongText("建立成功", EstablishActivity.this);
        // 设置默认打开的 Activity
        //todo 老师加入班级
        AVObject studentCourseMapTom = new AVObject("ClassroomAndStudent");// 选课表对象
        // 设置关联
        StorageStudent storageStudent = StorageStudent.getInstance();
        AVObject student = storageStudent.getStudent();
        Log.e("梁洁Student", student.toString());
        studentCourseMapTom.put("student_pointer", student);//pointer
        studentCourseMapTom.put("classroom_pointer", add);
        //***********************************************************************************
        studentCourseMapTom.put("nickname",student.get("user_name"));//string name
        studentCourseMapTom.put("user_icon_url", student.get("user_icon_url"));//todo 默认值
        studentCourseMapTom.put("classroom_icon_url", add.get("classroom_icon_url"));
        studentCourseMapTom.put("classroom_name", add.get("classroom_name"));//string name
        studentCourseMapTom.put("creater_name", add.get("creater_name"));
        //***********************************************************************************
        studentCourseMapTom.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    Log.e("梁洁", e.toString());
                } else {
                    Log.e("梁洁", "成功加入");
                }
                finish();
            }
        });
    }

    public byte[] img(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    private void checkInput() {
        className = classNameEt.getText().toString().trim();
        classInfo = classInfoEt.getText().toString().trim();
        if (bitmap == null) {
            ToastUtils.showShort(this, "请上传图片！");
        } else if (TextUtils.isEmpty(className)) {
            ToastUtils.showShort(this, R.string.create_class_name_hint);
        } else if (TextUtils.isEmpty(classInfo)) {
            ToastUtils.showShort(this, R.string.create_class_info_hint);
        } else {
            AVQuery<AVObject> query = new AVQuery<>("ClassroomTable");
            query.whereStartsWith("classroom_name", className);
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    // object 就是符合条件的第一个 AVObject
                    if (e == null) {
                        if (avObject == null) {
                            commit();
                        } else if (avObject.get("classroom_name").toString().equals(className)) {
                            ToastUtils.makeShortText("班级名已存在", EstablishActivity.this);
                        } else {
                            commit();
                        }
                    }
                }
            });
        }
    }
    //**************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri imageUri = Crop.getOutput(result);
            classIconAdd.setImageURI(Crop.getOutput(result));
            try {
                //uri to bitmap
                Log.e("梁洁URI", imageUri.toString());
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap = ImageCompressL(bitmap);
            bitmap2ImageUri = getImageUri(this, bitmap);
            try {
                final AVFile file = AVFile.withAbsoluteLocalPath(
                        "icon.png",
                        getRealFilePath(EstablishActivity.this,bitmap2ImageUri));
                Log.e("梁洁", getRealFilePath(EstablishActivity.this,bitmap2ImageUri));
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        imageUrlStr = file.getUrl();
                        Log.e("梁洁", "imageURLStr is " + imageUrlStr);//返回一个唯一的 Url 地址
                    }
                });
            }catch (FileNotFoundException e) {
                Log.e("梁洁", "file not found");
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //bitmap to uri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    //计算 bitmap大小，如果超过64kb，则进行压缩
    private Bitmap ImageCompressL(Bitmap bitmap) {
        double targetwidth = Math.sqrt(64.00 * 1000);
        if (bitmap.getWidth() > targetwidth || bitmap.getHeight() > targetwidth) {
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 计算宽高缩放率
            double x = Math.max(targetwidth / bitmap.getWidth(), targetwidth / bitmap.getHeight());
            // 缩放图片动作
            matrix.postScale((float) x, (float) x);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }
    //uri to path
    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
