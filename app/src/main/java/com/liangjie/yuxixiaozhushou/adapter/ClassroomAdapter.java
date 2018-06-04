package com.liangjie.yuxixiaozhushou.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.TimeUtil;
import com.liangjie.yuxixiaozhushou.model.MultipleFixedChoice;

import java.util.Date;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/5/12.
 */
public class ClassroomAdapter extends ArrayAdapter<AVObject> {

    private int resourceId;
    private List<AVObject> objectList;
    public ClassroomAdapter(Context context, int resource, List<AVObject> objectList) {
        super(context, resource, objectList);
        resourceId = resource;
        this.objectList = objectList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AVObject avObject = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView commentName = (TextView) view.findViewById(R.id.comment_name) ;
        TextView commentContent = (TextView) view.findViewById(R.id.comment_content) ;
        TextView commentTime = (TextView) view.findViewById(R.id.comment_time) ;
        ImageView icon = (ImageView)view.findViewById(R.id.iv_icon) ;

        commentContent.setText((String)avObject.get("comment_content"));
        if (avObject.get("comment_content").toString().equals("")){
            commentTime.setText("无数据！");
        } else {
            try {
                commentName.setText((String)avObject.get("nickname"));
                Date date = (Date)avObject.get("createdAt");
                commentTime.setText(TimeUtil.getMyTime(date));
                String uriStr = avObject.get("local_uri").toString();
                icon.setImageURI(Uri.parse(uriStr));
            } catch(Exception e) {
                Log.e("梁洁 classroomadapter", e.toString());
            }
        }
        return view;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }
}


