package com.liangjie.yuxixiaozhushou.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.TimeUtil;
import com.liangjie.yuxixiaozhushou.model.Notice;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/19.
 */
public class PushAdapter extends ArrayAdapter<Notice> {

    private int resourceId;
    private List<Notice> objectList;
    public PushAdapter(Context context, int resource, List<Notice> objectList) {
        super(context, resource, objectList);
        resourceId = resource;
        Log.e("梁洁", "learningadapter");
        this.objectList = objectList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("梁洁", "getView()");
        Notice notice = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        TextView time = (TextView) view.findViewById(R.id.tv_time);
//        if(avObject.get("res_name").equals("")){
//            uploadTime.setText("无数据！");
//        } else {
//            uploadTime.setText(avObject.get("createdAt").toString());
//        }
        if (notice.getTime().equals(null)) {
            time.setText("刚刚");
        } else {
            Date date = (Date)notice.getTime();
            time.setText(TimeUtil.getMyTime(date));
        }
        content.setText(notice.getContent());
        return view;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }
}

