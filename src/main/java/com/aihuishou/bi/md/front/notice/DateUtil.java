package com.aihuishou.bi.md.front.notice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     *
     * @param target  要比较的日期
     * @return
     * @throws ParseException
     */
    public static boolean isYesterday(Date target) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date current = new Date();
        String todayStr = format.format(current);
        Date today = format.parse(todayStr);
        if((today.getTime() - target.getTime()) > 0 && (today.getTime() - target.getTime()) <= 86400000) {
            return true;
        }
        return false;
    }
}
