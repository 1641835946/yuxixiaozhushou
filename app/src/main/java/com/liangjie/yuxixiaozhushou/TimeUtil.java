package com.liangjie.yuxixiaozhushou;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/5/27.
 */
public class TimeUtil {

    public static String getMyTime(Date date) {
        Date today = Calendar.getInstance().getTime();
        String dateStr = "";
        if (today.getMonth() == date.getMonth() && today.getDate() == date.getDate()) {
            if (date.getHours()<10) {
                dateStr = "今天0"+date.getHours()+":";
            } else {
                dateStr = "今天"+date.getHours() + ":";
            }
            if (date.getMinutes()<10) {
                dateStr = dateStr + "0" + date.getMinutes();
            } else {
                dateStr = dateStr + date.getMinutes();
            }
        } else {
            int mouth = date.getMonth()+1;
            if(date.getMonth()<9) {
                dateStr = "0" + mouth + "月";
            } else {
                dateStr = mouth + "月";
            }
            if (date.getDate()<10) {
                dateStr = dateStr + "0" + date.getDate()+"日";
            } else {
                dateStr = dateStr + date.getDate()+"日";
            }
        }
        return dateStr;
    }
}
