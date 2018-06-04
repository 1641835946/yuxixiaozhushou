/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liangjie.yuxixiaozhushou.Sandwich.wizard.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.liangjie.yuxixiaozhushou.R;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.AbstractWizardModel;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.ModelCallbacks;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.Page;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.ReviewItem;
import com.liangjie.yuxixiaozhushou.activity.AnswerActivity;
import com.liangjie.yuxixiaozhushou.model.JudgeProblem;
import com.liangjie.yuxixiaozhushou.model.MultipleFixedChoice;
import com.liangjie.yuxixiaozhushou.model.PaperTemplate;
import com.liangjie.yuxixiaozhushou.model.SingleFixedChoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReviewFragment extends ListFragment implements ModelCallbacks {
    private Callbacks mCallbacks;
    private AbstractWizardModel mWizardModel;
    private List<ReviewItem> mCurrentReviewItems;

    private ReviewAdapter mReviewAdapter;

    public ReviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReviewAdapter = new ReviewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);

        TextView titleView = (TextView) rootView.findViewById(android.R.id.title);
        titleView.setText(R.string.review);
        titleView.setTextColor(getResources().getColor(R.color.review_green));

        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        setListAdapter(mReviewAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks");
        }

        mCallbacks = (Callbacks) activity;

        mWizardModel = mCallbacks.onGetModel();
        mWizardModel.registerListener(this);
        onPageTreeChanged();
    }

    @Override
    public void onPageTreeChanged() {
        onPageDataChanged(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;

        mWizardModel.unregisterListener(this);
    }

    @Override
    public void onPageDataChanged(Page changedPage) {
        ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
        for (Page page : mWizardModel.getCurrentPageSequence()) {
            page.getReviewItems(reviewItems);
        }
        Collections.sort(reviewItems, new Comparator<ReviewItem>() {
            @Override
            public int compare(ReviewItem a, ReviewItem b) {
                return a.getWeight() > b.getWeight() ? +1 : a.getWeight() < b.getWeight() ? -1 : 0;
            }
        });
        mCurrentReviewItems = reviewItems;

        if (mReviewAdapter != null) {
            mReviewAdapter.notifyDataSetInvalidated();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallbacks.onEditScreenAfterReview(mCurrentReviewItems.get(position).getPageKey());
    }

    public interface Callbacks {
        AbstractWizardModel onGetModel();
        void onEditScreenAfterReview(String pageKey);
    }

    private class ReviewAdapter extends BaseAdapter {
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public Object getItem(int position) {
            return mCurrentReviewItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mCurrentReviewItems.get(position).hashCode();
        }

        int paperNum = 0;
        @Override
        public View getView(int position, View view, ViewGroup container) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View rootView = inflater.inflate(R.layout.list_item_review, container, false);

            ReviewItem reviewItem = mCurrentReviewItems.get(position);
            String value = reviewItem.getDisplayValue();
            Log.e("梁洁", "value is " + value);
            if (TextUtils.isEmpty(value)) {
                value = "(None)";
            }
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(reviewItem.getTitle());
            ((TextView) rootView.findViewById(android.R.id.text2)).setText(value);

            //todo "(None)"有可能没有填！！！区分试卷是否做了
            List<PaperTemplate> list = AnswerActivity.testList;
            String problem = reviewItem.getTitle();
            String answer = value;
            Boolean isMatch = false;
            if(problem.equals("试卷")) {
                for (int i = 0; i<list.size(); i++) {
                    if (list.get(i).getName().equals(value)){
                        paperNum = i;
                        AnswerActivity.paperNum = paperNum;
                        isMatch = true;
                        break;
                    }
                }
            }
            if (!isMatch) {
                List<SingleFixedChoice> sList = list.get(paperNum).getsList();
                for (int j = 0; j < sList.size(); j++) {
                    SingleFixedChoice single = sList.get(j);
                    if (single.getProblem().equals(problem)) {
                        String a = single.getA();
                        String b = single.getB();
                        String c = single.getC();
                        String d = single.getD();
                        if (answer.equals(a)) {
                            single.setAnswer(1);
                            isMatch = true;
                            break;
                        } else if (answer.equals(b)) {
                            single.setAnswer(2);
                            isMatch = true;
                            break;
                        } else if (answer.equals(c)) {
                            single.setAnswer(4);
                            isMatch = true;
                            break;
                        } else if (answer.equals(d)) {
                            single.setAnswer(8);
                            isMatch = true;
                            break;
                        }
                    }
                }
            }
            if (!isMatch) {
                List<JudgeProblem> jList = list.get(paperNum).getjList();
                for (int x = 0; x<jList.size(); x++) {
                    JudgeProblem judgeProblem = jList.get(x);
                    if (judgeProblem.getProblem().equals(problem)) {
                        if(answer.equals("正确")) {
                            judgeProblem.setAnswer(1);
                            isMatch = true;
                            break;
                        } else if (answer.equals("错误")) {
                            judgeProblem.setAnswer(2);
                            isMatch = true;
                            break;
                        }
                    }
                }
            }
            if (!isMatch) {
                List<MultipleFixedChoice> mList = list.get(paperNum).getmList();
                for (int z = 0; z<mList.size(); z++) {
                    MultipleFixedChoice multiple = mList.get(z);
                    if(multiple.getProblem().equals(problem)) {
                        String a = multiple.getA();
                        String b = multiple.getB();
                        String c = multiple.getC();
                        String d = multiple.getD();
                        int choice = 0;
                        if (answer.contains(a)) {
                            choice = choice+1;
                        }
                        if (answer.contains(b)) {
                            choice = choice+2;
                        }
                        if (answer.contains(c)) {
                            choice = choice+4;
                        }
                        if (answer.contains(d)) {
                            choice = choice+8;
                        }
                        multiple.setAnswer(choice);
                        break;
                    }
                }
            }
            return rootView;
        }

        @Override
        public int getCount() {
            return mCurrentReviewItems.size();
        }
    }
}
