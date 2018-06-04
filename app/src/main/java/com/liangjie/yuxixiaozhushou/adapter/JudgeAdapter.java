package com.liangjie.yuxixiaozhushou.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.model.JudgeProblem;

import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 */
public class JudgeAdapter extends ArrayAdapter<JudgeProblem> {

    private int resourceId;
    private List<JudgeProblem> objectList;
    public JudgeAdapter(Context context, int resource, List<JudgeProblem> objectList) {
        super(context, resource, objectList);
        resourceId = resource;
        this.objectList = objectList;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final JudgeProblem judgeProblem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView problemTv = (TextView) view.findViewById(R.id.tv_single_problem);
        RadioGroup mRadioGroup = (RadioGroup) view.findViewById(R.id.gendergroup);
        RadioButton mRadioA = (RadioButton) view.findViewById(R.id.radioBtn_A);
        RadioButton mRadioB = (RadioButton) view.findViewById(R.id.radioBtn_B);
        problemTv.setText(judgeProblem.getProblem());

        if (judgeProblem.getAnswer() != 0) {
            int answer = judgeProblem.getAnswer();
            if (answer == 1) {
                mRadioA.setChecked(true);
            } else if (answer == 2) {
                mRadioB.setChecked(true);
            }
            int standardAnswer = judgeProblem.getStandardAnswer();
            if (answer != standardAnswer) {
                if (standardAnswer == 1) {
                    mRadioA.setTextColor(Color.RED);
                } else if (standardAnswer == 2) {
                    mRadioB.setTextColor(Color.RED);
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

