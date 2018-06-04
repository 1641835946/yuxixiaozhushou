package com.liangjie.yuxixiaozhushou.activity;

/**
 * Created by Administrator on 2017/5/6.
 */

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.liangjie.yuxixiaozhushou.R;
import com.soundcloud.android.crop.Crop;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CropActivity extends Activity {

    private ImageView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        resultView = (ImageView) findViewById(R.id.result_image);
        resultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultView.setImageDrawable(null);
                Crop.pickImage(CropActivity.this);
            }
        });
    }

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
            Bitmap bitmap = null;
            Uri imageUri = Crop.getOutput(result);
            resultView.setImageURI(Crop.getOutput(result));
            try {
                //uri to bitmap
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap = ImageCompressL(bitmap);
            Uri bitmap2ImageUri = getImageUri(this, bitmap);
            try {
                final AVFile file = AVFile.withAbsoluteLocalPath(
                        "icon_"+AVUser.getCurrentUser().getUsername() +".png",
                        getRealFilePath(CropActivity.this,bitmap2ImageUri));
                //nickname 图片而非文件
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        Log.e("梁洁", file.getUrl());//返回一个唯一的 Url 地址
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

