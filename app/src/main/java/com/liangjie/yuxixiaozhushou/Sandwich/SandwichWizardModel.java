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

package com.liangjie.yuxixiaozhushou.Sandwich;

import android.content.Context;
import android.util.Log;

import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.AbstractWizardModel;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.BranchPage;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.CustomerInfoPage;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.MultipleFixedChoicePage;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.Page;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.PageList;
import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.SingleFixedChoicePage;
import com.liangjie.yuxixiaozhushou.activity.AnswerActivity;
import com.liangjie.yuxixiaozhushou.model.JudgeProblem;
import com.liangjie.yuxixiaozhushou.model.MultipleFixedChoice;
import com.liangjie.yuxixiaozhushou.model.PaperTemplate;
import com.liangjie.yuxixiaozhushou.model.SingleFixedChoice;

import java.util.ArrayList;
import java.util.List;

public class SandwichWizardModel extends AbstractWizardModel {
    public SandwichWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        PageList pageList = new PageList();
        BranchPage branchPage = new BranchPage(this, "试卷");
        for (int i = 0; i<AnswerActivity.testList.size(); i++) {//每张卷子
            PageList childPageList = new PageList();
            for (int j = 0; j<AnswerActivity.testList.get(i).getsList().size(); j++) {
                SingleFixedChoice singleFixedChoice = AnswerActivity.testList.get(i).getsList().get(j);
                Page page = new SingleFixedChoicePage(SandwichWizardModel.this, singleFixedChoice.getProblem())
                        .setChoices(singleFixedChoice.getA(),
                                singleFixedChoice.getB(),
                                singleFixedChoice.getC(),
                                singleFixedChoice.getD());
                page.setParentKey(AnswerActivity.testList.get(i).getName());
                childPageList.add(page);
            }
            for (int j = 0; j<AnswerActivity.testList.get(i).getmList().size(); j++) {
                MultipleFixedChoice multipleFixedChoice = AnswerActivity.testList.get(i).getmList().get(j);
                Page page = new MultipleFixedChoicePage(SandwichWizardModel.this, multipleFixedChoice.getProblem())
                        .setChoices(multipleFixedChoice.getA(),
                                multipleFixedChoice.getB(),
                                multipleFixedChoice.getC(),
                                multipleFixedChoice.getD());
                page.setParentKey(AnswerActivity.testList.get(i).getName());
                childPageList.add(page);
            }
            for (int j = 0; j<AnswerActivity.testList.get(i).getjList().size(); j++) {
                JudgeProblem judgeProblem = AnswerActivity.testList.get(i).getjList().get(j);
                Page page = new SingleFixedChoicePage(SandwichWizardModel.this, judgeProblem.getProblem())
                        .setChoices("正确", "错误");
                page.setParentKey(AnswerActivity.testList.get(i).getName());
                childPageList.add(page);
            }
            branchPage.addBranch(AnswerActivity.testList.get(i).getName(), childPageList);
        }
        pageList.add(branchPage);
        return pageList;
    }
}
