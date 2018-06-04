package com.liangjie.yuxixiaozhushou.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.TimeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/11.
 */
public class LearningAdapter extends ArrayAdapter<AVObject>{

    private int resourceId;
    private List<AVObject> objectList;
    public LearningAdapter(Context context, int resource, List<AVObject> objectList) {
        super(context, resource, objectList);
        resourceId = resource;
        Log.e("梁洁", "learningadapter");
        this.objectList = objectList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("梁洁", "getView()");
        AVObject avObject = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView resNameTv = (TextView) view.findViewById(R.id.tv_res_name);
        TextView uploadTime = (TextView) view.findViewById(R.id.tv_upload_time);
        TextView uploadId = (TextView) view.findViewById(R.id.tv_upload_id);
        ImageView iconIv = (ImageView) view.findViewById(R.id.iv_icon);
        Log.e("梁洁", (String)avObject.get("res_name"));
        if(avObject.get("res_name").equals("")){
            uploadTime.setText("无数据！");
        } else {
            Date date = (Date)avObject.get("createdAt");
            uploadTime.setText(TimeUtil.getMyTime(date));
            String fileName = avObject.get("res_name").toString();
            String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
            Log.e("梁洁", prefix);
            resNameTv.setText(fileName);
            if (prefix.equals("pdf")) {
                iconIv.setImageResource(R.drawable.pdf);
            } else if (prefix.equals("ppt") || prefix.equals("pptx")) {
                iconIv.setImageResource(R.drawable.ppt);
            } else if (prefix.equals("docx") || prefix.equals("doc")) {
                iconIv.setImageResource(R.drawable.word);
            } else if (prefix.equals("txt")) {
                iconIv.setImageResource(R.drawable.txt);
            }
            uploadId.setText((String)avObject.get("upload_id"));
        }
        return view;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }
}
