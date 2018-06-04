package com.liangjie.yuxixiaozhushou;

import android.util.Log;

import com.liangjie.yuxixiaozhushou.Sandwich.wizard.model.MultipleFixedChoicePage;
import com.liangjie.yuxixiaozhushou.model.JudgeProblem;
import com.liangjie.yuxixiaozhushou.model.MultipleFixedChoice;
import com.liangjie.yuxixiaozhushou.model.PaperTemplate;
import com.liangjie.yuxixiaozhushou.model.SingleFixedChoice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 */
public class JSONUtil {

    public static PaperTemplate parseJSONWithJSONObject(String jsonData) {
        PaperTemplate paperTemplate = new PaperTemplate("name");
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            paperTemplate.setName(jsonObject.getString("name"));
            paperTemplate.setCount(jsonObject.getInt("count"));
            paperTemplate.setRightCount(jsonObject.getInt("right_count"));
            paperTemplate.setsList(parseSJSONArray(jsonObject.getString("sList")));
            paperTemplate.setmList(parseMJSONArray(jsonObject.getString("mList")));
            paperTemplate.setjList(parseJJSONArray(jsonObject.getString("jList")));
        } catch(Exception e){
        }
        return paperTemplate;
    }

    public static List<SingleFixedChoice> parseSJSONArray(String jsonData) {
        List<SingleFixedChoice> singleList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0;i<jsonArray.length();i++) {
                Log.e("梁洁3", "json");
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SingleFixedChoice single = new SingleFixedChoice();
                single.setProblem(jsonObject.getString("problem"));
                single.setA(jsonObject.getString("a"));
                single.setB(jsonObject.getString("b"));
                single.setC(jsonObject.getString("c"));
                single.setD(jsonObject.getString("d"));
                single.setStandardAnswer(jsonObject.getInt("standardAnswer"));
                single.setAnswer(jsonObject.getInt("answer"));
                singleList.add(single);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return singleList;
    }

    public static List<MultipleFixedChoice> parseMJSONArray(String jsonData) {
        List<MultipleFixedChoice> multipleList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MultipleFixedChoice multiple = new MultipleFixedChoice();
                multiple.setProblem(jsonObject.getString("problem"));
                multiple.setA(jsonObject.getString("a"));
                multiple.setB(jsonObject.getString("b"));
                multiple.setC(jsonObject.getString("c"));
                multiple.setD(jsonObject.getString("d"));
                multiple.setStandardAnswer(jsonObject.getInt("standardAnswer"));
                multiple.setAnswer(jsonObject.getInt("answer"));
                multipleList.add(multiple);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multipleList;
    }
    public static List<JudgeProblem> parseJJSONArray(String jsonData) {
        List<JudgeProblem> judgeList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JudgeProblem judge = new JudgeProblem();
                judge.setProblem(jsonObject.getString("problem"));
                judge.setStandardAnswer(jsonObject.getInt("standardAnswer"));
                judge.setAnswer(jsonObject.getInt("answer"));
                judgeList.add(judge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return judgeList;
    }

    public static String createJson(PaperTemplate paper) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", paper.getName());
        jsonObject.put("count", paper.getCount());
        jsonObject.put("right_count", paper.getRightCount());

        JSONArray jsonSArray = new JSONArray();
        for (int i = 0; i<paper.getsList().size(); i++) {
            SingleFixedChoice single = paper.getsList().get(i);
            jsonSArray.put(i,createSJson(single));
        }
        jsonObject.put("sList", jsonSArray);

        JSONArray jsonMArray = new JSONArray();
        for (int i = 0; i<paper.getmList().size(); i++) {
            MultipleFixedChoice multiple = paper.getmList().get(i);
            jsonMArray.put(i,createMJson(multiple));
        }
        jsonObject.put("mList", jsonMArray);

        JSONArray jsonJArray = new JSONArray();
        for (int i = 0; i<paper.getjList().size(); i++) {
            JudgeProblem judge = paper.getjList().get(i);
            jsonJArray.put(i,createJJson(judge));
        }
        jsonObject.put("jList", jsonJArray);

        return jsonObject.toString();
    }

    public static JSONObject createSJson(SingleFixedChoice single) throws JSONException {
        JSONObject innerJsonObject = new JSONObject();
        innerJsonObject.put("problem", single.getProblem());
        innerJsonObject.put("a", single.getA());
        innerJsonObject.put("b", single.getB());
        innerJsonObject.put("c", single.getC());
        innerJsonObject.put("d", single.getD());
        innerJsonObject.put("standardAnswer", single.getStandardAnswer());
        innerJsonObject.put("answer", single.getAnswer());
        return innerJsonObject;
    }
    public static JSONObject createMJson(MultipleFixedChoice multiple) throws JSONException {
        JSONObject innerJsonObject = new JSONObject();
        innerJsonObject.put("problem", multiple.getProblem());
        innerJsonObject.put("a", multiple.getA());
        innerJsonObject.put("b", multiple.getB());
        innerJsonObject.put("c", multiple.getC());
        innerJsonObject.put("d", multiple.getD());
        innerJsonObject.put("standardAnswer", multiple.getStandardAnswer());
        innerJsonObject.put("answer", multiple.getAnswer());
        return innerJsonObject;
    }
    public static JSONObject createJJson(JudgeProblem judge) throws JSONException {
        JSONObject innerJsonObject = new JSONObject();
        innerJsonObject.put("problem", judge.getProblem());
        innerJsonObject.put("standardAnswer", judge.getStandardAnswer());
        innerJsonObject.put("answer", judge.getAnswer());
        return innerJsonObject;
    }
}
