package com.liangjie.yuxixiaozhushou.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.text.IDNA;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.liangjie.yuxixiaozhushou.CleanEditText;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.StorageStudent;
import com.liangjie.yuxixiaozhushou.utils.ToastUtils;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/17.
 */
public class InfoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "EstablishActivity";
    private ImageView classIconAdd;
    private CleanEditText classNameEt;
    private Button commitBtn;
    private Bitmap bitmap = null;
    private String className;
    private String imageUrlStr;
    private Uri bitmap2ImageUri;
    private String serializedString;
    private AVObject deserializedObject;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(R.layout.activity_stu_info);

        Intent intent = getIntent();
        serializedString = intent.getStringExtra("c_s_obj_str");
        try {
            deserializedObject = AVObject.parseAVObject(serializedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initViews();
        classIconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classIconAdd.setImageDrawable(null);
                Crop.pickImage(InfoActivity.this);
            }
        });
    }

    private void initViews() {
        classIconAdd = getView(R.id.class_icon);
        commitBtn = getView(R.id.btn_commit);
        try {
            String uriStr = deserializedObject.get("local_uri").toString();
            classIconAdd.setImageURI(Uri.parse(uriStr));
        } catch (Exception e) {
            Log.e("梁洁", "infoactivity initviews");
        }
        classNameEt = getView(R.id.et_class_name);
        classNameEt.setHint(deserializedObject.get("nickname").toString());
        classNameEt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        classNameEt.setImeOptions(EditorInfo.IME_ACTION_GO);
        classNameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // 点击虚拟键盘的done
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    if (checkInput()) {
                        commit();
                        finish();
                    }
                }
                return false;
            }
        });
        loadClassroomIcon(deserializedObject);
    }
    private void loadClassroomIcon(AVObject avObject) {
        final String url = avObject.get("user_icon_url").toString();
        className = avObject.get("nickname").toString();
        final AVObject avObjectTmp = avObject;
        final AVFile file = new AVFile(className,
                url, new HashMap<String, Object>());
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                if (e == null) {
                    Log.d("梁洁saved", "文件大小" + bytes.length);
                } else {
                    Log.d("梁洁saved", "出错了" + e.getMessage());
                }
                Log.e("梁洁", "done");
                //下载后文件的存储路径：Environment.getExternalStorageDirectory() + "/"+resName
                File downloadedFile = new File(Environment.getExternalStorageDirectory() + "/yuxixiaozhushou/"+className);
                Uri uri = Uri.fromFile(downloadedFile);
                avObjectTmp.put("local_uri", uri.toString());
                Log.e("梁洁local_uri", avObjectTmp.get("local_uri").toString());
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(downloadedFile);
                    fout.write(bytes);
                    Log.d("saved", "文件写入成功.");
                    fout.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                    Log.d("saved", "文件找不到.." + e1.getMessage());
                } catch (IOException e1) {
                    Log.d("saved", "文件读取异常.");
                }
                // bytes 就是文件的数据流
                handler.sendEmptyMessage(0);
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                // 下载进度数据，integer 介于 0 和 100。
            }
        });
    }

    //定义Handler对象
    protected Handler handler =new Handler(){
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //处理UI
            switch (msg.what){
                case 0:
                    Log.e("梁洁", "what =0");
                    String uriStr = deserializedObject.get("local_uri").toString();
                    classIconAdd.setImageURI(Uri.parse(uriStr));
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.btn_commit:
                if (checkInput()) {
                    commit();
                    finish();
                }
                break;
            default:
                break;
        }
    }
    private void commit() {
        // TODO:请求服务端添加班级
        //ClassroomAndStudent
        AVObject todo = AVObject.createWithoutData("ClassroomAndStudent", deserializedObject.getObjectId());
        String nickname = classNameEt.getText().toString().trim();
        if (!TextUtils.isEmpty(nickname)) {
            todo.put("nickname",nickname);
            deserializedObject.put("nickname", nickname);
        }
        if(bitmap != null) {
            todo.put("user_icon_url", imageUrlStr);
            deserializedObject.put("user_icon_url", imageUrlStr);
        }
        // 保存到云端
        todo.saveInBackground();
        Intent returnClassroom = new Intent();
        returnClassroom.putExtra("return_class_stu", deserializedObject.toString());
        setResult(RESULT_OK, returnClassroom);
    }

    private boolean checkInput() {
        className = classNameEt.getText().toString().trim();
        if (bitmap == null && TextUtils.isEmpty(className)) {
            ToastUtils.showShort(this, "没有修改哟！");
            finish();
        }
        return true;
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
                        getRealFilePath(InfoActivity.this,bitmap2ImageUri));
                Log.e("梁洁", getRealFilePath(InfoActivity.this,bitmap2ImageUri));
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
