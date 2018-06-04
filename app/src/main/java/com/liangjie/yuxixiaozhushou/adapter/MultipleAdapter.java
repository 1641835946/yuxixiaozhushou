package com.liangjie.yuxixiaozhushou.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.model.MultipleFixedChoice;

import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 */
public class MultipleAdapter extends ArrayAdapter<MultipleFixedChoice> {

    private int resourceId;
    private List<MultipleFixedChoice> objectList;
    public MultipleAdapter(Context context, int resource, List<MultipleFixedChoice> objectList) {
        super(context, resource, objectList);
        resourceId = resource;
        this.objectList = objectList;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MultipleFixedChoice multipleFixedChoice = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView problemTv = (TextView) view.findViewById(R.id.tv_single_problem);
        CheckBox mCheckBoxA = (CheckBox) view.findViewById(R.id.checkbox_a);
        CheckBox mCheckBoxB = (CheckBox) view.findViewById(R.id.checkbox_b);
        CheckBox mCheckBoxC = (CheckBox) view.findViewById(R.id.checkbox_c);
        CheckBox mCheckBoxD = (CheckBox) view.findViewById(R.id.checkbox_d);
        problemTv.setText(multipleFixedChoice.getProblem());
        mCheckBoxA.setText(multipleFixedChoice.getA());
        mCheckBoxB.setText(multipleFixedChoice.getB());
        mCheckBoxC.setText(multipleFixedChoice.getC());
        mCheckBoxD.setText(multipleFixedChoice.getD());
        if (multipleFixedChoice.getAnswer() != 0) {
            int answer = multipleFixedChoice.getAnswer();
            switch (answer) {
                case 1:
                    mCheckBoxA.setChecked(true);
                    break;
                case 2:
                    mCheckBoxB.setChecked(true);
                    break;
                case 3:
                    mCheckBoxA.setChecked(true);
                    mCheckBoxB.setChecked(true);
                    break;
                case 4:
                    mCheckBoxC.setChecked(true);
                    break;
                case 5:
                    mCheckBoxA.setChecked(true);
                    mCheckBoxC.setChecked(true);
                    break;
                case 6:
                    mCheckBoxB.setChecked(true);
                    mCheckBoxC.setChecked(true);
                    break;
                case 7:
                    mCheckBoxA.setChecked(true);
                    mCheckBoxB.setChecked(true);
                    mCheckBoxC.setChecked(true);
                    break;
                case 8:
                    mCheckBoxD.setChecked(true);
                    break;
                case 9:
                    mCheckBoxA.setChecked(true);
                    mCheckBoxD.setChecked(true);
                    break;
                case 10:
                    mCheckBoxB.setChecked(true);
                    mCheckBoxD.setChecked(true);
                    break;
                case 11:
                    mCheckBoxA.setChecked(true);
                    mCheckBoxB.setChecked(true);
                    mCheckBoxD.setChecked(true);
                    break;
                case 12:
                    mCheckBoxC.setChecked(true);
                    mCheckBoxD.setChecked(true);
                    break;
                case 13:
                    mCheckBoxA.setChecked(true);
                    mCheckBoxC.setChecked(true);
                    mCheckBoxD.setChecked(true);
                    break;
                case 14:
                    mCheckBoxB.setChecked(true);
                    mCheckBoxC.setChecked(true);
                    mCheckBoxD.setChecked(true);
                    break;
                case 15:
                    mCheckBoxA.setChecked(true);
                    mCheckBoxB.setChecked(true);
                    mCheckBoxC.setChecked(true);
                    mCheckBoxD.setChecked(true);
                    break;
            }
            int standardAnswer = multipleFixedChoice.getStandardAnswer();
            if (answer != standardAnswer) {
                switch (standardAnswer) {
                    case 1:
                        mCheckBoxA.setTextColor(Color.RED);
                        break;
                    case 2:
                        mCheckBoxB.setTextColor(Color.RED);
                        break;
                    case 3:
                        mCheckBoxA.setTextColor(Color.RED);
                        mCheckBoxB.setTextColor(Color.RED);
                        break;
                    case 4:
                        mCheckBoxC.setTextColor(Color.RED);
                        break;
                    case 5:
                        mCheckBoxA.setTextColor(Color.RED);
                        mCheckBoxC.setTextColor(Color.RED);
                        break;
                    case 6:
                        mCheckBoxB.setTextColor(Color.RED);
                        mCheckBoxC.setTextColor(Color.RED);
                        break;
                    case 7:
                        mCheckBoxA.setTextColor(Color.RED);
                        mCheckBoxB.setTextColor(Color.RED);
                        mCheckBoxC.setTextColor(Color.RED);
                        break;
                    case 8:
                        mCheckBoxD.setTextColor(Color.RED);
                        break;
                    case 9:
                        mCheckBoxA.setTextColor(Color.RED);
                        mCheckBoxD.setTextColor(Color.RED);
                        break;
                    case 10:
                        mCheckBoxB.setTextColor(Color.RED);
                        mCheckBoxD.setTextColor(Color.RED);
                        break;
                    case 11:
                        mCheckBoxA.setTextColor(Color.RED);
                        mCheckBoxB.setTextColor(Color.RED);
                        mCheckBoxD.setTextColor(Color.RED);
                        break;
                    case 12:
                        mCheckBoxC.setTextColor(Color.RED);
                        mCheckBoxD.setTextColor(Color.RED);
                        break;
                    case 13:
                        mCheckBoxA.setTextColor(Color.RED);
                        mCheckBoxC.setTextColor(Color.RED);
                        mCheckBoxD.setTextColor(Color.RED);
                        break;
                    case 14:
                        mCheckBoxB.setTextColor(Color.RED);
                        mCheckBoxC.setTextColor(Color.RED);
                        mCheckBoxD.setTextColor(Color.RED);
                        break;
                    case 15:
                        mCheckBoxA.setTextColor(Color.RED);
                        mCheckBoxB.setTextColor(Color.RED);
                        mCheckBoxC.setTextColor(Color.RED);
                        mCheckBoxD.setTextColor(Color.RED);
                        break;
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