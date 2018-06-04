package com.liangjie.yuxixiaozhushou.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.model.AllNotice;
import com.liangjie.yuxixiaozhushou.model.Notice;

import java.util.List;

/**
 * Created by Administrator on 2017/5/19.
 */
public class GetAllAdapter extends ArrayAdapter<AllNotice> {

    private int resourceId;
    private List<AllNotice> objectList;
    public GetAllAdapter(Context context, int resource, List<AllNotice> objectList) {
        super(context, resource, objectList);
        resourceId = resource;
        Log.e("梁洁", "learningadapter");
        this.objectList = objectList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("梁洁", "getView()");
        AllNotice allNotice = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        TextView time = (TextView) view.findViewById(R.id.tv_time);
        TextView name = (TextView) view.findViewById(R.id.tv_name);
//        if(avObject.get("res_name").equals("")){
//            uploadTime.setText("无数据！");
//        } else {
//            uploadTime.setText(avObject.get("createdAt").toString());
//        }
        content.setText(allNotice.getContent());
        time.setText(allNotice.getTime());
        name.setText(allNotice.getName());
        return view;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }
}


