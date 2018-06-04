package com.liangjie.yuxixiaozhushou.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.model.SingleFixedChoice;

import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 */
public class SingleListAdapter extends ArrayAdapter<SingleFixedChoice> {

    private int resourceId;
    private List<SingleFixedChoice> objectList;
    public SingleListAdapter(Context context, int resource, List<SingleFixedChoice> objectList) {
        super(context, resource, objectList);
        resourceId = resource;
        this.objectList = objectList;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SingleFixedChoice singleFixedChoice = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView problemTv = (TextView) view.findViewById(R.id.tv_single_problem);
        RadioGroup mRadioGroup = (RadioGroup) view.findViewById(R.id.gendergroup);
        RadioButton mRadioA = (RadioButton) view.findViewById(R.id.radioBtn_A);
        RadioButton mRadioB = (RadioButton) view.findViewById(R.id.radioBtn_B);
        RadioButton mRadioC = (RadioButton) view.findViewById(R.id.radioBtn_C);
        RadioButton mRadioD = (RadioButton) view.findViewById(R.id.radioBtn_D);
        problemTv.setText(singleFixedChoice.getProblem());
        mRadioA.setText(singleFixedChoice.getA());
        mRadioB.setText(singleFixedChoice.getB());
        mRadioC.setText(singleFixedChoice.getC());
        mRadioD.setText(singleFixedChoice.getD());
        if (singleFixedChoice.getAnswer() != 0) {
            int answer = singleFixedChoice.getAnswer();
            if (answer == 1) {
                mRadioA.setChecked(true);
            } else if (answer ==2) {
                mRadioB.setChecked(true);
            } else if (answer ==4) {
                mRadioC.setChecked(true);
            } else if (answer ==8) {
                mRadioD.setChecked(true);
            }
            int standardAnswer = singleFixedChoice.getStandardAnswer();
            if (answer != standardAnswer) {
                if (standardAnswer == 1) {
                    mRadioA.setTextColor(Color.RED);
                } else if (standardAnswer ==2) {
                    mRadioB.setTextColor(Color.RED);
                } else if (standardAnswer ==4) {
                    mRadioC.setTextColor(Color.RED);
                } else if (standardAnswer ==8) {
                    mRadioD.setTextColor(Color.RED);
                }
            }
        }
        return view;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }
}
