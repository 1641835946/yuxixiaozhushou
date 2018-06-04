package com.liangjie.yuxixiaozhushou.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.activity.MainActivity;
import com.soundcloud.android.crop.Crop;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/12.
 */
public class MainAdapter extends ArrayAdapter<AVObject> {

    private int resourceId;
    private List<AVObject> objectList;
    public MainAdapter(Context context, int resource, List<AVObject> objectList) {
        super(context, resource, objectList);
        resourceId = resource;
        this.objectList = objectList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AVObject avObject = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView classroomName = (TextView) view.findViewById(R.id.tv_classroom_name);
        TextView createrName = (TextView) view.findViewById(R.id.tv_creater_name);

        if (!avObject.get("classroom_name").toString().equals("")){
            try {
                ImageView icon = (ImageView)view.findViewById(R.id.iv_icon) ;
                Log.e("梁洁avobject", avObject.toString());
                String uriStr = avObject.get("local_uri").toString();
                icon.setImageURI(Uri.parse(uriStr));
                classroomName.setText((String)avObject.get("classroom_name"));
                createrName.setText((String) avObject.get("creater_name"));
            } catch(Exception e) {
                Log.e("梁洁mainadapter getview", e.toString());
            }
        }
        return view;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }
}

