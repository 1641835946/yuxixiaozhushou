package com.liangjie.yuxixiaozhushou.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.avos.avoscloud.AVObject;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.TimeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/5/12.
 */
public class ReplyAdapter extends ArrayAdapter<AVObject> {

    private int resourceId;
    private List<AVObject> objectList;
    public ReplyAdapter(Context context, int resource, List<AVObject> objectList) {
        super(context, resource, objectList);
        resourceId = resource;
        this.objectList = objectList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AVObject avObject = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView replyName = (TextView) view.findViewById(R.id.comment_name) ;
        TextView replyContent = (TextView) view.findViewById(R.id.comment_content) ;
        TextView replyTime = (TextView) view.findViewById(R.id.comment_time) ;
        ImageView icon = (ImageView)view.findViewById(R.id.iv_icon) ;

        Log.e("梁洁objectList", objectList.toString());
        Log.e("梁洁avObject", avObject.toString());
        replyContent.setText((String)avObject.get("reply_content"));
        if (avObject.get("reply_content").toString().equals("")){
            replyTime.setText("无数据！");
        } else {
            replyName.setText((String)avObject.get("nickname"));
            Date date = (Date)avObject.get("createdAt");
            replyTime.setText(TimeUtil.getMyTime(date));
            try {
                String uriStr = avObject.get("local_uri").toString();
                icon.setImageURI(Uri.parse(uriStr));
            } catch(Exception e) {
                Log.e("梁洁 replyadapter", e.toString());
            }
        }
//        icon.setImageURI((String)avObject.get("upload_id"));
        return view;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }
}
